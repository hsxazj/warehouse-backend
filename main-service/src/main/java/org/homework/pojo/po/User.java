package org.homework.pojo.po;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.homework.conventioin.database.BaseDO;

import java.io.Serializable;
import java.util.Date;

/**
 * @author BEJSON
 * @description admin
 * @date 2024-09-19
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@TableName("user")
public class User extends BaseDO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 电话
     */
    @ExcelProperty(order = 1)
    private String phoneNumber;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 身份证号
     */
    @ExcelProperty(order = 3)
    private String identity;
    
    /**
     * 姓名
     */
    @ExcelProperty(order = 2)
    private String realName;
    
    /**
     * 性别：男 女
     */
    @ExcelProperty(order = 4)
    private String gender;
    
    /**
     * 地址
     */
    @ExcelProperty(order = 5)
    private String address;
    
    /**
     * 出生日期
     */
    @ExcelProperty(order = 6)
    @DateTimeFormat("yyyy/MM/dd")
    private Date birth;
    
    
    @TableField(exist = false)
    private Date createTime;
}