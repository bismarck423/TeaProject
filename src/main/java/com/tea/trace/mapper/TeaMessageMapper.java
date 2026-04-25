package com.tea.trace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.trace.entity.TeaMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeaMessageMapper extends BaseMapper<TeaMessage> {
}