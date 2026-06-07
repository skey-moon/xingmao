package com.xingmao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingmao.entity.Delivery;
import com.xingmao.entity.Food;
import com.xingmao.entity.OrderItem;
import com.xingmao.entity.Orders;
import com.xingmao.mapper.DeliveryMapper;
import com.xingmao.mapper.FoodMapper;
import com.xingmao.mapper.OrderItemMapper;
import com.xingmao.mapper.OrdersMapper;
import com.xingmao.service.DeliveryService;
import com.xingmao.service.OrderService;
import com.xingmao.utils.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Override
    public Page<Orders> getPage(int pageNum, int pageSize, Integer status, String orderNo, String startDate, String endDate) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(Orders::getOrderNo, orderNo);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(Orders::getCreateTime, LocalDateTime.parse(startDate + " 00:00:00",
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(Orders::getCreateTime, LocalDateTime.parse(endDate + " 23:59:59",
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        wrapper.orderByDesc(Orders::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Page<Orders> getPageByUserId(int pageNum, int pageSize, Long userId, Integer status) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId);
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        wrapper.orderByDesc(Orders::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional
    public Map<String, Object> createOrder(Long userId, String address, String remark, List<Map<String, Object>> items) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("订单商品不能为空");
        }

        Orders order = new Orders();
        order.setOrderNo(CodeGenerator.generateOrderNo());
        order.setUserId(userId);
        order.setAddress(address);
        order.setRemark(remark);
        order.setStatus(0);

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map<String, Object> item : items) {
            if (!item.containsKey("foodId") || !item.containsKey("quantity")) {
                throw new RuntimeException("订单商品数据不完整");
            }
            Long foodId = Long.valueOf(item.get("foodId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());

            Food food = foodMapper.selectById(foodId);
            if (food == null) {
                throw new RuntimeException("食材不存在: " + foodId);
            }

            // 原子扣减库存，WHERE条件保证并发安全
            int updated = foodMapper.deductStock(foodId, quantity);
            if (updated == 0) {
                throw new RuntimeException("食材库存不足: " + food.getName());
            }

            BigDecimal subtotal = food.getPrice().multiply(new BigDecimal(quantity));
            totalPrice = totalPrice.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setFoodId(foodId);
            orderItem.setFoodName(food.getName());
            orderItem.setPrice(food.getPrice());
            orderItem.setQuantity(quantity);
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);
        }

        order.setTotalPrice(totalPrice);
        this.save(order);

        // 保存订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("totalPrice", totalPrice);
        return result;
    }

    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        order.setStatus(status);
        this.updateById(order);
    }

    @Override
    public void dispatchOrder(Long orderId) {
        deliveryService.autoAssignDelivery(orderId);
    }

    @Override
    public Map<String, Object> getOrderDetail(Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));

        // 查询配送信息
        Delivery delivery = deliveryMapper.selectOne(
                new LambdaQueryWrapper<Delivery>().eq(Delivery::getOrderId, orderId));

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        result.put("delivery", delivery);
        return result;
    }

    @Override
    @Transactional
    public void confirmReceipt(Long orderId) {
        Orders order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 4) {
            throw new RuntimeException("订单状态不允许确认收货");
        }
        // 更新订单状态为已完成
        order.setStatus(2);
        this.updateById(order);
    }
}