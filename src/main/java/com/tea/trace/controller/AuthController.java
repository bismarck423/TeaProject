package com.tea.trace.controller; // 必须与目录对应

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tea.trace.entity.TeaUser;
import com.tea.trace.mapper.UserMapper;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.SpecCaptcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 将 ArithmeticCaptcha 换成 SpecCaptcha (普通字符验证码)
        // 三个参数分别为：宽、高、字符位数
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);

        // 设置字体（可选）
        captcha.setFont(new Font("Verdana", Font.PLAIN, 32));

        // 验证码存入 session
        request.getSession().setAttribute("captcha", captcha.text().toLowerCase());

        // 输出图片流
        captcha.out(response.getOutputStream());
    }


    @PostMapping("/register")
    public String register(@RequestBody TeaUser user) {
        // 1. 检查重名
        if (userMapper.selectOne(new LambdaQueryWrapper<TeaUser>().eq(TeaUser::getUsername, user.getUsername())) != null) {
            return "用户名已存在";
        }

        // 2. 【关键修改】初始化用户信息
        if (user.getBalance() == null) {
            user.setBalance(new BigDecimal("10000.00")); // 赠送1万元体验金
        }
        if (user.getRole() == null) {
            user.setRole("USER"); // 默认角色
        }
        user.setCreateTime(LocalDateTime.now()); // 设置注册时间

        // 3. 存入数据库
        userMapper.insert(user);
        return "SUCCESS";
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> loginData, HttpSession session) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        String code = loginData.get("code");
        String sessionCode = (String) session.getAttribute("captcha");

        // 验证码校验
        if (code == null || !code.equalsIgnoreCase(sessionCode)) {
            return "验证码错误";
        }

        // 账号密码校验 [cite: 1]
        TeaUser user = userMapper.selectOne(new LambdaQueryWrapper<TeaUser>()
                .eq(TeaUser::getUsername, username)
                .eq(TeaUser::getPassword, password));

        if (user != null) {
            session.setAttribute("user", user); // 存入登录状态
            return "SUCCESS";
        }
        return "用户名或密码错误";
    }
}