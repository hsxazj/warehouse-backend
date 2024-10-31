package org.homework.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author BEJSON
 * @description admin
 * @date 2024-09-19
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
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
     * 性别：男 女
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

}