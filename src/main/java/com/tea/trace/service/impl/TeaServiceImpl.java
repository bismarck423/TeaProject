package com.tea.trace.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tea.trace.entity.TeaProduct;
import com.tea.trace.mapper.TeaMapper;
import com.tea.trace.service.TeaService;
import org.springframework.stereotype.Service;

@Service
public class TeaServiceImpl extends ServiceImpl<TeaMapper, TeaProduct> implements TeaService {
    // 继承了 ServiceImpl 后，基本的数据库操作逻辑就自动完成了
}