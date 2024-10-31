package org.homework.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.homework.conventioin.result.Result;
import org.homework.enums.OperationType;
import org.homework.pojo.dto.GetApiLogByPageDto;
import org.homework.pojo.dto.GetOperationLogByPageDto;
import org.homework.pojo.po.User;
import org.homework.pojo.vo.LogVo;
import org.homework.pojo.vo.OperationLogVo;
import org.homework.service.LogService;
import org.homework.utils.ExcelUtil;
import org.homework.utils.MongoUtil;
import org.homework.utils.SecurityInfoUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LogServiceImpl implements LogService {
    
    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    @Resource
    private MongoUtil mongoUtil;
    
    @Override
    public Result<List<LogVo>> getApiLogByPage(GetApiLogByPageDto getApiLogByPageDto) {
        Query query = new Query();
        if (!getApiLogByPageDto.isForExport()) {
            // 分页
            query = query.with(PageRequest.of(getApiLogByPageDto.getCurrentPage() - 1, getApiLogByPageDto.getSize()));
            if (StringUtils.hasText(getApiLogByPageDto.getServerName())) {
                query.addCriteria(Criteria.where("serverName").is(getApiLogByPageDto.getServerName()));
            }
            if (StringUtils.hasText(getApiLogByPageDto.getRequestUrl())) {
                query.addCriteria(Criteria.where("requestPath").is(getApiLogByPageDto.getRequestUrl()));
            }
            if (getApiLogByPageDto.getResponseCode() != null) {
                query.addCriteria(Criteria.where("responseCode").is(getApiLogByPageDto.getResponseCode().toString()));
            }
        } else {
            query.addCriteria(Criteria.where("responseCode").ne(String.valueOf(200)));
            query.addCriteria(Criteria.where("requestPath").ne("/login"));
        }
        timeSelectOrder(
                query, getApiLogByPageDto.getStartTime(),
                getApiLogByPageDto.getEndTime(), getApiLogByPageDto.getIsDesc(),
                getApiLogByPageDto.isForExport(), "requestTime"
        );
        List<LogVo> log = mongoUtil.findList("api_log", query, LogVo.class);
        return Result.success(log);
    }
    
    @Override
    public Result getOperationLogByPage(GetOperationLogByPageDto getOperationLogByPageDto) {
        User user = SecurityInfoUtil.getUser();
        Query query = new Query();
        String type = getOperationLogByPageDto.getType();
        if (StringUtils.hasText(type)) {
            try {
                String typeStr
                        = OperationType.getTypeStringFromStr(type);
                query.addCriteria(Criteria.where("type").is(typeStr));
            } catch (Exception e) {
                log.info("无此类型");
            }
        }
        // 指定查看 who 的操作日志
        if (getOperationLogByPageDto.getUserId() != null) {
            query.addCriteria(Criteria.where("operationUserId").gte(user.getId()));
        }
        
        try {
            timeSelectOrder(
                    query, getOperationLogByPageDto.getStartTime(),
                    getOperationLogByPageDto.getEndTime(), getOperationLogByPageDto.getIsDesc(),
                    getOperationLogByPageDto.getIsForExport(), "operationTime"
            );
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
        
        long total = mongoUtil.totalData("operation_log", query);
        // 获取总页数
        int totalPage = (int) Math.ceil((double) total / getOperationLogByPageDto.getSize());
        // 不分页，用于导出数据
        if (!getOperationLogByPageDto.getIsForExport()) {
            // 分页
            if (getOperationLogByPageDto.getCurrentPage() == null || getOperationLogByPageDto.getSize() == null) {
                return Result.fail("未指定页码或者每页大小");
            }
            query = query.with(PageRequest.of(
                    getOperationLogByPageDto.getCurrentPage() - 1,
                    getOperationLogByPageDto.getSize())
            );
        }
        List<OperationLogVo> log = mongoUtil.findList("operation_log", query, OperationLogVo.class);
        Map<String, Object> map = new HashMap<>();
        map.put("totalPage", totalPage);
        map.put("data", log);
        return Result.success(map);
    }
    
    @Override
    public void exportOperationLogByExcel(GetOperationLogByPageDto getOperationLogByPageDto,
                                          HttpServletResponse response) throws IOException {
        // TODO 必须设置日期，并且时间跨度最多为7天
        Result operationLogByPage = getOperationLogByPage(getOperationLogByPageDto);
        List<OperationLogVo> logVoList = (List<OperationLogVo>) operationLogByPage.getData();
        StringBuffer sb = new StringBuffer("operation_log_");
        if (getOperationLogByPageDto.getType() != null) {
            String typeStringFromStr = OperationType.getTypeStringFromStr(getOperationLogByPageDto.getType());
            sb.insert(0, typeStringFromStr);
        }
        sb.append(sf.format(new Date()));
        sb.append(".xlsx");
        String filePath = sb.toString();
        EasyExcel.write(filePath, OperationLogVo.class)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet("sheet1")
                .doWrite(logVoList);
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
        ExcelUtil.export(xssfWorkbook, filePath, response);
    }
    
    public void timeSelectOrder(Query query, Date startTime, Date endTime,
                                Boolean isDesc, Boolean isForExport, String timeTableName) {
        Criteria criteria = new Criteria();
        // 转为LocalDate
        LocalDateTime start
                = startTime != null ? startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
        LocalDateTime end
                = endTime != null ? endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
        if (isForExport) {
            if (start != null) {
                if (end != null) {
                    long daysBetween = ChronoUnit.DAYS.between(start, end);
                    if (Math.abs(daysBetween) > 7) {
                        throw new IllegalArgumentException("时间跨度最大为七天");
                    } else {
                        query.addCriteria(Criteria.where(timeTableName).gte(start).lte(end));
                    }
                } else {
                    query.addCriteria(Criteria.where(timeTableName).gte(start).lte(start.plusDays(8)));
                }
            } else {
                if (end != null) {
                    query.addCriteria(Criteria.where(timeTableName).gte(end.minusDays(7)).lte(end.plusDays(1)));
                } else {
                    LocalDateTime now = LocalDateTime.now();
                    query.addCriteria(Criteria.where(timeTableName).gte(now.minusDays(7)).lte(now.plusDays(1)));
                }
            }
        } else {
            Criteria where = Criteria.where(timeTableName);
            boolean needToAdd = false;
            if (startTime != null) {
                needToAdd = true;
                where.gte(start);
            }
            if (endTime != null) {
                needToAdd = true;
                where.lte(end);
            }
            if (needToAdd) {
                query.addCriteria(where);
            }
        }
        if (isDesc != null && isDesc) {
            query.with(Sort.by(Sort.Direction.DESC, timeTableName));
        }
    }
}
