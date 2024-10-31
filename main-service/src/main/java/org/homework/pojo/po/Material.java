package org.homework.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @TableName materials
 */
@TableName(value = "material")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Material implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableId(type = IdType.AUTO)
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