package org.homework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.homework.pojo.po.User;

import java.util.List;

/**
 * @author zhanghaifeng
 * @description 针对表【admin】的数据库操作Mapper
 * @createDate 2024-09-12 21:28:12
 * @Entity org.homework.pojo.po.Admin
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<String> getPermissionList(@Param("userId") Long userId);

    List<User> fuzzySelectByRealName(@Param("realName") String realName);

    int insertIgnore(@Param("userList") List<User> userList);

}




