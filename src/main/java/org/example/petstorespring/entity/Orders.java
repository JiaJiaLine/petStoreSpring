package org.example.petstorespring.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("orders")
public class Orders {
    // 🌟 主键必须指明真实列名 "orderid"，并设置自增
    @TableId(value = "orderid", type = IdType.AUTO)
    private Integer orderId;

    @TableField("userid")
    private String userId;

    @TableField("orderdate")
    private Date orderDate;

    // 收货信息
    @TableField("shiptofirstname")
    private String shipToFirstName;

    @TableField("shiptolastname")
    private String shipToLastName;

    @TableField("shipaddr1")
    private String shipAddr1;

    @TableField("shipaddr2")
    private String shipAddr2;

    @TableField("shipcity")
    private String shipCity;

    @TableField("shipstate")
    private String shipState;

    @TableField("shipzip")
    private String shipZip;

    @TableField("shipcountry")
    private String shipCountry;

    // 账单信息
    @TableField("billtofirstname")
    private String billToFirstName;

    @TableField("billtolastname")
    private String billToLastName;

    @TableField("billaddr1")
    private String billAddr1;

    @TableField("billaddr2")
    private String billAddr2;

    @TableField("billcity")
    private String billCity;

    @TableField("billstate")
    private String billState;

    @TableField("billzip")
    private String billZip;

    @TableField("billcountry")
    private String billCountry;

    // 其他结算信息
    @TableField("courier")
    private String courier;

    @TableField("totalprice")
    private BigDecimal totalPrice;

    @TableField("creditcard")
    private String creditCard;

    @TableField("exprdate")
    private String exprDate;

    @TableField("cardtype")
    private String cardType;

    @TableField("locale")
    private String locale;

    // 🌟 扩展字段：用来在列表中展示当前状态（不属于 orders 表）
    @TableField(exist = false)
    private String currentStatus;
}