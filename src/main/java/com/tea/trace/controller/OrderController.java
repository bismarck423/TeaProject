package com.tea.trace.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tea.trace.entity.TeaOrder;
import com.tea.trace.entity.TeaUser;
import com.tea.trace.mapper.OrderMapper;
import com.tea.trace.mapper.UserMapper;
import com.tea.trace.service.impl.TeaServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.web.bind.annotation.*;
import com.tea.trace.entity.TeaCart;
import com.tea.trace.entity.TeaProduct;
import com.tea.trace.mapper.CartMapper;
import com.tea.trace.mapper.TeaMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired private OrderMapper orderMapper;
    @Autowired private TeaServiceImpl teaService;
    @Autowired private CartMapper cartMapper;
    @Autowired private TeaMapper teaMapper;
    @Autowired private UserMapper userMapper;

    // 创建订单
    @PostMapping("/create")
    @Transactional
    public Object createOrder(@RequestBody TeaOrder order, HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) {
            return "请先登录";
        }

        // 1. 检查库存并预占
        boolean lockSuccess = teaService.tryDecreaseStock(order.getTeaId(), order.getCount());
        if (!lockSuccess) {
            return "库存不足";
        }

        // 2. 生成订单
        order.setUserId(user.getId());
        order.setOrderNo("TEA" + System.currentTimeMillis());
        order.setStatus(0);
        order.setIsSandbox(0);
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


    @PostMapping("/cartCheckout")
    @Transactional
    public Object cartCheckout(HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) {
            return "请先登录";
        }

        List<TeaCart> cartList = cartMapper.selectList(new LambdaQueryWrapper<TeaCart>().eq(TeaCart::getUserId, user.getId()));
        if (cartList == null || cartList.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<String> orderNos = new ArrayList<>();

        for (TeaCart cart : cartList) {
            TeaProduct product = teaMapper.selectById(cart.getTeaId());
            boolean lockSuccess = teaService.tryDecreaseStock(cart.getTeaId(), cart.getCount());
            if (!lockSuccess) {
                throw new RuntimeException("商品 [" + product.getName() + "] 库存不足，结算失败");
            }

            TeaOrder order = new TeaOrder();
            order.setUserId(user.getId());
            order.setOrderNo("TEA" + System.nanoTime() + (int)(Math.random() * 100));
            order.setTeaId(product.getId());
            order.setTeaName(product.getName());
            order.setCount(cart.getCount());
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(cart.getCount()));
            order.setAmount(itemTotal);
            order.setStatus(0);
            order.setIsSandbox(0);
            order.setCreateTime(LocalDateTime.now());

            orderMapper.insert(order);
            totalAmount = totalAmount.add(itemTotal);
            orderNos.add(order.getOrderNo());
        }

        // 订单生成完毕，清空当前用户的购物车
        cartMapper.delete(new LambdaQueryWrapper<TeaCart>().eq(TeaCart::getUserId, user.getId()));

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", String.join(",", orderNos));
        result.put("amount", totalAmount);
        return result;
    }

    @PostMapping("/confirm")
    @Transactional
    public String confirmReceipt(@RequestParam String orderNo, HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) {
            return "请先登录";
        }

        TeaOrder order = orderMapper.selectOne(new LambdaQueryWrapper<TeaOrder>().eq(TeaOrder::getOrderNo, orderNo));

        if (order == null || !order.getUserId().equals(user.getId())) {
            return "无权操作此订单";
        }
        if (order.getStatus() != 2) {
            return "该订单状态无法确认收货";
        }

        order.setStatus(3);
        orderMapper.updateById(order);

        return "SUCCESS";
    }

    @PostMapping("/delete")
    @Transactional
    public String deleteOrder(@RequestParam String orderNo, HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) {
            return "请先登录";
        }

        TeaOrder order = orderMapper.selectOne(new LambdaQueryWrapper<TeaOrder>().eq(TeaOrder::getOrderNo, orderNo));
        if (order == null) {
            return "订单不存在";
        }

        if (!"ADMIN".equals(user.getRole()) && !order.getUserId().equals(user.getId())) {
            return "无权操作此订单";
        }

        if (order.getStatus() != 3 && order.getStatus() != 4) {
            return "只能删除【已完成】或【已退款】的历史订单，进行中的订单受保护";
        }

        orderMapper.deleteById(order.getId());

        return "SUCCESS";
    }

    @PostMapping("/refund")
    @Transactional
    public String applyRefund(@RequestParam String orderNo, HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) {
            return "请先登录";
        }

        TeaOrder order = orderMapper.selectOne(new LambdaQueryWrapper<TeaOrder>().eq(TeaOrder::getOrderNo, orderNo));

        if (order == null || !order.getUserId().equals(user.getId())) {
            return "无权操作此订单";
        }
        if (order.getStatus() != 1) {
            return "仅已支付待发货订单可申请退款";
        }




        TeaUser dbUser = userMapper.selectById(user.getId());
        BigDecimal currentBalance = dbUser.getBalance() == null ? BigDecimal.ZERO : dbUser.getBalance();
        BigDecimal refundAmount = order.getAmount() == null ? BigDecimal.ZERO : order.getAmount();

        dbUser.setBalance(currentBalance.add(refundAmount));
        userMapper.updateById(dbUser);

        teaService.releaseStock(order.getTeaId(), order.getCount());

        order.setStatus(4);
        orderMapper.updateById(order);

        user.setBalance(dbUser.getBalance());
        session.setAttribute("user", user);

        return "SUCCESS";
    }
}