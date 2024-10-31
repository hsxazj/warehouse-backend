package org.homework.handler;

import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.homework.pojo.excel.OrderRow;
import org.springframework.stereotype.Component;

@Component
public class CustomSheetWriteHandler implements SheetWriteHandler {

    @Override
    public void beforeSheetCreate(com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder writeWorkbookHolder, com.alibaba.excel.write.metadata.holder.WriteSheetHolder writeSheetHolder) {
        // 可选：在创建 sheet 前执行
    }

    @Override
    public void afterSheetCreate(com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder writeWorkbookHolder, com.alibaba.excel.write.metadata.holder.WriteSheetHolder writeSheetHolder) {
        // 可选：在创建 sheet 后执行
    }


    public void afterRowDispose(WriteSheetHolder writeSheetHolder, Row row, Object data, CellData cellData, WriteDirectionEnum writeDirectionEnum) {
        if (data instanceof OrderRow) {
            Sheet sheet = writeSheetHolder.getSheet();
            // 设置订单行样式，例如加粗字体
            CellStyle style = writeSheetHolder.getSheet().getWorkbook().createCellStyle();
            Font font = writeSheetHolder.getSheet().getWorkbook().createFont();
            font.setBold(true);
            style.setFont(font);
            row.getCell(0).setCellStyle(style);
        }
    }
}
