package org.example.petstorespring.service;

import org.example.petstorespring.entity.Orders;
import org.example.petstorespring.vo.LoginAccountVO;

import java.util.List;

public interface OrderService {
    // 核心大招：生成订单（传入当前登录用户，以及前端填好的收货/账单信息）
    Orders createOrder(LoginAccountVO loginAccount, Orders order);
    // 查询某个用户的所有订单（按时间倒序）
    List<Orders> getOrdersByUserId(String username);

    // 查询单个订单的详细信息（包含关联的 LineItem 和商品详情）
    Orders getOrderById(Integer orderId);

    List<Orders> getAllOrders();
    void updateOrderStatus(Integer orderId, String status);
}