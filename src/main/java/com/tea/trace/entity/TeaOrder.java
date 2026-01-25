package com.tea.trace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data // 必须确保有这个注解，它会自动生成 getUserId() 方法
@TableName("tea_order")
public class TeaOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;

    // 关键修复：必须定义 userId 字段，类型与 TeaUser 的 id 保持一致 (Long)
    private Long userId;

    private BigDecimal amount;
    private Integer status; // 0:待支付, 1:已支付
    private LocalDateTime createTime;
}