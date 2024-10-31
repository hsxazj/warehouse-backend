package org.homework.pojo.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.homework.config.CustomDateDeserializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetOperationLogByPageDto {

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 目前页码
     */
    private Integer currentPage;

    /**
     * 是否倒序
     */
    private Boolean isDesc;

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

    private String type;

    private Long userId;

    // 是否准备导出
    private Boolean isForExport;


}
