package org.homework.controller;

import lombok.RequiredArgsConstructor;
import org.homework.conventioin.result.Result;
import org.homework.pojo.po.Permission;
import org.homework.service.PermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 权限相关
 */
@RestController
@PreAuthorize("hasAuthority('permission:manage')")
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 修改某角色的权限
     */
    @PutMapping("/alterPermissionFormRole")
    Result alterPermissionFormRole(@RequestParam("roleId") Integer roleId,
                                   @RequestParam("addPermissionStr") String addPermissionStr,
                                   @RequestParam("delPermissionStr") String delPermissionStr) {
        return permissionService.alterPermissionFormRole(roleId, addPermissionStr, delPermissionStr);
    }

    /**
     * 直接删除某权限（如果角色拥有此权限也一并删除）
     */
    @DeleteMapping("/deletePermission")
    Result deletePermission(Long permissionId) {
        return permissionService.deletePermission(permissionId);
    }

    /**
     * 修改某权限的信息
     */
    @PutMapping("/updatePermission")
    Result updatePermission(@RequestBody Permission Permission) {
        return permissionService.updatePermission(Permission);
    }

    /**
     * 获取权限列表
     */
    @GetMapping("/getPermissions")
    Result getPermissions() {
        return permissionService.getPermissions();
    }

    /**
     * 根据角色id获取的权限列表
     */
    @GetMapping("/getPermissionByRoleId")
    Result getPermissionByRoleId(@RequestParam("roleId") Integer roleId) {
        return permissionService.getPermissionByRoleId(roleId);
    }
}
