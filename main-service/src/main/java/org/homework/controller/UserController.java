package org.homework.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.homework.conventioin.result.Result;
import org.homework.pojo.dto.AdminPageDTO;
import org.homework.pojo.dto.AdminRegisterDTO;
import org.homework.pojo.dto.AdminUpdateDTO;
import org.homework.pojo.dto.BugReportDto;
import org.homework.service.UserService;
import org.homework.utils.MailUtil;
import org.redisson.api.RedissonClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 管理员相关
 */
@PreAuthorize("hasAuthority('user:manage')")
@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final MailUtil mailUtil;
    private final RedissonClient redissonClient;

    /**
     * 添加用户接口
     */
    @PostMapping("/addUser")
    Result addUser(@RequestBody @Validated AdminRegisterDTO requestParam) {
        return userService.addUser(requestParam);
    }


    /**
     * 分页查询用户接口
     */
    @GetMapping("/page/getUserList")
    Result getUserPage(@ModelAttribute AdminPageDTO adminPageDTO) {
        return userService.getUserPage(adminPageDTO);
    }

    /**
     * 超管修改用户接口
     */
    @PutMapping("/updateUser")
    Result updateUser(@RequestBody @Validated AdminUpdateDTO adminUpdateDTO) {
        return userService.updateUser(adminUpdateDTO);
    }

    /**
     * 根据id删除用户
     */
    @DeleteMapping("/deleteUser")
    Result deleteUser(@RequestParam("id") String id) {
        return userService.deleteUser(id);
    }

    /**
     * 超管模糊查询用户接口
     */
    @GetMapping("/fuzzyQueryUser")
    Result fuzzyQueryUser(@RequestParam("searchText") String searchText) {
        return userService.fuzzyQueryUser(searchText);
    }

    /**
     * 修改用户角色
     */
    @PutMapping("/updateUserRole")
    Result setUserRole(@RequestParam("userId") Long userId,
                       @RequestParam("roleId") Integer roleId) {
        return userService.setUserRole(userId, roleId);
    }

    /**
     * 添加用户角色
     */
    @PostMapping("/addUserRole")
    Result addUserRole(@RequestParam("userId") Long userId,
                       @RequestParam("roleId") Integer roleId) {
        return userService.addUserRole(userId, roleId);
    }

    /**
     * 通过excel导入用户
     */
    @PostMapping("/importUserByExcel")
    Result importUserByExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return userService.importUserByExcel(file.getInputStream());
    }

    /**
     * BUG反馈
     */
    @PreAuthorize("hasAuthority('report:bug')")
    @GetMapping("/bugReport")
    Result bugReport(@RequestBody @Validated BugReportDto bugReport) throws Exception {
        return userService.bugReport(bugReport);
    }

    /**
     * 意见反馈
     */
    @PreAuthorize("hasAuthority('report:suggestion')")
    @GetMapping("/uploadSuggestion")
    Result uploadSuggestion(@RequestParam("suggestion") String suggestion,
                            @RequestParam("contactDetails") String contactDetails) throws MessagingException {
        return userService.uploadSuggestion(suggestion, contactDetails);
    }
}
