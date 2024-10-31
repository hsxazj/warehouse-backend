package org.homework.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import lombok.Data;

import java.util.Date;

/**
 * 库存账单Excel实体类
 */
@HeadStyle(fillForegroundColor = 13, fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND)
@ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
@Data
public class WareHouseLedgerExcel {
    /**
     * 仓库ID
     */
    @ExcelProperty({"库存账单表", "物料ID"})
    private Long materialId;
    /**
     * 物料名称
     */
    @ExcelProperty({"库存账单表", "物料名称"})
    private String materialName;
    /**
     * 物料规格
     */
    @ExcelProperty({"库存账单表", "物料规格"})
    private String materialSpecification;
    /**
     * 物料单位
     */
    @ExcelProperty({"库存账单表", "单位"})
    private String materialUnit;
    /**
     * 提交时间
     */
    @ExcelProperty({"库存账单表", "提交时间"})
    private Date submitTime;
    /**
     * 入库数量
     */
    @ExcelProperty({"库存账单表", "入库数量"})
    private Long totalIn;
    /**
     * 出库数量
     */
    @ExcelProperty({"库存账单表", "出库数量"})
    private Long totalOut;
}
