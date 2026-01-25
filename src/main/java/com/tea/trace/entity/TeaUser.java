package com.tea.trace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tea_user")
public class TeaUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    // 权限角色：ADMIN, USER, TEST_USER
    private String role;
    private BigDecimal balance;
    private LocalDateTime createTime;
}