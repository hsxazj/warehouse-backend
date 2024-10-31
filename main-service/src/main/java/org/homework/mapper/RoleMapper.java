package org.homework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.homework.pojo.po.Role;
import org.homework.pojo.po.User;

import java.util.List;

/**
 * @author zhanghaifeng
 * @description 针对表【role】的数据库操作Mapper
 * @createDate 2024-10-04 22:53:50
 * @Entity org.homework.pojo.po.Role
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 根据角色ID查询用户列表
     *
     * @param roleId 角色ID
     * @return 用户列表
     */
    @Select("SELECT u.id, u.phone_number, u.real_name, u.gender, u.address, u.birth\n" +
            "FROM user u\n" +
            "JOIN user_role ur ON u.id = ur.user_id\n" +
            "JOIN role r ON ur.role_id = r.id\n" +
            "WHERE r.id = #{roleId};")
    List<User> selectUsersByRoleId(@Param("roleId") Integer roleId);
}




