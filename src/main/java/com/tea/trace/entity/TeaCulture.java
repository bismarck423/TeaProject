package com.tea.trace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tea_culture")
public class TeaCulture {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;     // 标题（如：大红袍的历史）
    private String category;  // 分类（历史、技艺、品鉴）
    private String content;   // 内容（支持HTML或纯文本）
    private String videoUrl;  // 视频素材链接
    private LocalDateTime createTime;
}