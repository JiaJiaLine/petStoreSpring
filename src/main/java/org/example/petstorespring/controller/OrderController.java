package org.example.petstorespring.controller;

import org.example.petstorespring.entity.Order;
import org.example.petstorespring.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    public ModelAndView listOrders(){
        List<Order> orders = orderService.getAllOrders();

        ModelAndView mv=new ModelAndView("order_list");
        mv.addObject("orders", orders);
        return mv;
    }
}
