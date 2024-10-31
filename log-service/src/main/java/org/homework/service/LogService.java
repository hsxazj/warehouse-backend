package org.homework.service;

import jakarta.servlet.http.HttpServletResponse;
import org.homework.conventioin.result.Result;
import org.homework.pojo.dto.GetApiLogByPageDto;
import org.homework.pojo.dto.GetOperationLogByPageDto;
import org.homework.pojo.vo.LogVo;

import java.io.IOException;
import java.util.List;


public interface LogService {
    /**
     * 分页查询API日志
     *
     * @param getApiLogByPageDto 包含查询条件和分页信息的DTO对象
     * @return 返回查询结果，包括API日志数据和分页信息
     */
    Result<List<LogVo>> getApiLogByPage(GetApiLogByPageDto getApiLogByPageDto);

    /**
     * 分页查询操作日志
     *
     * @param getOperationLogByPageDto 包含查询条件和分页信息的DTO对象
     * @return 返回查询结果，包括操作日志数据和分页信息
     */
    Result getOperationLogByPage(GetOperationLogByPageDto getOperationLogByPageDto);

    /**
     * 通过Excel导出操作日志
     *
     * @param getOperationLogByPageDto 包含查询条件的DTO对象
     * @return 返回导出结果，包括Excel文件流和文件名
     */
    void exportOperationLogByExcel(GetOperationLogByPageDto getOperationLogByPageDto, HttpServletResponse response) throws IOException;

}
