package com.tea.trace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.trace.entity.TeaOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<TeaOrder> {
}