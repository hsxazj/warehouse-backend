package org.homework.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 订单行 Excel 模型
 */
@Data
public class OrderRow {
    @ExcelProperty("订单ID")
    private Long orderId;

    public OrderRow(Long orderId) {
        this.orderId = orderId;
    }
}