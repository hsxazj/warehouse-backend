package org.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.homework.conventioin.exception.ServiceException;
import org.homework.conventioin.result.Result;
import org.homework.enums.OperationType;
import org.homework.mapper.RoleMapper;
import org.homework.mapper.RolePermissionMapper;
import org.homework.pojo.dto.AddRoleDto;
import org.homework.pojo.po.Role;
import org.homework.pojo.po.RolePermission;
import org.homework.pojo.po.User;
import org.homework.service.RoleService;
import org.homework.utils.MQUtil;
import org.homework.utils.RedisUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhanghaifeng
 * @description 针对表【role】的数据库操作Service实现
 * @createDate 2024-10-04 22:53:50
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
        implements RoleService {

    private final RolePermissionMapper rolePermissionMapper;
    private final RedisUtil redisUtil;
    private final MQUtil mqUtil;
    private final RedissonClient redissonClient;
    private final RoleMapper roleMapper;


    @Override
    public Result addRole(AddRoleDto addRoleDto) {
        Role role = new Role();
        role.setName(addRoleDto.getName());
        role.setDescription(addRoleDto.getDescription());
        // 尝试插入
        boolean save = save(role);
        if (!save) {
            Result.fail("该角色已存在");
        }
        // 插入role_permission表
        if (StringUtils.hasText(addRoleDto.getPermissionIdListStr())) {
            String[] permissionIdListArray = addRoleDto.getPermissionIdListStr().split(",");
            if (permissionIdListArray.length == 0) {
                return Result.success();
            }
            List<RolePermission> rolePermissionList = Arrays
                    .stream(permissionIdListArray)
                    .map(permissionId -> {
                        RolePermission rolePermission = new RolePermission();
                        rolePermission.setRoleId(role.getId());
                        rolePermission.setPermissionId(Integer.valueOf(permissionId));
                        return rolePermission;
                    })
                    .toList();
            rolePermissionMapper.insert(rolePermissionList);
        }
        redisUtil.delete("roleList");
        mqUtil.sendOperationLogM(OperationType.PERMISSION, "添加新角色：" + addRoleDto.getName());
        return Result.success();
    }

    @Override
    public Result getRoleList() {
        List<Role> roleList = redisUtil.getList("roleList", Role.class);
        if (CollectionUtils.isEmpty(roleList)) {
            RLock getRoleList = redissonClient.getLock("getRoleList");
            try {
                getRoleList.lock();
                roleList = redisUtil.getList("roleList", Role.class);
                if (CollectionUtils.isEmpty(roleList)) {
                    roleList = baseMapper.selectList(new LambdaQueryWrapper<>());
                    redisUtil.setList("roleList", roleList);
                }
            } finally {
                getRoleList.unlock();
            }
        }
        return Result.success(roleList);
    }

    @Override
    public Result getUserListByRoleId(Integer roleId) {
        //根据角色名称获取角色id
        List<User> userList = baseMapper.selectUsersByRoleId(roleId);
        //根据角色id获取用户id
        return Result.success(userList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteRole(Integer roleId) {

        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            return Result.fail("该角色不存在");
        }
        //根据角色id删除角色
        boolean delete = removeById(role);
        if (!delete) {
            throw new ServiceException("删除角色失败");
        }
        //根据角色id删除角色权限关联表
        LambdaQueryWrapper<RolePermission> queryWrapper = Wrappers.lambdaQuery(RolePermission.class)
                .eq(RolePermission::getRoleId, roleId);
        int rolePermissionDelFlag = rolePermissionMapper.delete(queryWrapper);
        if (rolePermissionDelFlag <= 0) {
            Result.fail("删除失败");
        }
        redisUtil.delete("roleList");
        mqUtil.sendOperationLogM(OperationType.PERMISSION, "删除角色：" + role.getName());
        return Result.success();
    }
}




