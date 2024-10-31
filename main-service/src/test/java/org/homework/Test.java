package org.homework;

import jakarta.annotation.Resource;
import org.homework.conventioin.result.Result;
import org.homework.mapper.RolePermissionMapper;
import org.homework.mapper.UserMapper;
import org.homework.pojo.po.RolePermission;
import org.homework.service.UserService;
import org.homework.utils.MailUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = AdminServiceApplication.class)
public class Test {
    private final String presentedPassword = "$2a$10$hKfnu8AqfiZvEbd8mxLH7eANjSs8L9GT2bA3JCJzGkFgquPGOcOXW";
    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserMapper userMapper;

    @Resource
    private MailUtil mailUtil;

    @Resource
    private UserService userService;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @org.junit.jupiter.api.Test
    public void tests() {

        boolean matches = passwordEncoder.matches("123", presentedPassword);
        System.out.println(matches);
    }

    @org.junit.jupiter.api.Test
    public void insert() {
        int[] ids = {12, 13, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
        List<RolePermission> rolePermissionList = new ArrayList<>();
        for (int id : ids) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setPermissionId(id);
            rolePermission.setRoleId(1);
            rolePermissionList.add(rolePermission);
        }
        rolePermissionMapper.insert(rolePermissionList);
    }

    @org.junit.jupiter.api.Test
    void test() throws IOException {
        File file = new File("E:\\java\\warehouse-management-cloud\\template_for_user_import.xlsx");
        InputStream inputStream = new FileInputStream(file);
        Result result = userService.importUserByExcel(inputStream);
    }
}
