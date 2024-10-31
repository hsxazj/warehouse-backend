package org.homework.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class LoginDto {

    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", message = "电话号格式不正确")
    private String phoneNumber;

    @NotBlank(message = "密码不能为空")
    private String password;

}
