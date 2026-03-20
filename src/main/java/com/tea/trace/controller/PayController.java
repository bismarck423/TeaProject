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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pay")
public class PayController {

    @Autowired private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired private TeaServiceImpl teaService;

    // 沙箱支付 (仅测试用户可用)
    @PostMapping("/sandbox")
    @Transactional
    public String sandboxPay(@RequestParam String orderNo, HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        // 1. 权限校验
        if (user == null || !"TEST_USER".equals(user.getRole())) {
            return "无权使用沙箱支付";
        }

        TeaOrder order = orderMapper.selectOne(new QueryWrapper<TeaOrder>().eq("order_no", orderNo));
        if (order == null || order.getStatus() != 0) return "订单状态无效";

        // 2. 模拟支付成功
        order.setStatus(1); // 标记支付成功
        order.setIsSandbox(1); // 标记为沙箱订单
        orderMapper.updateById(order);

        // 3. 记录日志 (需新建 SandboxLog 表，这里省略)

        // 4. 特殊逻辑：需求文档提到“测试完成后自动释放库存”
        // 这里我们可以做一个简化：沙箱支付成功后，不扣用户余额，
        // 但为了演示效果，可以选择保留库存扣减（模拟真实）或者立即释放。
        // 根据文档：“沙箱支付订单仅预占库存...不实际扣减”可能指不扣钱。
        // 若要完全符合“测试完自动释放”，可以在这里把库存加回去，或者由定时任务清理。
        // 简单做法：沙箱订单支付成功后，直接回滚库存，模拟测试结束。
        teaService.releaseStock(order.getTeaId(), order.getCount());

        return "沙箱支付测试成功 (库存已回滚)";
    }
}