package org.homework.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import lombok.Data;

import java.util.Date;

/**
 * 订单详情 Excel 模型
 */
@Data
@HeadStyle(fillForegroundColor = 13, fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND)
@ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
@HeadRowHeight(50)
@ContentRowHeight(30)
public class OrderDetailsExcel {

    @ExcelProperty({"订单详情表", "订单ID"})
    private Long orderId;

    @ExcelProperty({"订单详情表", "提交时间"})
    private Date submitTime;

    @ExcelProperty({"订单详情表", "出入库状态"})
    private String inOutStatus;
    @ColumnWidth(150)
    @ExcelProperty({"订单详情表", "变动情况"})
    private String variation;
}
