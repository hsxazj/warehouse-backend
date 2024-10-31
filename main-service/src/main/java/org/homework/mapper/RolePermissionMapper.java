package org.homework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.homework.pojo.po.RolePermission;

/**
 * @author zhanghaifeng
 * @description 针对表【role_permission】的数据库操作Mapper
 * @createDate 2024-10-04 22:53:50
 * @Entity org.homework.pojo.po.RolePermission
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

}




