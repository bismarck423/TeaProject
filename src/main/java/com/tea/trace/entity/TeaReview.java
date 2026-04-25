package com.tea.trace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tea_review")
public class TeaReview {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String username;
    private Long teaId;
    private Integer rating;
    private String content;
    private LocalDateTime createTime;
}