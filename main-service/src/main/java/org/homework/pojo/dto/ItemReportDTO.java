package org.homework.pojo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ItemReportDTO {
    private Long materialId;
    private String materialName;
    private String materialSpecification;
    private String materialUnit;
    private Date submitTime;
    private Long totalIn;
    private Long totalOut;
}
