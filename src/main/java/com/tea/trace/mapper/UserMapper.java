package com.tea.trace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.trace.entity.TeaUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<TeaUser> {
}