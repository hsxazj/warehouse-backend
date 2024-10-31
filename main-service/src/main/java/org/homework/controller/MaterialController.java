package org.homework.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.homework.conventioin.result.Result;
import org.homework.pojo.dto.ItemReportDTO;
import org.homework.pojo.dto.MaterialUpdateDTO;
import org.homework.service.MaterialService;
import org.homework.service.impl.ExcelExportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物料相关
 */
@RequestMapping("/materials")
@RestController
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialsService;
    private final ExcelExportService excelExportService;


    /**
     * 分页获取物料存量信息
     */
    @PreAuthorize("hasAuthority('material:check_stock')")
    @GetMapping("/getMaterialByPage")
    Result getMaterialByPage(@RequestParam("current") int current,
                             @RequestParam("size") int size,
                             @RequestParam(value = "searchText", required = false) String searchText,
                             @RequestParam("filterNull") boolean filterNull) {
        return materialsService.getMaterialByPage(current, size, filterNull, searchText);
    }


    /**
     * 获取物料品类列表
     */
    @PreAuthorize("hasAuthority('material:type:get')")
    @GetMapping("/getTypeList")
    Result getTypeList() {
        return materialsService.getTypeList();
    }

    /**
     * 添加物料品类
     */
    @PreAuthorize("hasAuthority('material:add')")
    @PostMapping("/addMaterialType")
    Result addMaterialType(@RequestParam("materialJSON") String materialJSON) {
        return materialsService.addMaterialType(materialJSON);
    }

    /**
     * 修改物料
     */
    @PreAuthorize("hasAuthority('material:update')")
    @PutMapping("/updateMaterial")
    Result updateMaterial(@RequestBody MaterialUpdateDTO materialUpdateDTO) {
        return materialsService.updateMaterial(materialUpdateDTO);
    }

    /**
     * 根据id删除物料
     */
    @PreAuthorize("hasAuthority('material:delete')")
    @DeleteMapping("/deleteMaterialById")
    Result deleteMaterialById(@RequestParam("id") Long id) {
        return materialsService.deleteMaterialById(id);
    }

    /**
     * 根据物料id以及年份获取仓库账本信息
     */
    @PreAuthorize("hasAuthority('statistic:permission')")
    @GetMapping("/getWarehouseLedger")
    public Result getReportByMaterialIdAndYear(@RequestParam("materialId") Long materialId,
                                               @RequestParam("year") String year) {
        return Result.success(materialsService.queryOrderByMaterialIdAndYear(materialId, year));
    }

    /**
     * 导出年度报表数据（xlsx）
     */
    @PreAuthorize("hasAuthority('statistic:permission')")
    @GetMapping("/exportWarehouseLedger")
    public void getReport(@RequestParam("materialId") Long materialId,
                          @RequestParam("year") String year,
                          HttpServletResponse response) {
        List<ItemReportDTO> itemReports = materialsService.queryOrderByMaterialIdAndYear(materialId, year);
        excelExportService.exportLedgerByMaterialIdAndYear(itemReports, response);
    }
}
