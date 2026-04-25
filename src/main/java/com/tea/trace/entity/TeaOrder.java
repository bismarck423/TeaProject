package com.tea.trace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tea_order")
public class TeaOrder {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;    // 订单号
    private Long userId;       // 用户ID
    private Long teaId;        // 商品ID
    private String teaName;    // 商品名称快照
    private Integer count;     // 购买数量
    private Integer isSandbox; // 0:真实订单, 1:沙箱订单
    private BigDecimal amount; // 总金额
    private Integer status;    // 0:待支付, 1:已支付, 2:已取消, 3:已发货
    private String payType;    // 支付方式
    private String expressInfo;
    private LocalDateTime createTime;

    @TableLogic
    private Integer isDeleted; //MyBatis-Plus
}