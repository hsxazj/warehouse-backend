package org.homework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.homework.pojo.po.UserRole;

/**
 * @author zhanghaifeng
 * @description 针对表【user_role】的数据库操作Mapper
 * @createDate 2024-10-04 22:53:50
 * @Entity org.homework.pojo.po.UserRole
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

}




