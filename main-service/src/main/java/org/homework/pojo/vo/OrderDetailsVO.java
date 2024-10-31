package org.homework.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 订单详情 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailsVO {
    /**
     * 订单的物料列表
     */
    private List<OrderMaterialVO> orderMaterialVOList;
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
     * 库存的变动信息
     */
    private String variation;
}
