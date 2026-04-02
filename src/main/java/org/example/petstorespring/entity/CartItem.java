package org.example.petstorespring.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("cartitem")
public class CartItem {
    // 数据库里的真实字段
    @TableField("userid")
    private String userId;
    @TableField("itemid")
    private String itemId;
    private Integer quantity;

    // 🌟 以下是为了前端页面展示方便，临时挂载的扩展字段（不存进 cart_item 表）

    @TableField(exist = false)
    private Item item; // 查出具体的宠物信息（名字、图片、单价等）

    @TableField(exist = false)
    private BigDecimal total; // 算好的小计：单价 * 数量
}
