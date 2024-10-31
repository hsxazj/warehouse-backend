package org.homework.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterDTO {

    /**
     * 电话
     */
    private String phoneNumber;

    /**
     * 密码
     */
    private String password;

    /**
     * 身份证号
     */
    private String identity;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 性别：0为女性，1为男性
     */
    private String gender;

    /**
     * 地址
     */
    private String address;

    /**
     * 出生日期
     */
    private Date birth;
    /**
     * 角色id
     */
    private Integer roleId;
}
