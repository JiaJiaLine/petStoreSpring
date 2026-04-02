package org.example.petstorespring.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("lineItem")
public class LineItem {
    // 🌟 指明所有带有驼峰命名对应的真实小写列名
    @TableField("orderid")
    private Integer orderId;
    @TableField("linenum")
    private Integer lineNum;

    @TableField("itemid")
    private String itemId;

    private Integer quantity;

    @TableField("unitprice")
    private BigDecimal unitPrice;

    // ----------------------------------------
    // 🌟 计算这行商品的小计 (数量 * 单价)，不需要存数据库，用的时候算一下就行
    @TableField(exist = false)
    private BigDecimal total;

    // 🌟 关联展示商品的详情（比如名字、图片），供前端展示用
    @TableField(exist = false)
    private Item item;
}
