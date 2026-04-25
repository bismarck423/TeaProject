package com.tea.trace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.trace.entity.TeaReview;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReviewMapper extends BaseMapper<TeaReview> {
}