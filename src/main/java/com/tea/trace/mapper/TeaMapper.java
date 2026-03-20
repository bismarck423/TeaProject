package com.tea.trace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tea.trace.entity.TeaProduct;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 告诉 Spring 这是一个数据库操作接口
public interface TeaMapper extends BaseMapper<TeaProduct> {
    // MyBatis Plus 已经内置了基本的增删改查，这里暂时不需要写代码
}