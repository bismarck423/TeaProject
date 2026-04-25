package com.tea.trace.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tea.trace.entity.TeaProduct;
import com.tea.trace.mapper.TeaMapper;
import com.tea.trace.service.TeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeaServiceImpl extends ServiceImpl<TeaMapper, TeaProduct> implements TeaService {

    @Override
    @Transactional
    public synchronized boolean tryDecreaseStock(Long teaId, Integer count) {
        TeaProduct product = this.getById(teaId);
        if (product != null && product.getStock() >= count) {
            product.setStock(product.getStock() - count);
            return this.updateById(product);
        }
        return false;
    }

    @Override
    @Transactional
    public void releaseStock(Long teaId, Integer count) {
        TeaProduct product = this.getById(teaId);
        if (product != null) {
            product.setStock(product.getStock() + count);
            this.updateById(product);
        }
    }
}