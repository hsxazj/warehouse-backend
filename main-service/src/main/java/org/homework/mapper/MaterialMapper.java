package org.homework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.homework.pojo.bo.MaterialStockCheck;
import org.homework.pojo.dto.ItemReportDTO;
import org.homework.pojo.po.Material;
import org.homework.pojo.vo.MaterialTypeVo;

import java.util.List;

/**
 * @author zhanghaifeng
 * @description 针对表【materials】的数据库操作Mapper
 * @createDate 2024-09-19 10:26:13
 * @Entity org.homework.pojo.po.Material
 */
@Mapper

public interface MaterialMapper extends BaseMapper<Material> {

    List<MaterialTypeVo> getTypeList();

    List<MaterialStockCheck> getStock(@Param("orderId") Long orderId);

    @Select("""
            SELECT
                m.id AS material_id,
                m.name AS material_name,
                m.specification AS material_specification,
                m.unit AS material_unit,
                o.submit_time,
                IFNULL(SUM(CASE WHEN o.in_out = 0 THEN om.quantity ELSE 0 END), 0) AS total_in,
                IFNULL(SUM(CASE WHEN o.in_out = 1 THEN om.quantity ELSE 0 END), 0) AS total_out
                        
            FROM
                material m
            JOIN
                order_material om ON m.id = om.material_id
            JOIN
                `order` o ON om.order_id = o.id
            WHERE
                m.id = #{materialId} AND YEAR(o.submit_time) = #{year}
            GROUP BY
                m.id, m.name, m.specification, m.unit, o.submit_time
            ORDER BY
                o.submit_time;""")
    List<ItemReportDTO> queryOrderByMaterialIdAndYear(@Param("materialId") Long materialId, @Param("year") String year);
}




