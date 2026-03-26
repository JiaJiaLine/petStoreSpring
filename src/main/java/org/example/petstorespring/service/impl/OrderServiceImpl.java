package org.example.petstorespring.service.impl;

import org.example.petstorespring.entity.Order;
import org.example.petstorespring.persistence.OrderMapper;
import org.example.petstorespring.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<Order> getAllOrders() {
        return orderMapper.getAllOrders();
    }
}