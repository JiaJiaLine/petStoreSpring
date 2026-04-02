package org.example.petstorespring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.example.petstorespring.entity.*;
import org.example.petstorespring.persistence.*;
import org.example.petstorespring.service.CartService;
import org.example.petstorespring.service.OrderService;
import org.example.petstorespring.vo.LoginAccountVO;
import org.example.petstorespring.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private LineItemMapper lineItemMapper;
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private CartService cartService;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional // 🌟 史诗级护盾：保证以下 5 步要么全部成功，要么全部失败回滚！
    public Orders createOrder(LoginAccountVO loginAccount, Orders order) {

        // 1. 获取该用户在数据库里的购物车清单
        List<CartItem> cartItemList = cartService.getDbCart(loginAccount.getUsername());
        if (cartItemList == null || cartItemList.isEmpty()) {
            throw new RuntimeException("Cart is empty, cannot create order!");
        }

        // ================= 动作 1：插入 Orders 主表 =================
        order.setUserId(loginAccount.getUsername());
        order.setOrderDate(new Date());

        // 在后端重新安全地计算一次总价，防止前端篡改数据
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem ci : cartItemList) {
            if (ci.getTotal() != null) {
                totalPrice = totalPrice.add(ci.getTotal());
            }
        }
        order.setTotalPrice(totalPrice);

        // 执行插入！(前提：你的 Orders 实体类的主键 orderId 必须加上 @TableId(type = IdType.AUTO) 以获取自增 ID)
        ordersMapper.insert(order);
        Integer newOrderId = order.getOrderId(); // 此时 MyBatis-Plus 会自动把生成的 ID 塞回对象里

        // ================= 动作 2：插入 OrderStatus 状态表 =================
        OrderStatus status = new OrderStatus();
        status.setOrderId(newOrderId);
        status.setLineNum(1);
        status.setTimestamp(new Date());
        status.setStatus("P"); // P = Pending (待处理/待发货)
        orderStatusMapper.insert(status);

        // ================= 动作 3 & 4：循环插入明细并扣减库存 =================
        for (int i = 0; i < cartItemList.size(); i++) {
            CartItem cartItem = cartItemList.get(i);

            // 动作 3：插入 LineItem
            LineItem lineItem = new LineItem();
            lineItem.setOrderId(newOrderId);
            lineItem.setLineNum(i + 1); // 订单里的第几行商品
            lineItem.setItemId(cartItem.getItemId());
            lineItem.setQuantity(cartItem.getQuantity());
            lineItem.setUnitPrice(cartItem.getItem().getListPrice());
            lineItemMapper.insert(lineItem);

            // 动作 4：真实扣减库存 (防超卖的高级逻辑未来可以加在这里)
            Inventory inventory = inventoryMapper.selectById(cartItem.getItemId());
            if (inventory != null) {
                int newQty = inventory.getQuantity() - cartItem.getQuantity();
                inventory.setQuantity(Math.max(newQty, 0)); // 兜底防止变负数
                inventoryMapper.updateById(inventory);
            }
        }

        // ================= 动作 5：清空购物车 =================
        cartService.clearDbCart(loginAccount.getUsername());

        return order; // 返回带有完整 orderId 的订单对象
    }

    @Override
    public List<Orders> getOrdersByUserId(String username) {
        // 按下单时间倒序排列，最新的订单在最前面
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, username).orderByDesc(Orders::getOrderDate);
        return ordersMapper.selectList(wrapper);
    }

    @Override
    public OrderVO getOrderById(Integer orderId) {
        OrderVO orderVO = new OrderVO();
        // 1. 查出订单主表
        Orders order = ordersMapper.selectById(orderId);
        org.springframework.beans.BeanUtils.copyProperties(order, orderVO);
        if (order != null) {
            // 2. 查出订单状态
            LambdaQueryWrapper<OrderStatus> statusWrapper = new LambdaQueryWrapper<>();
            statusWrapper.eq(OrderStatus::getOrderId, orderId);
            OrderStatus status = orderStatusMapper.selectOne(statusWrapper);
            if (status != null) {
                order.setCurrentStatus(status.getStatus());
            }

            // 3. 查出该订单下所有的明细 (LineItem)
            LambdaQueryWrapper<LineItem> lineWrapper = new LambdaQueryWrapper<>();
            lineWrapper.eq(LineItem::getOrderId, orderId);
            List<LineItem> lineItems = lineItemMapper.selectList(lineWrapper);

            // 4. 遍历明细，把具体的商品名字和单行小计补全
            for (LineItem lineItem : lineItems) {
                // 🌟 直接用 Mapper 查基础 Item，最稳妥！
                Item item = itemMapper.selectById(lineItem.getItemId());
                if (item != null) {
                    // 🌟 顺藤摸瓜查出 Product，为了前端能显示名字
                    Product product = productMapper.selectById(item.getProductId());
                    item.setProduct(product); // 把产品对象塞进 Item 里！

                    lineItem.setItem(item);
                    // 计算小计
                    if (lineItem.getUnitPrice() != null) {
                        lineItem.setTotal(lineItem.getUnitPrice().multiply(new BigDecimal(lineItem.getQuantity())));
                    }
                }
            }
            orderVO.setCurrentStatus(status.getStatus());
            // 5. 把组装好的明细塞回订单对象
            orderVO.setLineItems(lineItems);
        }
        return orderVO;
    }

    @Override
    public List<Orders> getAllOrders() {
        // 1. 查出所有订单，按日期倒序（最新的在最上面）
        List<Orders> orderList = ordersMapper.selectList(
                new LambdaQueryWrapper<Orders>().orderByDesc(Orders::getOrderDate)
        );

        // 2. 遍历订单，补全状态信息
        for (Orders order : orderList) {
            OrderStatus statusRecord = orderStatusMapper.selectOne(
                    new LambdaQueryWrapper<OrderStatus>().eq(OrderStatus::getOrderId, order.getOrderId())
            );
            if (statusRecord != null) {
                order.setCurrentStatus(statusRecord.getStatus());
            }
        }
        return orderList;
    }

    @Override
    @Transactional // 🌟 修改状态建议加事务
    public void updateOrderStatus(Integer orderId, String status) {
        // 构建更新条件
        LambdaUpdateWrapper<OrderStatus> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OrderStatus::getOrderId, orderId);

        // 设置更新内容：状态和当前时间
        OrderStatus updateRecord = new OrderStatus();
        updateRecord.setStatus(status);
        updateRecord.setTimestamp(new Date());

        orderStatusMapper.update(updateRecord, wrapper);
    }
}
