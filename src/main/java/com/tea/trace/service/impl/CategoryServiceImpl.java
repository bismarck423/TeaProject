package com.tea.trace.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tea.trace.entity.TeaCategory;
import com.tea.trace.mapper.CategoryMapper;
import com.tea.trace.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, TeaCategory> implements CategoryService {
}