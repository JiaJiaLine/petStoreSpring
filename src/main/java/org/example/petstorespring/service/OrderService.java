package org.example.petstorespring.service;

import org.example.petstorespring.entity.Order;
import java.util.List;

// 把 class 改成 interface ✅
public interface OrderService {

    // 只保留方法声明，不要大括号和实现代码
    List<Order> getAllOrders();

}