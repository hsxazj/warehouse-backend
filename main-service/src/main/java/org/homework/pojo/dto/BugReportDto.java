package org.homework.pojo.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.homework.config.CustomDateDeserializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BugReportDto {

    /**
     * 操作流程
     */
    @NotBlank(message = "请描述操作流程")
    private String description;

    /**
     * BUG出现的时间，精确到分
     */
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    @NotBlank(message = "请输入联系方式")
    private String contactDetails;

}
