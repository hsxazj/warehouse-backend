package org.homework.pojo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperationLogVo {

    @ExcelProperty(value = "操作人")
    private String operationUser;

    @ExcelProperty(value = "操作时间")
    @JsonFormat(locale = "zh", timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operationTime;

    @ExcelProperty(value = "操作详情")
    private String description;

    @ExcelProperty(value = "操作类型")
    private String type;

}
