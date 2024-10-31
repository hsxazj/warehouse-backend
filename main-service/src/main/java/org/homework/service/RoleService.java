package org.homework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.homework.conventioin.result.Result;
import org.homework.pojo.dto.AddRoleDto;
import org.homework.pojo.po.Role;


/**
 * @author zhanghaifeng
 * @description 针对表【role】的数据库操作Service
 * @createDate 2024-10-04 22:53:50
 */
public interface RoleService extends IService<Role> {


    /**
     * 添加角色
     * <p>
     * 该方法用于根据 AddRoleDto 对象中的角色信息创建一个新的角色，并且对其权限进行初始化（如果需要）
     * 它是服务层或业务逻辑层的方法，负责处理与角色添加相关的逻辑
     *
     * @param addRoleDto 包含角色信息的数据传输对象，用于创建新角色
     * @return 返回一个 Result 对象，表示操作的结果，包含是否成功、消息等信息
     */
    Result addRole(AddRoleDto addRoleDto);

    /**
     * 获取角色列表
     *
     * @return 返回一个 Result 对象，包含角色列表
     */
    Result getRoleList();

    /**
     * 根据角色名称获取用户列表
     *
     * @param roleName 角色名称，用于筛选具有该角色的用户
     * @return 返回一个Result对象，包含根据角色名称查询到的用户列表
     */
    Result getUserListByRoleId(Integer roleName);


    /**
     * 删除指定的角色
     *
     * @param roleId 角色的唯一标识符
     * @return 返回删除角色的结果
     */
    Result deleteRole(Integer roleId);
}
