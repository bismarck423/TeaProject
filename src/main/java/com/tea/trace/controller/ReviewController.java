package com.tea.trace.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tea.trace.entity.TeaReview;
import com.tea.trace.entity.TeaUser;
import com.tea.trace.mapper.ReviewMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewMapper reviewMapper;

    // 添加评价
    @PostMapping("/add")
    public String addReview(@RequestBody TeaReview review, HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) {
            return "请先登录";
        }

        review.setUserId(user.getId());
        review.setUsername(user.getUsername());
        review.setCreateTime(LocalDateTime.now());
        reviewMapper.insert(review);
        return "SUCCESS";
    }

    // 获取某个商品的评价列表
    @GetMapping("/list")
    public List<TeaReview> getReviews(@RequestParam Long teaId) {
        return reviewMapper.selectList(new LambdaQueryWrapper<TeaReview>()
                .eq(TeaReview::getTeaId, teaId)
                .orderByDesc(TeaReview::getCreateTime)); // 按时间倒序
    }
}