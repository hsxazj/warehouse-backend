package org.homework.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.BatchResult;
import org.homework.conventioin.result.Result;
import org.homework.enums.OperationType;
import org.homework.mapper.MaterialMapper;
import org.homework.mapper.OrderMaterialMapper;
import org.homework.pojo.dto.ItemReportDTO;
import org.homework.pojo.dto.MaterialUpdateDTO;
import org.homework.pojo.po.Material;
import org.homework.pojo.po.OrderMaterial;
import org.homework.pojo.vo.MaterialTypeVo;
import org.homework.service.MaterialService;
import org.homework.utils.MQUtil;
import org.homework.utils.RedisUtil;
import org.homework.utils.ScanDelType;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author zhanghaifeng
 * @description 针对表【materials】的数据库操作Service实现
 * @createDate 2024-09-19 10:26:13
 */
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material>
        implements MaterialService {

    private final MaterialMapper materialsMapper;

    private final MQUtil mqUtil;

    private final RedisUtil redisUtil;

    private final RedissonClient redissonClient;
    private final OrderMaterialMapper orderMaterialMapper;

    @Override
    public Result getMaterialByPage(int current, int size, boolean filterNull, String searchText) {
        QueryWrapper<Material> materialsQueryWrapper = new QueryWrapper<>();
        if (filterNull) {
            materialsQueryWrapper.gt("stock", 0);
        }
        if (StringUtils.hasText(searchText)) {
            materialsQueryWrapper.like("name", searchText);
        }
        Page<Material> materialsPage = materialsMapper.selectPage(new Page<>(current, size), materialsQueryWrapper);
        HashMap<String, Object> map = new HashMap<>();
        map.put("data", materialsPage.getRecords());
        map.put("total", materialsPage.getTotal());
        return Result.success(map);
    }


    @Override
    public Result<List<MaterialTypeVo>> getTypeList() {
        // 定义变量，用于存储材质类型信息列表
        // 先尝试从Redis缓存中获取材质类型列表
        List<MaterialTypeVo> materialTypeList = redisUtil.getList("materialTypeList", MaterialTypeVo.class);

        // 如果获取的列表为空，则尝试加锁后再次从Redis获取，若仍为空，则从数据库获取并存入Redis
        if (CollectionUtils.isEmpty(materialTypeList)) {
            // 使用Redisson的分布式锁，确保并发时数据的一致性
            RLock getMaterialTypeList = redissonClient.getLock("getMaterialTypeList");
            try {
                // 加锁，防止并发问题
                getMaterialTypeList.lock();

                // 再次尝试从Redis获取，因为其他线程可能已经更新了缓存
                materialTypeList = redisUtil.getList("materialTypeList", MaterialTypeVo.class);

                // 如果Redis中仍然没有数据，则从数据库中获取
                if (CollectionUtils.isEmpty(materialTypeList)) {
                    materialTypeList = materialsMapper.getTypeList();
                    // 更新Redis缓存，以便后续请求可以直接使用
                    redisUtil.setList("materialTypeList", materialTypeList);
                }
            } finally {
                // 解锁，确保锁会被释放，避免死锁
                getMaterialTypeList.unlock();
            }
        }
        // 返回成功的结果，携带材质类型列表
        return Result.success(materialTypeList);
    }

    @Override
    public Result addMaterialType(String materialJSON) {
        List<Material> materialList = null;
        try {
            materialList = JSON.parseObject(materialJSON, new TypeReference<>() {
            });
        } catch (Exception e) {
            Result.fail("请检查JSON格式");
        }

        if (CollectionUtils.isEmpty(materialList)) {
            return Result.fail("列表为空");
        }

        List<BatchResult> insert = materialsMapper.insert(materialList);

        // 删除redis缓存
        redisUtil.delByScan("materialTypeList", ScanDelType.LIST);

        // 拼接操作日志
        StringBuffer sb = new StringBuffer("添加了新的物料类型，具体如下:");
        for (int i = 0; i < materialList.size(); i++) {
            sb.append(materialList.get(i).getName());
            if (i < materialList.size() - 1) {
                sb.append("、");
            }
        }

        // 发送操作日志
        mqUtil.sendOperationLogM(OperationType.MATERIALS, sb.toString());

        return Result.success();
    }

    @Override
    public Result updateMaterial(MaterialUpdateDTO materialUpdateDTO) {
        LambdaQueryWrapper<Material> lambdaQueryWrapper = Wrappers.lambdaQuery(Material.class)
                .eq(Material::getId, materialUpdateDTO.getId());
        Material material = materialsMapper.selectOne(lambdaQueryWrapper);
        if (Objects.isNull(material)) {
            return Result.fail("物料不存在");
        }
        Material updateMaterial = Material.builder()
                .name(materialUpdateDTO.getName())
                .stock(materialUpdateDTO.getStock())
                .specification(materialUpdateDTO.getSpecification())
                .unit(StrUtil.isBlank(materialUpdateDTO.getUnit()) ? null : materialUpdateDTO.getUnit())
                .remark(StrUtil.isBlank(materialUpdateDTO.getRemark()) ? null : materialUpdateDTO.getRemark())
                .build();
        int updateFlag = baseMapper.update(updateMaterial, lambdaQueryWrapper);
        if (updateFlag != 1) {
            return Result.fail("更新失败");
        }
        mqUtil.sendOperationLogM(OperationType.MATERIALS,
                "修改了id为：" + material.getId() + "的物料信息");
        return Result.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteMaterialById(Long id) {
        LambdaQueryWrapper<Material> materialLambdaQueryWrapper = Wrappers.lambdaQuery(Material.class)
                .eq(Material::getId, id);
        Material material = materialsMapper.selectOne(materialLambdaQueryWrapper);
        if (material == null) {
            return Result.fail("物料不存在");
        }
        int materialDelFlag = materialsMapper.deleteById(material);
        if (materialDelFlag != 1) {
            return Result.fail("删除失败");
        }
        LambdaQueryWrapper<OrderMaterial> orderMaterialLambdaQueryWrapper = Wrappers.lambdaQuery(OrderMaterial.class)
                .eq(OrderMaterial::getMaterialId, id);
        orderMaterialMapper.delete(orderMaterialLambdaQueryWrapper);
        mqUtil.sendOperationLogM(OperationType.MATERIALS,
                "删除了名称为为："
                        + material.getName()
                        + "的物料");
        return Result.success("删除成功");
    }

    public List<ItemReportDTO> queryOrderByMaterialIdAndYear(Long materialId, String year) {
        List<ItemReportDTO> itemReports = materialsMapper.queryOrderByMaterialIdAndYear(materialId, year);
        return itemReports;
    }
}




