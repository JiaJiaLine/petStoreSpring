package org.example.petstorespring.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petstorespring.entity.CartItem;
import org.example.petstorespring.entity.Orders;
import org.example.petstorespring.service.CartService;
import org.example.petstorespring.service.OrderService;
import org.example.petstorespring.vo.LoginAccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    // ================= 1. 进入结算页面 (填写地址和信用卡) =================
    @GetMapping("/newOrder")
    public String newOrderForm(HttpSession session, Model model) {
        // 🌟 核心：结账必须登录！
        LoginAccountVO loginAccount = (LoginAccountVO) session.getAttribute("loginAccount");
        if (loginAccount == null) {
            return "redirect:/account/viewSignon"; // 没登录的先去登录
        }

        // 1. 获取要结算的商品列表
        List<CartItem> cartItemList = cartService.getDbCart(loginAccount.getUsername());
        if (cartItemList.isEmpty()) {
            return "redirect:/cart/viewCart"; // 车空了回购物车
        }

        // 2. 计算总价
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartItem ci : cartItemList) {
            subTotal = subTotal.add(ci.getTotal());
        }

        // 3. 准备一个空的 Orders 对象给前端绑定表单
        Orders order = new Orders();
        // 🌟 这里的细节：可以预填一些账号里的默认收货信息（如果你 Account 里存了的话）
        // order.setShipAddr1(loginAccount.getAddress1());

        model.addAttribute("order", order);
        model.addAttribute("cartItemList", cartItemList);
        model.addAttribute("subTotal", subTotal);

        return "order/newOrder"; // 跳转到 newOrder.html
    }

    // ================= 2. 确认并提交订单 =================
    @PostMapping("/confirmOrder")
    public String confirmOrder(Orders order, HttpSession session, RedirectAttributes redirectAttributes) {
        LoginAccountVO loginAccount = (LoginAccountVO) session.getAttribute("loginAccount");
        if (loginAccount == null) return "redirect:/account/loginForm";

        // 🌟 移花接木：把填好的 Ship 地址直接复制给 Bill 地址，应付数据库的 NOT NULL 检查
        order.setBillToFirstName(order.getShipToFirstName());
        order.setBillToLastName(order.getShipToLastName());
        order.setBillAddr1(order.getShipAddr1());
        order.setBillAddr2(order.getShipAddr2());
        order.setBillCity(order.getShipCity());
        order.setBillState(order.getShipState());
        order.setBillZip(order.getShipZip());
        order.setBillCountry(order.getShipCountry());

        order.setCourier("UPS"); // 顺手补一个快递公司默认值
        order.setLocale("en_US");

        try {
            // 🌟 调用 OrderService 的事务大招！
            Orders savedOrder = orderService.createOrder(loginAccount, order);

            // 下单成功，规范的做法是重定向到详情页，防止用户按 F5 刷新导致重复提交订单
            return "redirect:/order/viewOrder?orderId=" + savedOrder.getOrderId();

        } catch (Exception e) {
            // 🚨 极其重要：在控制台把真实的报错原因打印出来！
            e.printStackTrace();

            // 如果出错，使用 RedirectAttributes 把错误信息带给重定向后的页面
            redirectAttributes.addFlashAttribute("msg", "Failed to create order: " + e.getMessage());
            return "redirect:/cart/viewCart";
        }
    }

    // ================= 3. 查看我的订单列表 =================
    @GetMapping("/listOrders")
    public String listOrders(HttpSession session, Model model) {
        LoginAccountVO loginAccount = (LoginAccountVO) session.getAttribute("loginAccount");
        if (loginAccount == null) return "redirect:/account/loginForm";

        List<Orders> orderList = orderService.getOrdersByUserId(loginAccount.getUsername());
        model.addAttribute("orderList", orderList);

        return "order/listOrders";
    }

    // ================= 4. 查看单个订单详情 =================
    @GetMapping("/viewOrder")
    public String viewOrder(@RequestParam("orderId") Integer orderId, HttpSession session, Model model) {
        LoginAccountVO loginAccount = (LoginAccountVO) session.getAttribute("loginAccount");
        if (loginAccount == null) return "redirect:/account/loginForm";

        // 调用我们刚刚写好的超级组装大招
        Orders order = orderService.getOrderById(orderId);

        // 安全校验：防止别人通过修改 URL 参数偷看其他人的订单
        if (order == null || !order.getUserId().equals(loginAccount.getUsername())) {
            return "redirect:/order/listOrders";
        }

        model.addAttribute("order", order);
        return "order/viewOrder";
    }
}
