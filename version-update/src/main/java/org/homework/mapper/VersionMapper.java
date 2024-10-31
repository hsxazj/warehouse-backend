package org.homework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.homework.pojo.po.SystemVersion;

@Mapper
public interface VersionMapper extends BaseMapper<SystemVersion> {
}
