package org.homework.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.homework.config.CustomCellWriteHeightConfig;
import org.homework.config.CustomCellWriteWidthConfig;
import org.homework.conventioin.exception.ServiceException;
import org.homework.enums.OperationType;
import org.homework.pojo.dto.ItemReportDTO;
import org.homework.pojo.excel.OrderDetailsExcel;
import org.homework.pojo.excel.WareHouseLedgerExcel;
import org.homework.pojo.vo.OrderDetailsVO;
import org.homework.utils.MQUtil;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {
    private final CustomCellWriteHeightConfig customCellWriteHeightConfig;
    private final CustomCellWriteWidthConfig customCellWriteWidthConfig;
    private final MQUtil mqUtil;


    private List<OrderDetailsExcel> convertToExcelModel(List<OrderDetailsVO> orders) {
        List<OrderDetailsExcel> excelList = new ArrayList<>();
        for (OrderDetailsVO order : orders) {
            excelList.add(convertToOrderDetailsExcel(order));
        }
        return excelList;
    }

    public OrderDetailsExcel convertToOrderDetailsExcel(OrderDetailsVO order) {
        OrderDetailsExcel excel = new OrderDetailsExcel();
        excel.setOrderId(order.getOrderId());
        excel.setSubmitTime(order.getSubmitTime());
        // 转换出入库状态
        String inOutStr = order.getOrderInOut() == 0 ? "入库" : "出库";
        excel.setInOutStatus(inOutStr);
        excel.setVariation(order.getVariation());
        return excel;
    }

    /**
     * 导出订单详情到 Excel
     *
     * @param orders   订单详情列表
     * @param response HTTP 响应
     */
    public void exportOrdersToExcel(List<OrderDetailsVO> orders, HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.ms-excel");
            String fileName = URLEncoder.encode("订单详情" + System.currentTimeMillis(), "UTF-8") + ".xlsx";
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            // 使用 EasyExcel 写入 Excel
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), OrderDetailsExcel.class)

                    .registerWriteHandler(customCellWriteWidthConfig)
                    .build();
            WriteSheet writeSheet = EasyExcel.writerSheet("模板").needHead(Boolean.TRUE).build();
            excelWriter.write(convertToExcelModel(orders), writeSheet);
            excelWriter.finish();
            mqUtil.sendOperationLogM(OperationType.MATERIALS, "导出订单记录");
        } catch (Exception e) {
            throw new RuntimeException("导出 Excel 失败", e);
        }
    }

    public void exportLedgerByMaterialIdAndYear(List<ItemReportDTO> itemReports, HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.ms-excel");
            String fileName = URLEncoder.encode("库存盘点" + System.currentTimeMillis(), "UTF-8") + ".xlsx";
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            // 使用 EasyExcel 写入 Excel
            EasyExcel.write(response.getOutputStream(), WareHouseLedgerExcel.class)
                    .registerWriteHandler(customCellWriteHeightConfig)
                    .registerWriteHandler(customCellWriteWidthConfig)
                    .sheet("库存盘点").doWrite(convertToLedgerExcelModel(itemReports));
        } catch (Exception e) {
            throw new ServiceException("导出 Excel 失败");
        }
    }

    /**
     * 将 ItemReportDTO列表 转换为 WareHouseLedgerExcel列表
     *
     * @param itemReports ItemReportDTO列表
     * @return WareHouseLedgerExcel列表
     */
    private List<WareHouseLedgerExcel> convertToLedgerExcelModel(List<ItemReportDTO> itemReports) {
        List<WareHouseLedgerExcel> wareHouseLedgerExcelList = new ArrayList<>();
        for (ItemReportDTO itemReport : itemReports) {
            WareHouseLedgerExcel wareHouseLedgerExcel = BeanUtil.copyProperties(itemReport, WareHouseLedgerExcel.class);
            wareHouseLedgerExcelList.add(wareHouseLedgerExcel);
        }
        return wareHouseLedgerExcelList;
    }
}
