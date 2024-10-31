package org.homework.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.homework.conventioin.result.Result;
import org.homework.pojo.dto.SubmitOrderDto;
import org.homework.pojo.po.Order;
import org.homework.pojo.vo.OrderDetailsVO;

import java.util.List;

/**
 * @author zhanghaifeng
 * @description 针对表【Order】的数据库操作Service
 * @createDate 2024-09-26 11:07:06
 */
public interface OrderService extends IService<Order> {

    /**
     * 提交订单
     *
     * @param submitOrderDto 包含订单提交信息的数据传输对象
     * @return 返回订单提交的结果
     */
    Result submitOrder(SubmitOrderDto submitOrderDto) throws InterruptedException;

    /**
     * 审核订单
     *
     * @param orderId 订单ID
     * @param status  订单审核状态
     * @return 返回订单审核结果
     */
    Result auditOrder(Long orderId, Integer status);

    /**
     * 根据订单状态获取订单列表
     *
     * @param status 订单状态，例如“待付款”、“已发货”等
     * @return 返回包含符合指定状态的订单列表的结果对象
     */
    Result getOrderListByStatus(String status);


    /**
     * 获取订单列表
     * <p>
     * 根据订单状态、当前页码和页面大小来获取订单列表
     *
     * @param status      订单状态，用于筛选订单，例如"已付款"、"待发货"等
     * @param currentPage 当前页码，用于分页查询
     * @param pageSize    页面大小，表示每页返回的订单数量
     * @return 返回一个Result对象，包含查询结果
     */
    Result getOrderList(String status, Integer currentPage, Integer pageSize);

    /**
     * 获取订单详细信息
     *
     * @param orderId 订单ID，用于唯一标识一个订单
     * @return 返回包含指定订单详细信息的结果对象
     */
    Result getOrderDetail(Long orderId);

    /**
     * 根据年月获取订单列表
     *
     * @param year  年份
     * @param month 月份
     * @return 订单详情列表
     */
    List<OrderDetailsVO> getOrderListByPram(String year, String month);

}
