package org.homework.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialUpdateDTO {

    private Long id;
    /**
     *
     */
    private String name;
    /**
     *
     */
    private String specification;
    /**
     *
     */
    private String unit;
    /**
     *
     */
    private Long stock;
    /**
     *
     */
    private String remark;
}
