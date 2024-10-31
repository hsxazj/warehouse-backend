package org.homework.pojo.dto;

import lombok.Data;

import java.util.Date;

/**
 * 订单的查询 DTO
 */
@Data
public class OrderFlatDTO {

    /**
     * 订单ID
     * 对应 SQL 中的 o.id AS order_id
     */
    private Long orderId;

    /**
     * 用户ID
     * 对应 SQL 中的 o.user_id
     */
    private Long userId;

    /**
     * 订单状态
     * 对应 SQL 中的 o.status
     * <p>
     * 0 为未审核, 1 为已审批, 2 为拒绝
     */
    private Integer status;

    /**
     * 提交时间
     * 对应 SQL 中的 o.submit_time
     */
    private Date submitTime;

    /**
     * 订单的出入库状态
     * 对应 SQL 中的 o.in_out AS order_in_out
     * <p>
     * 0 为入库, 1 为出库
     */
    private Integer orderInOut;

    /**
     * 订单物料关系ID
     * 对应 SQL 中的 om.id AS order_material_id
     */
    private Long orderMaterialId;

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
     * 物资变动量及单位
     */
    private String variation;

}
