package com.tea.trace.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tea.trace.entity.TeaOrder;
import com.tea.trace.entity.TeaUser;
import com.tea.trace.mapper.OrderMapper;
import com.tea.trace.service.impl.TeaServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired private TeaServiceImpl teaService; // 注入商品服务处理库存

    // 创建订单
    @PostMapping("/create")
    @Transactional
    public Object createOrder(@RequestBody TeaOrder order, HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) return "请先登录";

        // 1. 检查库存并预占
        boolean lockSuccess = teaService.tryDecreaseStock(order.getTeaId(), order.getCount());
        if (!lockSuccess) {
            return "库存不足";
        }

        // 2. 生成订单
        order.setUserId(user.getId());
        order.setOrderNo("TEA" + System.currentTimeMillis());
        order.setStatus(0); // 待支付
        order.setIsSandbox(0); // 默认为真实，支付时可标记为沙箱
        order.setCreateTime(LocalDateTime.now());

        orderMapper.insert(order);
        return order;
    }

    // 取消订单接口 (用户或超时调用)
    @PostMapping("/cancel")
    @Transactional
    public String cancelOrder(@RequestParam String orderNo) {
        TeaOrder order = orderMapper.selectOne(new QueryWrapper<TeaOrder>().eq("order_no", orderNo));
        if (order != null && order.getStatus() == 0) {
            order.setStatus(2); // 已取消
            orderMapper.updateById(order);
            // 释放库存
            teaService.releaseStock(order.getTeaId(), order.getCount());
            return "订单取消成功，库存已释放";
        }
        return "订单无法取消";
    }
}