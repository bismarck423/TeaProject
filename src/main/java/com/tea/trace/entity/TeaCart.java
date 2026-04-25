package com.tea.trace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tea_cart")
public class TeaCart {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long teaId;
    private Integer count;
    private LocalDateTime createTime;

    // 非数据库字段，用于向前端返回商品详情（图片、名称、最新价格等）
    @TableField(exist = false)
    private TeaProduct teaProduct;
}