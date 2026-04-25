package com.tea.trace.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tea.trace.entity.TeaProduct;

public interface TeaService extends IService<TeaProduct> {
    boolean tryDecreaseStock(Long teaId, Integer count); // 尝试扣库存
    void releaseStock(Long teaId, Integer count);        // 释放库存
}