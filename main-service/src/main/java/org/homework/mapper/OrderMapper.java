package org.homework.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.homework.pojo.dto.OrderFlatDTO;
import org.homework.pojo.po.Order;

import java.util.List;

/**
 * @author zhanghaifeng
 * @description 针对表【Order】的数据库操作Mapper
 * @createDate 2024-09-26 11:07:06
 * @Entity generator.domain.Order
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    @Select("""
                SELECT
                    o.id AS order_id,
                    o.user_id,
                    o.status,
                    o.submit_time,
                    o.in_out AS order_in_out,
                    om.id AS order_material_id,
                    om.material_id,
                    m.name AS material_name,
                    CONCAT(om.quantity,' ',m.unit) as variation
                FROM
                    `order` o
                INNER JOIN
                    order_material om ON o.id = om.order_id
                INNER JOIN
                    material m ON om.material_id = m.id
                WHERE
                    o.status = #{status}
                ORDER BY
                    o.id, om.id
            """)
    List<OrderFlatDTO> selectOrdersWithMaterials(@Param("status") String status);


    @Select("""
                SELECT
                    o.id AS order_id,
                    o.user_id,
                    o.status,
                    o.submit_time,
                    o.in_out AS order_in_out,
                    om.id AS order_material_id,
                    om.material_id,
                    m.name AS material_name,
                    CONCAT(om.quantity,' ',m.unit) as variation
                FROM
                    `order` o
                INNER JOIN
                    order_material om ON o.id = om.order_id
                INNER JOIN
                    material m ON om.material_id = m.id
                WHERE
                    o.id = #{orderId}
                ORDER BY
                    o.id, om.id
            """)
    List<OrderFlatDTO> selectOrdersDetail(@Param("orderId") Long orderId);

    @Select("""
                SELECT
                    o.id AS order_id,
                    o.user_id,
                    o.status,
                    o.submit_time,
                    o.in_out AS order_in_out,
                    om.id AS order_material_id,
                    om.material_id,
                    m.name AS material_name,
                    CONCAT(om.quantity,' ',m.unit) as variation
                FROM
                    `order` o
                INNER JOIN
                    order_material om ON o.id = om.order_id
                INNER JOIN
                    material m ON om.material_id = m.id
                WHERE
                month(o.submit_time) = #{month} AND year(o.submit_time) = #{year} AND 
                    o.status = 1 
                ORDER BY
                    o.id, om.id
            """)
    List<OrderFlatDTO> selectOrderDetailsByMonth(@Param("month") String month, @Param("year") String year);

    List<OrderFlatDTO> selectOrderDetailsByParam(@Param("month") String month,
                                                 @Param("year") String year,
                                                 @Param("orderId") Long orderId, @Param("status") String status);
}




