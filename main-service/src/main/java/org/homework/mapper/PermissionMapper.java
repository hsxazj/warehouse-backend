package org.homework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.homework.pojo.po.Permission;

import java.util.List;

/**
 * @author zhanghaifeng
 * @description 针对表【permission】的数据库操作Mapper
 * @createDate 2024-10-04 22:53:50
 * @Entity org.homework.pojo.po.Permission
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Select("""
            SELECT p.id,p.name, p.description
            FROM permission p
            JOIN role_permission rp ON p.id = rp.permission_id
            WHERE rp.role_id = #{roleId};""")
    List<Permission> selectByRoleId(@Param("roleId") Integer roleId);
}




