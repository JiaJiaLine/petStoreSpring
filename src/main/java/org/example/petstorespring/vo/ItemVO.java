package org.example.petstorespring.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemVO {
    // 1. Item 基本信息
    private String itemId;
    private String productId;
    private BigDecimal listPrice;
    private String attribute1;
    private String attribute2;
    private String attribute3;
    private String attribute4;
    private String attribute5;

    // 2. 关联的 Product 信息
    private String productName;
    private String categoryId;

    // 3. 核心：正则切割后的描述
    private String descriptionImage; // 存放图片路径，如 "images/bird4.gif"
    private String descriptionText;  // 存放文字内容

    // 4. 库存信息 (来自 inventory 表)
    private Integer quantity;

    private String status;
    private int supplier;
}
