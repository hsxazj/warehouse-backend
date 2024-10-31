package org.homework.controller;

import lombok.RequiredArgsConstructor;
import org.homework.conventioin.result.Result;
import org.homework.pojo.dto.LoginDto;
import org.homework.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录接口
 */
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    Result login(@RequestBody @Validated LoginDto loginDto) {
        return userService.login(loginDto);
    }
}
