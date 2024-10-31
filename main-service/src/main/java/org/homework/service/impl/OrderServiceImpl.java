package org.homework.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.homework.conventioin.exception.ServiceException;
import org.homework.conventioin.result.Result;
import org.homework.enums.OperationType;
import org.homework.mapper.MaterialMapper;
import org.homework.mapper.OrderMapper;
import org.homework.mapper.OrderMaterialMapper;
import org.homework.pojo.bo.LoginUser;
import org.homework.pojo.bo.MaterialStockCheck;
import org.homework.pojo.dto.OrderFlatDTO;
import org.homework.pojo.dto.SubmitOrderDto;
import org.homework.pojo.po.Material;
import org.homework.pojo.po.Order;
import org.homework.pojo.po.OrderMaterial;
import org.homework.pojo.po.User;
import org.homework.pojo.vo.MaterialTypeVo;
import org.homework.pojo.vo.OrderDetailsVO;
import org.homework.pojo.vo.OrderMaterialVO;
import org.homework.service.MaterialService;
import org.homework.service.OrderService;
import org.homework.utils.MQUtil;
import org.homework.utils.SecurityInfoUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhanghaifeng
 * @description 针对表【Order】的数据库操作Service实现
 * @createDate 2024-09-26 11:07:06
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService {
    
    private final MQUtil mqUtil;
    
    private final OrderMaterialMapper orderMaterialMapper;
    
    private final MaterialService materialService;
    
    private final OrderMapper orderMapper;
    
    private final RedissonClient redissonClient;
    
    private final MaterialMapper materialMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result submitOrder(SubmitOrderDto submitOrderDto) throws InterruptedException {
        // 获取当前操作员的ID
        Long adminId = SecurityInfoUtil.getUserId();
        
        // 加锁
        RLock lock = redissonClient.getLock("submitOrder:" + adminId);
        boolean isSuccess = false;
        try {
            // 尝试获取锁
            isSuccess = lock.tryLock(-1, TimeUnit.MILLISECONDS);
            if (!isSuccess) {
                return Result.fail("订单正在处理中，请勿重复提交");
            }
            // 获取当前登录的管理员信息
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = loginUser.getUser();
            Long userId = user.getId();
            // 构建订单信息
            Order order = Order.builder()
                    .userId(userId)
                    .inOut(submitOrderDto.getInOut())
                    .submitTime(new Date()).build();
            // 保存订单
            int save = orderMapper.insert(order);
            if (save != 1) {
                // 如果保存失败，返回错误信息
                return Result.fail("提交失败");
            }
            // 分析订单信息map  id 数量
            Map<Long, Long> materialMap = JSON.parseObject(
                    submitOrderDto.getMaterialMap(), new TypeReference<>() {
                    });
            Set<Long> materialIdSet = materialMap.keySet();
            // 检查物料id是否合法
            Result<List<MaterialTypeVo>> typeList = materialService.getTypeList();
            List<MaterialTypeVo> materialTypeList = typeList.getData();
            Set<Long> idList = materialTypeList.stream().map(MaterialTypeVo::getMaterialId).collect(Collectors.toSet());
            if (!idList.containsAll(materialIdSet)) {
                throw new ServiceException("订单中含有未知的物品，请检查，如有需要，请添加");
            }
            // 构建订单物料列表
            List<OrderMaterial> orderMaterialList = new ArrayList<>();
            materialMap.forEach(
                    (materialId, quantity) -> orderMaterialList.add(OrderMaterial.builder()
                            .materialId(materialId)
                            .quantity(quantity)
                            .orderId(order.getId())
                            .build())
            );
            // 插入列表
            orderMaterialMapper.insert(orderMaterialList);
            // 获取订单类型（入库或出库）
            String inOut = submitOrderDto.getInOut().equals(0) ? "入库" : "出库";
            // 发送操作日志
            mqUtil.sendOperationLogM(
                    OperationType.MATERIALS,
                    "提交了" + inOut + "订单，订单id: " + order.getId());
        } finally {
            if (isSuccess && lock.isHeldByCurrentThread()) {
                // 释放锁
                lock.unlock();
            }
        }
        // 返回成功信息
        return Result.success("提交成功");
    }
    
    
    @Override
    public Result auditOrder(Long orderId, Integer status) {
        // 参数校验
        Preconditions.checkArgument(orderId != null, "订单id不能为空");
        Preconditions.checkArgument(status != null, "订单状态不能为空");
        
        RLock lock = redissonClient.getLock("auditOrder:" + orderId);
        
        // 根据状态码转换状态字符串
        String statusStr = "审核";
        if (status.equals(1)) {
            statusStr = "通过";
        } else if (status.equals(2)) {
            statusStr = "拒绝";
        }
        
        try {
            // 加锁以确保并发控制
            lock.lock();
            // 获取当前登录的管理员信息
            User user = SecurityInfoUtil.getUser();
            Long id = user.getId();
            // 根据订单ID查询订单详情
            Order order = orderMapper.selectById(orderId);
            // 检查订单状态，如果已处理则不允许再次处理
            if (!order.getStatus().equals(0)) {
                // 发送操作日志消息
                mqUtil.sendOperationLogM(
                        OperationType.MATERIALS,
                        "尝试" + statusStr + "订单, 订单id为:" + orderId + "，由于订单已被处理，操作失败");
                return Result.fail("订单已处理，无法再次处理");
            }
            
            // 根据订单ID获取库存检查列表
            List<MaterialStockCheck> materialStockCheckList = materialMapper.getStock(orderId);
            
            List<Material> materialList = new ArrayList<>();
            
            // 判断订单类型，如果是出库订单则进行库存检查
            if (order.getInOut().equals(1)) {
                // 出库
                // 检查库存是否满足需求
                List<String> insufficientList = new ArrayList<>();
                for (MaterialStockCheck materialStockCheck : materialStockCheckList) {
                    // 如果库存不足，记录不足的物料名称
                    if (materialStockCheck.getStock() < materialStockCheck.getQuantity()) {
                        insufficientList.add(materialStockCheck.getName());
                        continue;
                    }
                    // 计算更新后的库存，并添加到物料列表中
                    Material material = new Material();
                    material.setId(materialStockCheck.getId());
                    material.setStock(materialStockCheck.getStock() - materialStockCheck.getQuantity());
                    materialList.add(material);
                }
                // 如果存在库存不足的情况，返回错误信息
                if (insufficientList.size() > 0) {
                    mqUtil.sendOperationLogM(
                            OperationType.MATERIALS,
                            "尝试" + statusStr + "订单, 订单id为:" + orderId + "，由于库存不足，操作失败");
                    return Result.fail("库存不足", insufficientList);
                }
            } else {
                // 入库
                // 直接增加库存，无需检查
                for (MaterialStockCheck materialStockCheck : materialStockCheckList) {
                    Material material = new Material();
                    material.setId(materialStockCheck.getId());
                    material.setStock(materialStockCheck.getStock() + materialStockCheck.getQuantity());
                    materialList.add(material);
                }
            }
            
            // 更新库存信息
            materialMapper.updateById(materialList);
            
            // 更新订单状态
            int update = orderMapper.update(null, new LambdaUpdateWrapper<Order>()
                    .eq(Order::getId, orderId)
                    .set(Order::getStatus, status)
                    .set(Order::getAdminId, id)
                    .set(Order::getConfirmTime, new Date(System.currentTimeMillis()))
            );
            // 如果更新失败，返回失败信息
            if (update <= 0) {
                return Result.fail("操作失败");
            }
        } finally {
            // 释放锁
            lock.unlock();
        }
        
        // 发送操作日志消息
        mqUtil.sendOperationLogM(
                OperationType.MATERIALS,
                statusStr + "了订单, 订单id为:" + orderId);
        
        // 返回成功结果
        return Result.success();
    }
    
    @Override
    public Result getOrderListByStatus(String status) {
        Preconditions.checkArgument(status != null, "订单状态不能为空");
        List<OrderFlatDTO> orderFlatDTOS = orderMapper.selectOrdersWithMaterials(status);
        //根据orderId来分组
        Map<Long, List<OrderFlatDTO>> orderMapById = orderFlatDTOS.stream().collect(Collectors.groupingBy(OrderFlatDTO::getOrderId));
        List<OrderDetailsVO> orderDetailsVOList = new ArrayList<>();
        //根据orderId来构建OrderDetailsVO
        orderMapById.forEach((orderId, orderFlatDTOList) -> {
            OrderDetailsVO orderDetailsVO = OrderDetailsVO.builder()
                    .orderId(orderId)
                    .orderInOut(orderFlatDTOList.get(0).getOrderInOut())
                    .submitTime(orderFlatDTOList.get(0).getSubmitTime())
                    .userId(orderFlatDTOList.get(0).getUserId())
                    .status(orderFlatDTOList.get(0).getStatus())
                    .build();
            ArrayList<OrderMaterialVO> orderMaterialVOList = new ArrayList<>();
            for (OrderFlatDTO orderFlatDTO : orderFlatDTOList) {
                OrderMaterialVO orderMaterialVO = BeanUtil.copyProperties(orderFlatDTO, OrderMaterialVO.class);
                orderMaterialVOList.add(orderMaterialVO);
            }
            orderDetailsVO.setOrderMaterialVOList(orderMaterialVOList);
            orderDetailsVOList.add(orderDetailsVO);
        });
        return Result.success(orderDetailsVOList);
    }
    
    @Override
    public Result getOrderList(String status, Integer currentPage, Integer pageSize) {
        LambdaQueryWrapper<Order> queryWrapper = Wrappers.lambdaQuery(Order.class).eq(Order::getStatus, status)
                .orderByDesc(Order::getSubmitTime);
        Page<Order> orderPage = orderMapper.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
        long pages = orderPage.getPages();
        List<Order> records = orderPage.getRecords();
        HashMap<String, Object> map = new HashMap<>();
        map.put("totalPage", pages);
        map.put("data", records);
        return Result.success(map);
    }
    
    @Override
    public Result getOrderDetail(Long orderId) {
        Preconditions.checkArgument(orderId != null, "订单ID不能为空");
        // 根据订单ID查询订单详情
        List<OrderFlatDTO> orderFlatDTOList = orderMapper.selectOrdersDetail(orderId);
        // 根据orderId来构建OrderDetailsVO
        OrderDetailsVO orderDetailsVO = OrderDetailsVO.builder()
                .status(orderFlatDTOList.get(0).getStatus())
                .userId(orderFlatDTOList.get(0).getUserId())
                .orderInOut(orderFlatDTOList.get(0).getOrderInOut())
                .submitTime(orderFlatDTOList.get(0).getSubmitTime())
                .orderId(orderId)
                .build();
        ArrayList<OrderMaterialVO> orderMaterialVOList = new ArrayList<>();
        for (OrderFlatDTO orderFlatDTO : orderFlatDTOList) {
            OrderMaterialVO orderMaterialVO = BeanUtil.copyProperties(orderFlatDTO, OrderMaterialVO.class);
            orderMaterialVOList.add(orderMaterialVO);
        }
        orderDetailsVO.setOrderMaterialVOList(orderMaterialVOList);
        return Result.success(orderDetailsVO);
    }
    
    private OrderDetailsVO convertToOrderDetailsVO(List<OrderFlatDTO> orderFlatDTOList) {
        // 根据orderId来构建OrderDetailsVO
        OrderDetailsVO orderDetailsVO = OrderDetailsVO.builder()
                .status(orderFlatDTOList.get(0).getStatus())
                .userId(orderFlatDTOList.get(0).getUserId())
                .orderInOut(orderFlatDTOList.get(0).getOrderInOut())
                .submitTime(orderFlatDTOList.get(0).getSubmitTime())
                .orderId(orderFlatDTOList.get(0).getOrderId())
                .build();
        ArrayList<OrderMaterialVO> orderMaterialVOList = new ArrayList<>();
        StringBuffer variation = new StringBuffer();
        for (OrderFlatDTO orderFlatDTO : orderFlatDTOList) {
            OrderMaterialVO orderMaterialVO = BeanUtil.copyProperties(orderFlatDTO, OrderMaterialVO.class);
            variation.append("编号: ")
                    .append(orderFlatDTO.getMaterialId())
                    .append(" ")
                    .append("名称: ")
                    .append(orderFlatDTO.getMaterialName())
                    .append(" ")
                    .append(orderFlatDTO.getVariation())
                    .append("\n");
            orderMaterialVOList.add(orderMaterialVO);
        }
        variation.deleteCharAt(variation.length() - 1);
        orderDetailsVO.setVariation(variation.toString());
        orderDetailsVO.setOrderMaterialVOList(orderMaterialVOList);
        return orderDetailsVO;
    }
    
    @Override
    public List<OrderDetailsVO> getOrderListByPram(String year, String month) {
        Preconditions.checkArgument(year != null, "年份不能为空");
        Preconditions.checkArgument(month != null, "月份不能为空");
        // 根据年月获取订单列表
        List<OrderFlatDTO> orderFlatDTOS = orderMapper.selectOrderDetailsByMonth(month, year);
        //根据orderId来分组
        Map<Long, List<OrderFlatDTO>> orderMapById = orderFlatDTOS.stream().collect(Collectors.groupingBy(OrderFlatDTO::getOrderId));
        List<OrderDetailsVO> orderDetailsVOList = new ArrayList<>();
        //转化为订单详情VO
        orderMapById.forEach((orderId, orderFlatDTOList) -> {
            OrderDetailsVO orderDetailsVO = convertToOrderDetailsVO(orderFlatDTOList);
            orderDetailsVOList.add(orderDetailsVO);
        });
        return orderDetailsVOList;
    }
    
    
}




