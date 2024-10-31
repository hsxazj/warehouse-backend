package org.homework.controller;


import org.homework.conventioin.result.Result;
import org.homework.pojo.dto.AddRoleDto;
import org.homework.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 角色相关
 */
@PreAuthorize("hasAuthority('role:manage')")
@RequestMapping("/role")
@RestController
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 添加角色
     */
    @PostMapping("/addRole")
    Result addRole(@RequestBody AddRoleDto addRoleDto) {
        return roleService.addRole(addRoleDto);
    }

    /**
     * 获取所有角色列表
     */
    @GetMapping("/roleList")
    Result roleList() {
        return roleService.getRoleList();
    }

    /**
     * 根据角色id获取其对应的用户列表
     */
    @GetMapping("/userListByRoleId")
    Result userListByRoleName(@RequestParam("roleId") Integer roleId) {
        return roleService.getUserListByRoleId(roleId);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/deleteRole")
    Result deleteRole(@RequestParam("roleId") Integer roleId) {
        return roleService.deleteRole(roleId);
    }

}
