package org.homework;

import jakarta.annotation.Resource;
import org.homework.mapper.UserMapper;
import org.homework.pojo.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class AdminServiceApplicationTest {

    @Resource
    private UserMapper userMapper;

    @Test
    void test() {
        List<User> users = new ArrayList<>();
        // 创建user对象
        User user = new User();
        user.setPhoneNumber("18396015271");
        user.setPassword("123456");
        user.setIdentity("testtest");
        user.setRealName("admin");
        user.setGender("男");
        user.setAddress("中国");
        user.setBirth(new Date());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setDelFlag(0);
        userMapper.insert(users);
    }

}