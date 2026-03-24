package org.example.petstorespring.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("item")
public class Item {
    @TableId("itemid")
    private String itemId;
    @TableField("productid")
    private String productId;
    @TableField("listprice")
    private String listPrice;
    @TableField("unitcost")
    private String unitCost;
    private String supplier;
    private String status;
    @TableField("attr1")
    private String attribute1;
    @TableField("attr2")
    private String attribute2;
    @TableField("attr3")
    private String attribute3;
    @TableField("attr4")
    private String attribute4;
    @TableField("attr5")
    private String attribute5;
}
