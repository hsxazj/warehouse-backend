package org.homework.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUpdateDTO {
    /**
     * 电话
     */
    @NotNull
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
    private Date birth;

}
