package org.example.petstorespring.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.petstorespring.entity.Account;
import org.example.petstorespring.entity.CartItem;
import org.example.petstorespring.entity.Item;
import org.example.petstorespring.service.CartService;
import org.example.petstorespring.service.CatalogService;
import org.example.petstorespring.service.ManageService;
import org.example.petstorespring.service.impl.CatalogServiceImpl;
import org.example.petstorespring.vo.ItemVO;
import org.example.petstorespring.vo.LoginAccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/addItemToCart")
    public String addItemToCart(@RequestParam("workingItemId") String itemId, HttpSession session) {
        // 1. 检查用户是否登录（假设你登录成功后存的是 "loginAccount"）
        LoginAccountVO loginAccount = (LoginAccountVO) session.getAttribute("loginAccount");

        if (loginAccount != null) {
            // 🌟 路线 A：已登录，直接存进数据库
            cartService.addItemToDbCart(loginAccount.getUsername(), itemId);
        } else {
            // 🌟 路线 B：未登录，存进 Session 的 Map 里
            // 先尝试从 Session 拿临时购物车
            Map<String, CartItem> sessionCart = (Map<String, CartItem>) session.getAttribute("sessionCart");
            if (sessionCart == null) {
                sessionCart = new HashMap<>(); // 没有就给他发一辆新车
            }

            if (sessionCart.containsKey(itemId)) {
                CartItem cartItem = sessionCart.get(itemId);
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                // 更新 Session 里的小计
                cartItem.setTotal(cartItem.getItem().getListPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            } else {
                Item item = cartService.getItemById(itemId); // 🌟 使用 CatalogService
                CartItem newItem = new CartItem();
                newItem.setItemId(itemId);
                newItem.setQuantity(1);
                newItem.setItem(item);
                newItem.setTotal(item.getListPrice());
                sessionCart.put(itemId, newItem);
            }

            // 把更新后的车重新塞回 Session
            session.setAttribute("sessionCart", sessionCart);
        }

        // 添加完毕后，直接跳转到“查看购物车”页面
        return "redirect:/cart/viewCart";
    }

    @GetMapping("/viewCart")
    public String viewCart(HttpSession session, Model model) {
        LoginAccountVO loginAccount = (LoginAccountVO) session.getAttribute("loginAccount");
        List<CartItem> cartItemList = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;

        if (loginAccount != null) {
            // 🌟 规范路线 A：已登录，去数据库里捞数据（调用你之前写的 Service）
            cartItemList = cartService.getDbCart(loginAccount.getUsername());
        } else {
            // 🌟 规范路线 B：未登录，从 Session 的 Map 里捞数据
            Map<String, CartItem> sessionCart = (Map<String, CartItem>) session.getAttribute("sessionCart");
            if (sessionCart != null) {
                // 把 Map 的 Value 提取出来变成 List
                cartItemList = new ArrayList<>(sessionCart.values());
            }
        }

        // 统一计算总价 (SubTotal)
        for (CartItem cartItem : cartItemList) {
            if (cartItem.getTotal() != null) {
                subTotal = subTotal.add(cartItem.getTotal());
            }
        }

        // 把纯净的数据塞给前端 Model
        model.addAttribute("cartItemList", cartItemList);
        model.addAttribute("subTotal", subTotal);

        return "cart/cart";
    }


    // ================= 批量更新购物车数量 =================
    @PostMapping("/updateCart")
    public String updateCart(HttpServletRequest request, HttpSession session) {
        LoginAccountVO loginAccount = (LoginAccountVO) session.getAttribute("loginAccount");

        // 🌟 神仙操作：一次性抓取前端 <form> 里所有的 input 数据！
        Map<String, String[]> parameterMap = request.getParameterMap();

        if (loginAccount != null) {
            // 路线 A：已登录，交给 Service 更新数据库
            cartService.updateDbCartQuantities(loginAccount.getUsername(), parameterMap);
        } else {
            // 路线 B：未登录，更新 Session 里的 Map
            Map<String, CartItem> sessionCart = (Map<String, CartItem>) session.getAttribute("sessionCart");
            if (sessionCart != null) {
                for (String itemId : parameterMap.keySet()) {
                    try {
                        int qty = Integer.parseInt(parameterMap.get(itemId)[0]);
                        if (sessionCart.containsKey(itemId)) {
                            if (qty <= 0) {
                                // 数量不合法，直接移除
                                sessionCart.remove(itemId);
                            } else {
                                // 更新数量，并重新计算这个商品的小计 (Total)
                                CartItem ci = sessionCart.get(itemId);
                                ci.setQuantity(qty);
                                if (ci.getItem() != null && ci.getItem().getListPrice() != null) {
                                    ci.setTotal(ci.getItem().getListPrice().multiply(new BigDecimal(qty)));
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        // 忽略非数字的干扰参数
                    }
                }
                // 把更新后的 Map 重新放回 Session
                session.setAttribute("sessionCart", sessionCart);
            }
        }
        // 更新完直接刷新购物车页面
        return "redirect:/cart/viewCart";
    }

    // ================= 移除单件商品 =================
    @GetMapping("/removeItem")
    public String removeItem(@RequestParam("workingItemId") String itemId, HttpSession session) {
        LoginAccountVO loginAccount = (LoginAccountVO) session.getAttribute("loginAccount");

        if (loginAccount != null) {
            // 路线 A：已登录，从数据库删除
            cartService.removeDbCartItem(loginAccount.getUsername(), itemId);
        } else {
            // 路线 B：未登录，从 Session Map 中剔除
            Map<String, CartItem> sessionCart = (Map<String, CartItem>) session.getAttribute("sessionCart");
            if (sessionCart != null) {
                sessionCart.remove(itemId);
                session.setAttribute("sessionCart", sessionCart);
            }
        }
        // 删完直接刷新购物车页面
        return "redirect:/cart/viewCart";
    }
}
