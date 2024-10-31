package org.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.homework.conventioin.result.Result;
import org.homework.pojo.dto.ItemReportDTO;
import org.homework.pojo.dto.MaterialUpdateDTO;
import org.homework.pojo.po.Material;
import org.homework.pojo.vo.MaterialTypeVo;

import java.util.List;

/**
 * @author zhanghaifeng
 * @description 针对表【materials】的数据库操作Service
 * @createDate 2024-09-19 10:26:13
 */
public interface MaterialService extends IService<Material> {

    /**
     * 根据分页信息获取材料列表
     * 该方法用于处理材料数据的分页查询，可以根据当前页码和页面大小来获取相应的材料信息
     * 此外，它还支持对查询结果进行额外的过滤和搜索
     *
     * @param current    当前页码，表示请求的材料页面序号
     * @param size       页面大小，表示每页包含的材料数量
     * @param filterNull 是否过滤空值，如果为true，则从结果中排除空材料信息
     * @param searchText 搜索文本，如果提供，则对材料信息进行模糊
     * @return 返回一个Result对象，其中包含根据分页和过滤条件查询到的材料信息
     */
    Result getMaterialByPage(int current, int size, boolean filterNull, String searchText);

    /**
     * 获取材料类型列表
     * 该方法用于获取所有材料类型的列表，帮助用户了解系统中有哪些不同类型的材料
     *
     * @return 返回一个Result对象，包含所有材料类型的信息
     */
    Result<List<MaterialTypeVo>> getTypeList();

    /**
     * 添加材料类型
     * 本函数通过接收一个JSON格式的字符串来添加新的材料类型该字符串包含了新材料类型的所有必要信息
     *
     * @param materialJSON 包含新材料类型信息的JSON字符串
     * @return 返回一个Result对象，表示添加操作的结果
     */
    Result addMaterialType(String materialJSON);

    /**
     * 更新物料信息
     * 超管可对此函数进行调用，用于修改物料信息，包括物料名称、物料类型、物料描述等。
     *
     * @param requestParam 包含更新物料信息的请求参数
     * @return 返回一个Result对象，表示更新操作的结果
     */

    Result updateMaterial(MaterialUpdateDTO requestParam);

    /**
     * 删除材料信息
     *
     * @param id 材料ID
     * @return 返回一个Result对象，表示删除操作的结果
     */
    Result deleteMaterialById(Long id);

    /**
     * 通过物料ID和年份查询物料报表
     *
     * @param materialId 物料ID
     * @param year       年份
     * @return 返回一个Result对象，包含物料报表列表
     */
    List<ItemReportDTO> queryOrderByMaterialIdAndYear(Long materialId, String year);

}
