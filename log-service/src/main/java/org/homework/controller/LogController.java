package org.homework.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.homework.conventioin.result.Result;
import org.homework.pojo.dto.GetApiLogByPageDto;
import org.homework.pojo.dto.GetOperationLogByPageDto;
import org.homework.pojo.vo.LogVo;
import org.homework.service.LogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


/**
 * 日志相关
 */
@RequestMapping("/log")
@RestController
public class LogController {

    @Resource
    private LogService logService;

    /**
     * 分页获取api日志
     */
    @PreAuthorize("hasAuthority('log:api')")
    @GetMapping(value = "/getApiLogByPage")
    public Result<List<LogVo>> getApiLogListByPage(@ModelAttribute @Validated GetApiLogByPageDto getApiLogByPageDto) {
        return logService.getApiLogByPage(getApiLogByPageDto);
    }

    /**
     * 分页获取操作日志
     */
    @PreAuthorize("hasAuthority('log:operation')")
    @GetMapping("/getOperationLogByPage")
    public Result getOperationLogListByPage(@RequestBody @Validated GetOperationLogByPageDto getOperationLogByPageDto) {
        if (getOperationLogByPageDto.getIsForExport() == null) {
            getOperationLogByPageDto.setIsForExport(false);
        }
        if (getOperationLogByPageDto.getIsDesc() == null) {
            getOperationLogByPageDto.setIsDesc(true);
        }
        return logService.getOperationLogByPage(getOperationLogByPageDto);
    }

    /**
     * 以excel的形式导出操作日志，必须是超管才能导出
     */
    @PreAuthorize("hasAuthority('log:operation')")
    @GetMapping("/exportOperationLogByExcel")
    public void exportOperationLogByExcel(@RequestBody @Validated GetOperationLogByPageDto getOperationLogByPageDto,
                                          HttpServletResponse response) throws IOException {
        getOperationLogByPageDto.setIsForExport(true);
        getOperationLogByPageDto.setIsDesc(true);
        logService.exportOperationLogByExcel(getOperationLogByPageDto, response);
    }
}
