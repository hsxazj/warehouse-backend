package org.homework.pojo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogVo {

    @ExcelProperty(value = "服务名称")
    private String serverName;

    @ExcelProperty(value = "请求路径")
    private String requestPath;

    @ExcelProperty(value = "请求方法")
    private String requestMethod;

    @ExcelProperty(value = "请求ip")
    private String ip;

    @ExcelProperty(value = "请求时间")
    private String requestTime;

    @ExcelProperty(value = "请求参数")
    private String requestArgs;

    @ExcelProperty(value = "响应码")
    private String responseCode;

}
