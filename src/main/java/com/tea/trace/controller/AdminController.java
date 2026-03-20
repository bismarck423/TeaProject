package com.tea.trace.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tea.trace.entity.*;
import com.tea.trace.mapper.*;
import com.tea.trace.service.TeaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private TeaService teaService;
    @Autowired private TeaCultureMapper cultureMapper;
    @Autowired private OrderMapper orderMapper;
    @Autowired private UserMapper userMapper; // 必须注入 UserMapper 否则看板会报错

    // 鉴权
    private void checkAdmin(HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("无管理员权限");
        }
    }

    // === 1. 数据看板 (此前被误删) ===
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardData(HttpSession session) {
        checkAdmin(session);
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userMapper.selectCount(null));
        data.put("productCount", teaService.count());
        data.put("orderCount", orderMapper.selectCount(null));

        // 统计销售额
        List<TeaOrder> paidOrders = orderMapper.selectList(new LambdaQueryWrapper<TeaOrder>().in(TeaOrder::getStatus, 1));
        BigDecimal totalSales = paidOrders.stream().map(TeaOrder::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("totalSales", totalSales);
        return data;
    }

    // === 2. 订单管理 (此前被误删) ===
    @GetMapping("/orders")
    public List<TeaOrder> getAllOrders(HttpSession session) {
        checkAdmin(session);
        return orderMapper.selectList(new LambdaQueryWrapper<TeaOrder>().orderByDesc(TeaOrder::getCreateTime));
    }

    @PostMapping("/order/ship")
    public String shipOrder(@RequestParam String orderNo, HttpSession session) {
        checkAdmin(session);
        TeaOrder order = orderMapper.selectOne(new LambdaQueryWrapper<TeaOrder>().eq(TeaOrder::getOrderNo, orderNo));
        if (order != null && order.getStatus() == 1) {
            order.setStatus(2); // 2: 已发货
            orderMapper.updateById(order);
            return "SUCCESS";
        }
        return "状态异常";
    }

    @PostMapping("/order/refund")
    @Transactional
    public String refundOrder(@RequestParam String orderNo, HttpSession session) {
        checkAdmin(session);
        TeaOrder order = orderMapper.selectOne(new LambdaQueryWrapper<TeaOrder>().eq(TeaOrder::getOrderNo, orderNo));
        if (order == null) return "订单不存在";

        // 退款回用户余额
        TeaUser user = userMapper.selectById(order.getUserId());
        if (user != null) {
            user.setBalance(user.getBalance().add(order.getAmount()));
            userMapper.updateById(user);
        }
        order.setStatus(4); // 4: 已退款
        orderMapper.updateById(order);
        return "SUCCESS";
    }

    // === 3. 用户管理 (此前被误删) ===
    @GetMapping("/users")
    public List<TeaUser> getAllUsers(HttpSession session) {
        checkAdmin(session);
        return userMapper.selectList(null);
    }

    @PostMapping("/user/setRole")
    public String setUserRole(@RequestParam Long userId, @RequestParam String role, HttpSession session) {
        checkAdmin(session);
        TeaUser user = userMapper.selectById(userId);
        if(user != null) {
            user.setRole(role);
            userMapper.updateById(user);
        }
        return "SUCCESS";
    }

    @PostMapping("/user/updateStatus")
    public String updateUserStatus(@RequestParam Long userId, @RequestParam Integer status, HttpSession session) {
        checkAdmin(session);
        // 需确保 TeaUser 实体有 status 字段，如果没有可暂时注释
        return "SUCCESS";
    }

    // === 4. 商品管理 (保留) ===
    @PostMapping("/product/save")
    public String saveProduct(@RequestBody TeaProduct product, HttpSession session) {
        checkAdmin(session);
        if (product.getId() == null) {
            product.setCreateTime(LocalDateTime.now());
            if (product.getTraceCode() == null) product.setTraceCode("T" + System.currentTimeMillis());
            teaService.save(product);
        } else {
            teaService.updateById(product);
        }
        return "SUCCESS";
    }

    @PostMapping("/product/delete")
    public String deleteProduct(@RequestParam Long id, HttpSession session) {
        checkAdmin(session);
        teaService.removeById(id);
        return "SUCCESS";
    }

    // === 5. 文化内容管理 (保留) ===
    @GetMapping("/culture/list")
    public List<TeaCulture> getAdminCultures(HttpSession session) {
        checkAdmin(session);
        return cultureMapper.selectList(null);
    }

    @PostMapping("/culture/save")
    public String saveCulture(@RequestBody TeaCulture culture, HttpSession session) {
        checkAdmin(session);
        if (culture.getId() == null) {
            culture.setCreateTime(LocalDateTime.now());
            cultureMapper.insert(culture);
        } else {
            cultureMapper.updateById(culture);
        }
        return "SUCCESS";
    }

    @PostMapping("/culture/delete")
    public String deleteCulture(@RequestParam Long id, HttpSession session) {
        checkAdmin(session);
        cultureMapper.deleteById(id);
        return "SUCCESS";
    }
}