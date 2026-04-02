package org.example.petstorespring.service;

import org.example.petstorespring.entity.CartItem;
import org.example.petstorespring.entity.Item;

import java.util.List;
import java.util.Map;

public interface CartService {
    // 专门给已登录用户用的：添加商品到数据库购物车
    void addItemToDbCart(String userId, String itemId);

    // 查询用户的数据库购物车（顺便查出商品详细信息）
    List<CartItem> getDbCart(String userId);

    // 批量更新数据库购物车数量
    void updateDbCartQuantities(String userId, Map<String, String[]> parameterMap);
    // 从数据库移除某件商品
    void removeDbCartItem(String userId, String itemId);
    Item getItemById(String itemId);

    // 将 Session 购物车合并到数据库购物车
    void mergeSessionCartToDb(String userId, Map<String, CartItem> sessionCart);
    // 清空指定用户的数据库购物车
    void clearDbCart(String userId);
}
