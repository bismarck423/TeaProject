package com.tea.trace.controller;

import com.tea.trace.entity.TeaCategory;
import com.tea.trace.entity.TeaUser;
import com.tea.trace.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public List<TeaCategory> list() {
        return categoryService.list();
    }

    @PostMapping("/save")
    public String save(@RequestBody TeaCategory category, HttpSession session) {
        if (!isAdmin(session)) {
            return "无权操作：需要管理员权限";
        }

        if (category.getId() == null) {
            category.setCreateTime(LocalDateTime.now());
        }
        boolean success = categoryService.saveOrUpdate(category);
        return success ? "SUCCESS" : "操作失败";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return "无权操作：需要管理员权限";
        }

        boolean success = categoryService.removeById(id);
        return success ? "SUCCESS" : "删除失败";
    }

    private boolean isAdmin(HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }
}