package org.example.petstorespring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.example.petstorespring.entity.CartItem;
import org.example.petstorespring.entity.Inventory;
import org.example.petstorespring.entity.Item;
import org.example.petstorespring.entity.Product;
import org.example.petstorespring.persistence.CartItemMapper;
import org.example.petstorespring.persistence.ItemMapper;
import org.example.petstorespring.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service("cartService")
public class CartServiceImpl implements CartService {
    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public void addItemToDbCart(String userId, String itemId) {
        // 1. 先查查这件商品是不是已经在购物车里了
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId).eq(CartItem::getItemId, itemId);
        CartItem existingItem = cartItemMapper.selectOne(queryWrapper);

        if (existingItem != null) {
            // 2. 已经在车里了，数量 + 1
            existingItem.setQuantity(existingItem.getQuantity() + 1);

            LambdaUpdateWrapper<CartItem> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(CartItem::getUserId, userId).eq(CartItem::getItemId, itemId);
            cartItemMapper.update(existingItem, updateWrapper); // 🌟 使用 wrapper 更新联合主键

        } else {
            // 3. 没在车里，新建一条记录
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setItemId(itemId);
            newItem.setQuantity(1);
            cartItemMapper.insert(newItem);
        }
    }

    @Override
    public List<CartItem> getDbCart(String userId) {
        // 查出该用户的所有购物车记录
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId);
        List<CartItem> cartItems = cartItemMapper.selectList(wrapper);

        // 🌟 组装展示数据（跨表查 Item 信息和小计）
        for (CartItem cartItem : cartItems) {
            Item item = itemMapper.selectById(cartItem.getItemId());
            cartItem.setItem(item);

            // 计算小计 total = 单价 * 数量
            if (item != null && item.getListPrice() != null) {
                BigDecimal quantity = new BigDecimal(cartItem.getQuantity());
                cartItem.setTotal(item.getListPrice().multiply(quantity));
            }
        }
        return cartItems;
    }

    @Override
    public void updateDbCartQuantities(String userId, Map<String, String[]> parameterMap) {
        // 前端传来的 parameterMap 长这样：{"EST-1": ["2"], "EST-2": ["5"]}
        for (String itemId : parameterMap.keySet()) {
            try {
                int quantity = Integer.parseInt(parameterMap.get(itemId)[0]);
                LambdaUpdateWrapper<CartItem> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(CartItem::getUserId, userId).eq(CartItem::getItemId, itemId);

                if (quantity <= 0) {
                    // 如果用户把数量改成了 0 甚至负数，直接帮他从车里删掉
                    cartItemMapper.delete(wrapper);
                } else {
                    // 更新数量
                    CartItem updateEntity = new CartItem();
                    updateEntity.setQuantity(quantity);
                    cartItemMapper.update(updateEntity, wrapper);
                }
            } catch (NumberFormatException e) {
                // 如果传来的不是数字（比如其他隐藏参数），直接忽略，防报错
            }
        }
    }

    @Override
    public void removeDbCartItem(String userId, String itemId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId).eq(CartItem::getItemId, itemId);
        cartItemMapper.delete(wrapper);
    }

    @Override
    public Item getItemById(String itemId) {
        Item item = itemMapper.selectById(itemId);
        return item;
    }

    @Override
    public void mergeSessionCartToDb(String userId, Map<String, CartItem> sessionCart) {
        if (sessionCart == null || sessionCart.isEmpty()) {
            return; // 车里没东西，直接结束
        }

        for (CartItem sessionItem : sessionCart.values()) {
            // 查一下数据库里是不是已经有这件商品了
            LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CartItem::getUserId, userId).eq(CartItem::getItemId, sessionItem.getItemId());
            CartItem dbItem = cartItemMapper.selectOne(wrapper);

            if (dbItem != null) {
                // 如果数据库里有，数量相加！
                dbItem.setQuantity(dbItem.getQuantity() + sessionItem.getQuantity());
                LambdaUpdateWrapper<CartItem> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(CartItem::getUserId, userId).eq(CartItem::getItemId, sessionItem.getItemId());
                cartItemMapper.update(dbItem, updateWrapper);
            } else {
                // 如果数据库里没有，直接作为新记录插进去！
                CartItem newItem = new CartItem();
                newItem.setUserId(userId);
                newItem.setItemId(sessionItem.getItemId());
                newItem.setQuantity(sessionItem.getQuantity());
                cartItemMapper.insert(newItem);
            }
        }
    }
    @Override
    public void clearDbCart(String userId) {
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, userId);
        cartItemMapper.delete(wrapper);
    }
}
