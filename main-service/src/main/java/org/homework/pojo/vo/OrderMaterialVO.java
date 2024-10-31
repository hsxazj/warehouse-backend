package org.homework.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单中单个物料的详情信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderMaterialVO {
    /**
     * 物料ID
     * 对应 SQL 中的 om.material_id
     */
    private Long materialId;

    /**
     * 物料名称
     * 对应 SQL 中的 m.name AS material_name
     */
    private String materialName;

    /**
     * 变动量情况
     */
    private String variation;
}
