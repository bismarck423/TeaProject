package com.tea.trace.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tea.trace.entity.*;
import com.tea.trace.mapper.*;
import com.tea.trace.service.TeaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private TeaCultureMapper cultureMapper;

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
        if (sessionUser == null) {
            return null;
        }
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
        if (user == null) {
            throw new RuntimeException("请先登录");
        }

        order.setUserId(user.getId());
        order.setOrderNo("TEA" + System.currentTimeMillis());
        order.setStatus(0); // 0: 待支付
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);
        return order;
    }

    @GetMapping("/user/orders")
    public List<TeaOrder> getMyOrders(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status,
            HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) {
            return null;
        }

        LambdaQueryWrapper<TeaOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeaOrder::getUserId, user.getId()); // 强制隔离：只能查自己的

        if (StringUtils.hasText(orderNo)) {
            wrapper.like(TeaOrder::getOrderNo, orderNo.trim());
        }
        if (status != null) {
            wrapper.eq(TeaOrder::getStatus, status);
        }

        wrapper.orderByDesc(TeaOrder::getCreateTime);
        return orderMapper.selectList(wrapper);
    }

    @PostMapping("/pay/sandbox")
    @Transactional
    public String sandboxPay(@RequestParam String orderNo, HttpSession session) {
        TeaUser sessionUser = (TeaUser) session.getAttribute("user");
        if (sessionUser == null) {
            return "登录已过期";
        }

        TeaOrder order = orderMapper.selectOne(new LambdaQueryWrapper<TeaOrder>().eq(TeaOrder::getOrderNo, orderNo));
        if (order == null) {
            return "订单不存在";
        }
        if (order.getStatus() != 0) {
            return "订单状态异常";
        }

        TeaUser dbUser = userMapper.selectById(sessionUser.getId());
        if (dbUser.getBalance() == null) {
            dbUser.setBalance(BigDecimal.ZERO);
        }

        if (dbUser.getBalance().compareTo(order.getAmount()) < 0) {
            return "余额不足，请联系管理员充值";
        }

        // 扣款
        dbUser.setBalance(dbUser.getBalance().subtract(order.getAmount()));
        userMapper.updateById(dbUser);

        // 更新订单
        order.setStatus(1);

        // --- 新增：生成详细的虚拟物流信息 ---
        String logisticsCompany = "顺丰速运";
        String trackingNum = "SF" + System.currentTimeMillis();
        String origin = "福建省武夷山市原产地发货仓";
        String destination = "用户默认收货地址"; // 这里为了演示使用占位，如果你的系统有地址表可以替换
        String estTime = LocalDateTime.now().plusDays(3).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        String detailExpress = String.format("【%s】单号：%s \n[发件] %s \n[收件] %s \n[预计送达] %s \n[当前状态] 揽件包装中",
                logisticsCompany, trackingNum, origin, destination, estTime);
        order.setExpressInfo(detailExpress);
        // ---------------------------------

        orderMapper.updateById(order);

        // 刷新session
        sessionUser.setBalance(dbUser.getBalance());
        session.setAttribute("user", sessionUser);

        return "SUCCESS";
    }

    // ================== 管理员接口 (Admin) ==================

    @GetMapping("/admin/users")
    public List<TeaUser> getAllUsers(HttpSession session) {
        checkAdmin(session);
        return userMapper.selectList(null);
    }


    @PostMapping("/product/save")
    public String saveProduct(@RequestBody TeaProduct product, HttpSession session) {
        checkAdmin(session);

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new RuntimeException("商品名称不能为空");
        }
        if (product.getCategoryId() == null) {
            throw new RuntimeException("请选择关联的商品分类");
        }

        if (product.getId() == null) {
            product.setCreateTime(LocalDateTime.now());
            product.setTraceCode("TR" + System.currentTimeMillis());
            teaMapper.insert(product);
        } else {
            teaMapper.updateById(product);
        }
        return "SUCCESS";
    }

    @PostMapping("/product/delete")
    public String deleteProduct(@RequestParam Long id, HttpSession session) {
        checkAdmin(session);
        teaMapper.deleteById(id);
        return "SUCCESS";
    }

    @GetMapping("/admin/orders")
    public List<TeaOrder> getOrders(@RequestParam(required = false) String orderNo,
                                    @RequestParam(required = false) Integer status,
                                    HttpSession session) {
        checkAdmin(session); // 鉴权不变

        LambdaQueryWrapper<TeaOrder> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(orderNo)) {
            wrapper.like(TeaOrder::getOrderNo, orderNo.trim());
        }
        if (status != null) {
            wrapper.eq(TeaOrder::getStatus, status);
        }

        wrapper.orderByDesc(TeaOrder::getCreateTime);
        return orderMapper.selectList(wrapper);
    }

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

    @GetMapping("/admin/stats")
    public Map<String, Object> getStats(HttpSession session) {
        checkAdmin(session);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSales", orderMapper.selectCount(new LambdaQueryWrapper<TeaOrder>().eq(TeaOrder::getStatus, 1))); // 销量
        stats.put("userCount", userMapper.selectCount(null)); // 用户数
        stats.put("productCount", teaMapper.selectCount(null)); // 商品数
        stats.put("chartData", new int[]{120, 200, 150, 80, 70, 110, 130});
        return stats;
    }

    @PostMapping("/admin/order/updateExpress")
    public String updateExpress(@RequestParam Long orderId, @RequestParam String expressInfo, HttpSession session) {
        checkAdmin(session);
        TeaOrder order = orderMapper.selectById(orderId);
        if (order != null) {
            order.setExpressInfo(expressInfo);
            orderMapper.updateById(order);
            return "SUCCESS";
        }
        return "订单不存在";
    }

    @GetMapping("/culture/list")
    public List<TeaCulture> getCultureList() {
        return cultureMapper.selectList(new LambdaQueryWrapper<TeaCulture>().orderByDesc(TeaCulture::getCreateTime));
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

    @PostMapping("/user/recharge")
    public String recharge(@RequestParam BigDecimal amount, HttpSession session) {
        TeaUser sessionUser = (TeaUser) session.getAttribute("user");
        if (sessionUser == null) {
            return "请先登录";
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "充值金额不合法";
        }

        TeaUser dbUser = userMapper.selectById(sessionUser.getId());
        dbUser.setBalance(dbUser.getBalance().add(amount));
        userMapper.updateById(dbUser);

        sessionUser.setBalance(dbUser.getBalance());
        session.setAttribute("user", sessionUser);
        return "SUCCESS";
    }

    private void checkAdmin(HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("无权访问");
        }
    }
}