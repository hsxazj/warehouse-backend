package org.homework.pojo.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.homework.config.CustomDateDeserializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetApiLogByPageDto {

    /**
     * 每页大小
     */
    @NotNull(message = "每页大小不能为空")
    private Integer size;

    /**
     * 目前页码
     */
    @NotNull(message = "页码不能为空")
    private Integer currentPage;

    /**
     * 是否倒序
     */
    private Boolean isDesc;

    /**
     * 服务名称
     */
    private String serverName;

    /**
     * 开始时间
     */
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 请求路径
     */
    private String requestUrl;

    /**
     * 响应码
     */
    private Integer responseCode;

    /**
     * 是否为了导出
     */
    private boolean isForExport;


}
