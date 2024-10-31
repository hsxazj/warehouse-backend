package org.homework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.BatchResult;
import org.homework.conventioin.result.Result;
import org.homework.enums.OperationType;
import org.homework.mapper.PermissionMapper;
import org.homework.mapper.RolePermissionMapper;
import org.homework.mapper.UserRoleMapper;
import org.homework.pojo.po.Permission;
import org.homework.pojo.po.RolePermission;
import org.homework.pojo.po.UserRole;
import org.homework.pojo.vo.PermissionVo;
import org.homework.service.PermissionService;
import org.homework.utils.MQUtil;
import org.homework.utils.RedisUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhanghaifeng
 * @description 针对表【permission】的数据库操作Service实现
 * @createDate 2024-10-04 22:53:50
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission>
        implements PermissionService {

    private final RolePermissionMapper rolePermissionMapper;

    private final PermissionMapper permissionMapper;

    private final RedisUtil redisUtil;

    private final RedissonClient redissonClient;

    private final UserRoleMapper userRoleMapper;

    private final MQUtil mqUtil;

    @Override
    public Result deletePermission(Long permissionId) {
        Permission permission = permissionMapper
                .selectOne(new LambdaQueryWrapper<Permission>().eq(Permission::getId, permissionId));
        if (permission == null) {
            return Result.fail("不存在该权限");
        }
        int delete = permissionMapper.deleteById(permissionId);
        if (delete <= 0) {
            return Result.fail("删除失败");
        }
        // 删除角色权限关联
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getPermissionId, permissionId));
        redisUtil.delete("permissionList");
        mqUtil.sendOperationLogM(OperationType.PERMISSION, "删除了权限：" + permission.getName());
        return Result.success();
    }

    @Override
    public Result updatePermission(Permission permission) {
        int update = permissionMapper.update(permission, new LambdaQueryWrapper<Permission>()
                .eq(Permission::getId, permission.getId()));
        if (update <= 0) {
            return Result.fail("更新失败");
        }
        redisUtil.delete("permissionList");
        mqUtil.sendOperationLogM(OperationType.PERMISSION, "更新了权限：" + permission.getId());
        return Result.success();
    }

    @Override
    public Result getPermissions() {
        List<Permission> permissionList = redisUtil.getList("permissionList", Permission.class);
        if (CollectionUtils.isEmpty(permissionList)) {
            RLock getPermissions = redissonClient.getLock("getPermissions");
            try {
                getPermissions.lock();
                permissionList = redisUtil.getList("permissionList", Permission.class);
                if (CollectionUtils.isEmpty(permissionList)) {
                    LambdaQueryWrapper<Permission> queryWrapper = Wrappers.lambdaQuery(Permission.class);
                    permissionList = baseMapper.selectList(queryWrapper);
                    redisUtil.setList("permissionList", permissionList);
                }
            } finally {
                getPermissions.unlock();
            }
        }
        return Result.success(permissionList);
    }

    @Override
    public Result getPermissionByRoleId(Integer roleId) {
        Result permissions = getPermissions();
        List<Permission> permissionList = (List<Permission>) permissions.getData();
        // 查询角色权限关联表
        List<Permission> permissionListRole = baseMapper.selectByRoleId(roleId);
        List<Integer> idList = permissionListRole.stream().map(Permission::getId).toList();
        List<PermissionVo> permissionVoList = permissionList.stream().map(e -> {
            PermissionVo permissionVo = PermissionVo.builder()
                    .id(e.getId()).name(e.getName()).description(e.getDescription()).build();
            permissionVo.setSelected(idList.contains(e.getId()));
            return permissionVo;
        }).toList();
        return Result.success(permissionVoList);
    }

    @Override
    public Result alterPermissionFormRole(Integer roleId, String addPermissionStr, String delPermissionStr) {
        boolean changed = false;
        if (StringUtils.hasText(addPermissionStr)) {
            // 解析
            List<RolePermission> addPermissionList = Arrays.stream(addPermissionStr.split(",")).map(e -> {
                Integer permissionId = Integer.valueOf(e);
                return RolePermission.builder().permissionId(permissionId).roleId(roleId).build();
            }).toList();
            // 添加
            if (!CollectionUtils.isEmpty(addPermissionList)) {
                List<BatchResult> insert = rolePermissionMapper.insert(addPermissionList);
                if (!insert.isEmpty()) {
                    changed = true;
                    mqUtil.sendOperationLogM(OperationType.PERMISSION,
                            "为角色"
                                    + roleId
                                    + "添加了新的权限"
                                    + "，权限id:" + addPermissionList);
                }
            }
        }
        if (StringUtils.hasText(delPermissionStr)) {
            List<Integer> delPermissionIdList
                    = Arrays.stream(delPermissionStr.split(",")).map(Integer::valueOf).toList();
            if (!CollectionUtils.isEmpty(delPermissionIdList)) {
                int delete = rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>()
                        .eq(RolePermission::getRoleId, roleId)
                        .in(RolePermission::getPermissionId, delPermissionIdList));
                if (delete > 0) {
                    mqUtil.sendOperationLogM(OperationType.PERMISSION,
                            "为角色"
                                    + roleId
                                    + "删除了权限"
                                    + "，权限id:" + delPermissionStr);
                    changed = true;
                }
            }
        }
        if (changed) {
            // 获取角色id
            List<UserRole> userRoles = userRoleMapper
                    .selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, roleId));
            List<String> cacheList = new ArrayList<>();
            userRoles.forEach(e -> cacheList.add(redisUtil.LOGIN_KEY + e.getUserId()));
            // 删除校验信息
            redisUtil.delete(cacheList);
        }
        return Result.success();
    }
}




