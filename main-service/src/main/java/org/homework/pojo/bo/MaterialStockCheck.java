package org.homework.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialStockCheck {

    private Long id;

    private String name;

    private Long stock;

    private Long quantity;

}
