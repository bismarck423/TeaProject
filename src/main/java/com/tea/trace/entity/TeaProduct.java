package com.tea.trace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data // 自动生成 Get/Set 方法
@TableName("tea_product")
public class TeaProduct {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;      // 茶叶名称
    private String origin;    // 产地
    private BigDecimal price; // 价格
    private Integer stock;    // 库存
    private Long categoryId;  // 分类
    private String traceCode; // 溯源码
    private String imageUrl; //茶叶图片
    private String growthEnv;    // 产地环境（土壤、气候）
    private String plantingProcess; // 种植过程（施肥、采摘）
    private String manufacture;  // 制作工艺（杀青、揉捻等）
    private String testReport;   // 检测报告（农残指标）
    private String logistics;    // 物流信息
    private LocalDateTime createTime;
}