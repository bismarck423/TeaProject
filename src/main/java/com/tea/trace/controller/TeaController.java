package com.tea.trace.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tea.trace.entity.TeaOrder;
import com.tea.trace.entity.TeaProduct;
import com.tea.trace.entity.TeaUser;
import com.tea.trace.mapper.UserMapper;
import com.tea.trace.mapper.TeaMapper;
import com.tea.trace.mapper.OrderMapper; // 必须导入
import com.tea.trace.service.TeaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tea")
public class TeaController {

    @Autowired
    private TeaService teaService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper; // 修正：添加缺失的注入

    // 获取所有茶叶列表
    @GetMapping("/list")
    public List<TeaProduct> getAllTeas() {
        return teaService.list();
    }

    // 根据溯源码查询茶叶信息
    @GetMapping("/trace/{code}")
    public TeaProduct getByTraceCode(@PathVariable String code) {
        return teaService.lambdaQuery().eq(TeaProduct::getTraceCode, code).one();
    }

    // 管理员权限校验：获取所有用户
    @GetMapping("/admin/users")
    public Object getAllUsers(HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "无权访问";
        }
        return userMapper.selectList(null);
    }

    // 普通用户：获取我的订单
    @GetMapping("/user/orders")
    public List<TeaOrder> getMyOrders(HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) {
            return null;
        }
        return orderMapper.selectList(new LambdaQueryWrapper<TeaOrder>()
                .eq(TeaOrder::getUserId, user.getId()));
    }
    // 获取当前登录用户的实时信息
    @GetMapping("/user/me")
    public TeaUser getCurrentUser(HttpSession session) {
        TeaUser sessionUser = (TeaUser) session.getAttribute("user");
        if (sessionUser == null) return null;
        // 一定要查数据库，保证是最新的
        return userMapper.selectById(sessionUser.getId());
    }

    // 订单创建逻辑增强
    // 1. 创建订单接口
    @PostMapping("/order/create")
    public TeaOrder createOrder(@RequestBody TeaOrder order, HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("请先登录");
        }

        // 补全订单信息
        order.setUserId(user.getId());
        order.setOrderNo("TEA" + System.currentTimeMillis()); // 生成唯一订单号
        order.setStatus(0); // 0: 待支付
        order.setCreateTime(LocalDateTime.now());

        // 保存到数据库
        orderMapper.insert(order);
        return order;
    }

    // 2. 沙箱支付接口 (核心逻辑)
    @PostMapping("/pay/sandbox")
    @Transactional
    public String sandboxPay(@RequestParam String orderNo, HttpSession session) {
        TeaUser sessionUser = (TeaUser) session.getAttribute("user");
        if (sessionUser == null) return "登录已过期，请重新登录";

        // 1. 查订单
        TeaOrder order = orderMapper.selectOne(new LambdaQueryWrapper<TeaOrder>().eq(TeaOrder::getOrderNo, orderNo));
        if (order == null) return "订单不存在";
        if (order.getStatus() == 1) return "该订单已支付";

        // 2. 查数据库中的最新用户（确保余额是最新的）
        TeaUser dbUser = userMapper.selectById(sessionUser.getId());

        // 【关键】防止余额为 null 导致的报错
        if (dbUser.getBalance() == null) {
            dbUser.setBalance(BigDecimal.ZERO);
        }

        // 3. 校验余额
        if (dbUser.getBalance().compareTo(order.getAmount()) < 0) {
            return "支付失败：余额不足 (当前余额: " + dbUser.getBalance() + ")";
        }

        // 4. 扣款与更新状态
        dbUser.setBalance(dbUser.getBalance().subtract(order.getAmount()));
        userMapper.updateById(dbUser); // 更新用户表

        order.setStatus(1); // 设为已支付
        orderMapper.updateById(order); // 更新订单表

        // 5. 更新 Session，方便前端下次获取
        sessionUser.setBalance(dbUser.getBalance());
        session.setAttribute("user", sessionUser);

        return "SUCCESS";
    }
}