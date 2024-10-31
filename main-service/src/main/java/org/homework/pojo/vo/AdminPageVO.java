package org.homework.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AdminPageVO {
    /**
     * 管理员ID
     */
    private Long id;
    /**
     * 电话
     */
    private String phoneNumber;


    /**
     * 身份证号
     */
    private String identity;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 性别：
     */
    private String gender;

    /**
     * 地址
     */
    private String address;

    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birth;
}
