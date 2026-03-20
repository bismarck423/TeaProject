package com.tea.trace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tea_banner")
public class TeaBanner {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String imageUrl; // 图片路径
    private String title;    // 轮播标题
    private Integer sort;    // 排序权重
}