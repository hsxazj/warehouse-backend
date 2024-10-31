package org.homework;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.homework.mapper.UserMapper;
import org.homework.pojo.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestUser {
    @Resource
    UserMapper userMapper;

    @Test
    void test() {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, 1));
    }
}
