package org.homework.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.homework.conventioin.result.Result;
import org.homework.pojo.dto.SubmitOrderDto;
import org.homework.pojo.vo.OrderDetailsVO;
import org.homework.service.OrderService;
import org.homework.service.impl.ExcelExportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单相关
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ExcelExportService excelExportService;


    /**
     * 提交订单
     */
    @PreAuthorize("hasAuthority('order:submit')")
    @PostMapping("/submitOrder")
    Result submitIncomingOrder(@RequestBody SubmitOrderDto submitOrderDto) throws InterruptedException {
        return orderService.submitOrder(submitOrderDto);
    }

    /**
     * 审核订单
     */
    @PreAuthorize("hasAuthority('order:audit')")
    @PutMapping("/auditOrder")
    Result auditOrder(@RequestParam(value = "orderId", required = false) Long orderId,
                      @RequestParam(value = "status", required = false) Integer status) {
        return orderService.auditOrder(orderId, status);
    }

    /**
     * 根据传入的status来获取订单列表
     * 0为未审核 1为已审批 2为拒绝
     *
     * @return 订单列表
     */
    @GetMapping("/getOrderListByStatus")
    Result getOrderListByStatus(@RequestParam("status") String status) {
        return orderService.getOrderListByStatus(status);
    }

    /**
     * 通过传入的status获取订单列表（不包括物料信息）
     */
    @PreAuthorize("hasAuthority('order:audit')")
    @GetMapping("/getOrderList")
    Result getOrderList(@RequestParam("status") String status,
                        @RequestParam("currentPage") Integer currentPage,
                        @RequestParam("pageSize") Integer pageSize) {
        return orderService.getOrderList(status, currentPage, pageSize);
    }

    /**
     * 获取单个订单详情
     */
    @PreAuthorize("hasAuthority('order:audit')")
    @GetMapping("/getOrderDetail")
    Result getOrderDetailByOrderId(@RequestParam(value = "orderId", required = false) Long orderId) {
        return orderService.getOrderDetail(orderId);
    }

    /**
     * 获取指定年月下的审核通过的订单列表
     *
     * @param year  年份
     * @param month 月份
     * @return 订单列表
     */
    @PreAuthorize("hasAuthority('statistic:permission')")
    @GetMapping("/getOrderByMonth")
    Result getOrderByPram(@RequestParam(value = "year") String year,
                          @RequestParam(value = "month") String month) {
        return Result.success(orderService.getOrderListByPram(year, month));
    }

    /**
     * 根据年份和月份导出订单详情到 Excel
     *
     * @param year     年份
     * @param month    月份
     * @param response HTTP 响应
     */
    @PreAuthorize("hasAuthority('statistic:permission')")
    @GetMapping("/exportByMonth")
    public void exportOrdersByMonth(@RequestParam("year") String year,
                                    @RequestParam("month") String month,
                                    HttpServletResponse response) {
        List<OrderDetailsVO> orders = orderService.getOrderListByPram(year, month);
        excelExportService.exportOrdersToExcel(orders, response);
    }
}
