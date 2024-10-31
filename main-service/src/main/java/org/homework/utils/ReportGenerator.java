package org.homework.utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.homework.pojo.dto.ItemReportDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 报表工具生成类
 */
public class ReportGenerator {
    public static byte[] generateExcelReport(List<ItemReportDTO> itemReportList, String year) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventory Report " + year);

        // 创建报表头
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("物料代码");
        headerRow.createCell(1).setCellValue("名称");
        headerRow.createCell(2).setCellValue("规格");
        headerRow.createCell(3).setCellValue("计量单位");
        headerRow.createCell(4).setCellValue("日期");
        headerRow.createCell(5).setCellValue("进仓量");
        headerRow.createCell(6).setCellValue("出仓量");

        // 填充数据
        int rowNum = 1;
        int stock = 0;
        for (ItemReportDTO itemReport : itemReportList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(itemReport.getMaterialId());
            row.createCell(1).setCellValue(itemReport.getMaterialName());
            row.createCell(2).setCellValue(itemReport.getMaterialSpecification());
            row.createCell(3).setCellValue(itemReport.getMaterialUnit());
            row.createCell(4).setCellValue(itemReport.getSubmitTime().toString());


            row.createCell(5).setCellValue(itemReport.getTotalIn());
            row.createCell(6).setCellValue(itemReport.getTotalOut());


        }

        // 自动调整列宽
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }

        // 写入到字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        return bos.toByteArray();
    }

}
