package org.example.petstorespring.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.petstorespring.entity.LineItem;
import org.example.petstorespring.entity.Orders;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true) // 加上这个注解，Lombok 会自动处理父类属性的比较
public class OrderVO extends Orders {
    // 🌟 专门为前端展示附加的明细列表
    private List<LineItem> lineItems;

    // 🌟 专门为前端展示附加的当前状态 (比如 "P" 或 "S")
    private String currentStatus;
}