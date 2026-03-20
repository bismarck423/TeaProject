package com.tea.trace.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tea.trace.entity.TeaOrder;
import com.tea.trace.entity.TeaProduct;
import com.tea.trace.entity.TeaUser;
import com.tea.trace.mapper.OrderMapper;
import com.tea.trace.mapper.TeaMapper;
import com.tea.trace.mapper.UserMapper;
import com.tea.trace.service.TeaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.tea.trace.entity.TeaBanner;
import com.tea.trace.mapper.BannerMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tea")
public class TeaController {

    @Autowired
    private TeaService teaService;
    @Autowired
    private TeaMapper teaMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private BannerMapper bannerMapper;

    // ================== 公共接口 ==================

    @GetMapping("/list")
    public List<TeaProduct> getAllTeas() {
        return teaService.list();
    }
    @GetMapping("/banners")
    public List<TeaBanner> getBanners() {
        return bannerMapper.selectList(new LambdaQueryWrapper<TeaBanner>().orderByDesc(TeaBanner::getSort));
    }

    @GetMapping("/trace/{code}")
    public TeaProduct getByTraceCode(@PathVariable String code) {
        return teaService.lambdaQuery().eq(TeaProduct::getTraceCode, code).one();
    }

    @GetMapping("/user/me")
    public TeaUser getCurrentUser(HttpSession session) {
        TeaUser sessionUser = (TeaUser) session.getAttribute("user");
        if (sessionUser == null) return null;
        return userMapper.selectById(sessionUser.getId());
    }

    // ================== 订单与支付 (用户端) ==================

    @PostMapping("/banner/save")
    public String saveBanner(@RequestBody TeaBanner banner, HttpSession session) {
        checkAdmin(session); // 记得保留之前的 checkAdmin 方法
        if (banner.getId() == null) {
            bannerMapper.insert(banner);
        } else {
            bannerMapper.updateById(banner);
        }
        return "SUCCESS";
    }

    @PostMapping("/banner/delete")
    public String deleteBanner(@RequestParam Long id, HttpSession session) {
        checkAdmin(session);
        bannerMapper.deleteById(id);
        return "SUCCESS";
    }

    @PostMapping("/order/create")
    public TeaOrder createOrder(@RequestBody TeaOrder order, HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) throw new RuntimeException("请先登录");

        order.setUserId(user.getId());
        order.setOrderNo("TEA" + System.currentTimeMillis());
        order.setStatus(0); // 0: 待支付
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);
        return order;
    }

    @GetMapping("/user/orders")
    public List<TeaOrder> getMyOrders(HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) return null;
        return orderMapper.selectList(new LambdaQueryWrapper<TeaOrder>()
                .eq(TeaOrder::getUserId, user.getId())
                .orderByDesc(TeaOrder::getCreateTime));
    }

    @PostMapping("/pay/sandbox")
    @Transactional
    public String sandboxPay(@RequestParam String orderNo, HttpSession session) {
        TeaUser sessionUser = (TeaUser) session.getAttribute("user");
        if (sessionUser == null) return "登录已过期";

        TeaOrder order = orderMapper.selectOne(new LambdaQueryWrapper<TeaOrder>().eq(TeaOrder::getOrderNo, orderNo));
        if (order == null) return "订单不存在";
        if (order.getStatus() != 0) return "订单状态异常";

        TeaUser dbUser = userMapper.selectById(sessionUser.getId());
        if (dbUser.getBalance() == null) dbUser.setBalance(BigDecimal.ZERO);

        if (dbUser.getBalance().compareTo(order.getAmount()) < 0) {
            return "余额不足，请联系管理员充值";
        }

        // 扣款
        dbUser.setBalance(dbUser.getBalance().subtract(order.getAmount()));
        userMapper.updateById(dbUser);

        // 更新订单
        order.setStatus(1);
        order.setExpressInfo("顺丰速运: SF" + order.getOrderNo().substring(5) + " [运输中]");// 1: 已支付/待发货
        orderMapper.updateById(order);

        // 刷新session
        sessionUser.setBalance(dbUser.getBalance());
        session.setAttribute("user", sessionUser);

        return "SUCCESS";
    }

    // ================== 管理员接口 (Admin) ==================

    // 1. 获取所有用户
    @GetMapping("/admin/users")
    public List<TeaUser> getAllUsers(HttpSession session) {
        checkAdmin(session);
        return userMapper.selectList(null);
    }

    // 2. 商品管理：新增/修改
    @PostMapping("/product/save")
    public String saveProduct(@RequestBody TeaProduct product, HttpSession session) {
        checkAdmin(session);
        if (product.getId() == null) {
            product.setCreateTime(LocalDateTime.now());
            // 自动生成唯一溯源码
            product.setTraceCode("TR" + System.currentTimeMillis());
            teaMapper.insert(product);
        } else {
            teaMapper.updateById(product);
        }
        return "SUCCESS";
    }

    // 3. 商品管理：删除
    @PostMapping("/product/delete")
    public String deleteProduct(@RequestParam Long id, HttpSession session) {
        checkAdmin(session);
        teaMapper.deleteById(id);
        return "SUCCESS";
    }

    // 4. 订单管理：获取所有订单
    @GetMapping("/admin/orders")
    public List<TeaOrder> getAllOrders(HttpSession session) {
        checkAdmin(session);
        return orderMapper.selectList(new LambdaQueryWrapper<TeaOrder>().orderByDesc(TeaOrder::getCreateTime));
    }

    // 5. 订单管理：发货
    @PostMapping("/order/ship")
    public String shipOrder(@RequestParam Long orderId, HttpSession session) {
        checkAdmin(session);
        TeaOrder order = orderMapper.selectById(orderId);
        if (order != null && order.getStatus() == 1) { // 只有已支付的可以发货
            order.setStatus(2); // 2: 已发货
            orderMapper.updateById(order);
            return "SUCCESS";
        }
        return "订单状态不满足发货条件";
    }

    // 6. 数据看板统计 (Mock数据，真实开发需写复杂SQL)
    @GetMapping("/admin/stats")
    public Map<String, Object> getStats(HttpSession session) {
        checkAdmin(session);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSales", orderMapper.selectCount(new LambdaQueryWrapper<TeaOrder>().eq(TeaOrder::getStatus, 1))); // 销量
        stats.put("userCount", userMapper.selectCount(null)); // 用户数
        stats.put("productCount", teaMapper.selectCount(null)); // 商品数
        // 模拟近7天销售额
        stats.put("chartData", new int[]{120, 200, 150, 80, 70, 110, 130});
        return stats;
    }

    private void checkAdmin(HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("无权访问");
        }
    }
}