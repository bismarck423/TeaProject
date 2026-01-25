package com.tea.trace.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tea.trace.entity.TeaProduct;

public interface TeaService extends IService<TeaProduct> {
    // 这里可以定义复杂的业务逻辑，比如：溯源码生成的校验
}