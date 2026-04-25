package com.tea.trace.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tea.trace.entity.TeaCart;
import com.tea.trace.entity.TeaProduct;
import com.tea.trace.entity.TeaUser;
import com.tea.trace.mapper.CartMapper;
import com.tea.trace.mapper.TeaMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private TeaMapper teaMapper; // 需要关联查商品信息

    // 1. 加入购物车
    @PostMapping("/add")
    public String addToCart(@RequestParam Long teaId, HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) {
            return "请先登录";
        }

        // 先查查购物车里是不是已经有这款茶了
        TeaCart existCart = cartMapper.selectOne(new LambdaQueryWrapper<TeaCart>()
                .eq(TeaCart::getUserId, user.getId())
                .eq(TeaCart::getTeaId, teaId));

        if (existCart != null) {
            // 已存在，数量 +1
            existCart.setCount(existCart.getCount() + 1);
            cartMapper.updateById(existCart);
        } else {
            // 不存在，新增一条记录
            TeaCart newCart = new TeaCart();
            newCart.setUserId(user.getId());
            newCart.setTeaId(teaId);
            newCart.setCount(1);
            newCart.setCreateTime(LocalDateTime.now());
            cartMapper.insert(newCart);
        }
        return "SUCCESS";
    }

    // 2. 查看我的购物车
    @GetMapping("/list")
    public List<TeaCart> getMyCart(HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) {
            return null;
        }

        // 查出该用户的所有购物车记录
        List<TeaCart> cartList = cartMapper.selectList(new LambdaQueryWrapper<TeaCart>()
                .eq(TeaCart::getUserId, user.getId())
                .orderByDesc(TeaCart::getCreateTime));

        // 遍历记录，实时去商品表抓取最新的名称、价格和图片
        for (TeaCart cart : cartList) {
            TeaProduct product = teaMapper.selectById(cart.getTeaId());
            cart.setTeaProduct(product);
        }

        return cartList;
    }

    // 3. 移除购物车某项 (为下一步前端操作留好接口)
    @PostMapping("/remove")
    public String removeCartItem(@RequestParam Long cartId, HttpSession session) {
        TeaUser user = (TeaUser) session.getAttribute("user");
        if (user == null) {
            return "请先登录";
        }

        cartMapper.deleteById(cartId);
        return "SUCCESS";
    }
}