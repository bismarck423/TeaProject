package com.tea.trace.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tea.trace.entity.TeaUser;
import com.tea.trace.mapper.UserMapper;
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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        captcha.setFont(new Font("Verdana", Font.PLAIN, 32));
        request.getSession().setAttribute("captcha", captcha.text().toLowerCase());
        captcha.out(response.getOutputStream());
    }

    @PostMapping("/register")
    public String register(@RequestBody TeaUser user) {
        if (userMapper.selectOne(new LambdaQueryWrapper<TeaUser>().eq(TeaUser::getUsername, user.getUsername())) != null) {
            return "用户名已存在";
        }
        if (user.getBalance() == null) {
            user.setBalance(new BigDecimal("10000.00"));
        }
        // 管理员后门逻辑
        if ("admin".equals(user.getUsername())) {
            user.setRole("ADMIN");
        } else if (user.getRole() == null) {
            user.setRole("USER");
        }
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
        return "SUCCESS";
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData, HttpSession session, HttpServletResponse response) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        String code = loginData.get("code");
        String sessionCode = (String) session.getAttribute("captcha");

        Map<String, Object> result = new HashMap<>();

        // 验证码校验
        if (code == null || !code.equalsIgnoreCase(sessionCode)) {
            response.setStatus(400);
            result.put("message", "验证码错误");
            return result;
        }

        // 账号密码校验
        TeaUser user = userMapper.selectOne(new LambdaQueryWrapper<TeaUser>()
                .eq(TeaUser::getUsername, username)
                .eq(TeaUser::getPassword, password));

        if (user != null) {
            session.setAttribute("user", user);

            result.put("message", "SUCCESS");
            user.setPassword(null);
            result.put("user", user);
            return result;
        }

        // 登录失败
        response.setStatus(400);
        result.put("message", "用户名或密码错误");
        return result;
    }
}