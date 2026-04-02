package org.example.petstorespring.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("orderstatus")
public class OrderStatus {
    // 🌟 必须指明真实列名 "orderid" 和 "linenum"，去掉默认的下划线
    @TableField("orderid")
    private Integer orderId;
    @TableField("linenum")
    private Integer lineNum; // 原版设计里用来区分行，通常主状态 linenum 填 0 或 1
    private Date timestamp;
    private String status;   // P = Pending(待发货), S = Shipped(已发货)
}