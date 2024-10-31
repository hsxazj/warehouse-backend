package org.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.homework.conventioin.result.Result;
import org.homework.pojo.po.Permission;


/**
 * @author zhanghaifeng
 * @description 针对表【permission】的数据库操作Service
 * @createDate 2024-10-04 22:53:50
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 删除权限
     *
     * @param permissionId 待删除权限的唯一标识
     * @return 操作结果，包括成功、失败等信息
     */
    Result deletePermission(Long permissionId);

    /**
     * 更新权限信息
     *
     * @param permission 待更新的权限对象，包含权限的新信息
     * @return 操作结果，包括成功、失败等信息
     */
    Result updatePermission(Permission permission);

    /**
     * 获取所有权限信息
     *
     * @return 权限信息列表
     */
    Result getPermissions();

    /**
     * 根据角色ID获取权限信息
     *
     * @param roleId 角色ID
     * @return 权限信息列表
     */
    Result getPermissionByRoleId(Integer roleId);


    /**
     * 修改角色的权限
     * 通过添加和删除权限字符串来更新角色的权限
     *
     * @param roleId           角色的唯一标识符
     * @param addPermissionStr 要添加的权限字符串，以逗号分隔的权限ID
     * @param delPermissionStr 要删除的权限字符串，格式同addPermissionStr
     * @return 返回操作结果，指示修改是否成功
     */
    Result alterPermissionFormRole(Integer roleId, String addPermissionStr, String delPermissionStr);
}
