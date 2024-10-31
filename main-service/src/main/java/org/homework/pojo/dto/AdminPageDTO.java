package org.homework.pojo.dto;

import lombok.Data;

@Data
public class AdminPageDTO {
    /**
     * 当前页码
     */
    private Integer current;
    /**
     * 每页条数
     */
    private Integer size;
}
