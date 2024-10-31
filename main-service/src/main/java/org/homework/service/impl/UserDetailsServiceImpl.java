package org.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.homework.mapper.UserMapper;
import org.homework.mapper.UserRoleMapper;
import org.homework.pojo.bo.LoginUser;
import org.homework.pojo.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    private final UserRoleMapper userRoleMapper;

    @Autowired
    public UserDetailsServiceImpl(UserMapper userMapper, UserRoleMapper userRoleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhoneNumber, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        List<String> permissionList = userMapper.getPermissionList(user.getId());
        return new LoginUser(user, permissionList);
    }
}
