package com.xingmao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingmao.entity.Orders;
import com.xingmao.mapper.OrdersMapper;
import com.xingmao.service.CustomerService;
import com.xingmao.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    public Map<String, Object> getCustomerStats(Long userId) {
        LambdaQueryWrapper<Orders> baseWrapper = new LambdaQueryWrapper<>();
        baseWrapper.eq(Orders::getUserId, userId);

        long totalOrders = ordersMapper.selectCount(baseWrapper);

        LambdaQueryWrapper<Orders> pendingWrapper = baseWrapper.clone();
        pendingWrapper.eq(Orders::getStatus, 0);
        long pendingOrders = ordersMapper.selectCount(pendingWrapper);

        LambdaQueryWrapper<Orders> deliveryWrapper = baseWrapper.clone();
        deliveryWrapper.eq(Orders::getStatus, 1);
        long deliveryOrders = ordersMapper.selectCount(deliveryWrapper);

        LambdaQueryWrapper<Orders> completedWrapper = baseWrapper.clone();
        completedWrapper.eq(Orders::getStatus, 2);
        long completedOrders = ordersMapper.selectCount(completedWrapper);

        // 计算总消费（只统计已完成订单）
        LambdaQueryWrapper<Orders> spentWrapper = baseWrapper.clone();
        spentWrapper.eq(Orders::getStatus, 2);
        List<Orders> completedOrdersList = ordersMapper.selectList(spentWrapper);
        BigDecimal totalSpent = BigDecimal.ZERO;
        for (Orders order : completedOrdersList) {
            totalSpent = totalSpent.add(order.getTotalPrice());
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("deliveryOrders", deliveryOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("totalSpent", totalSpent.setScale(2, java.math.RoundingMode.HALF_UP));
        return stats;
    }

    @Override
    public Page<Orders> getRecentOrders(Long userId, int pageNum, int pageSize) {
        return orderService.getPageByUserId(pageNum, pageSize, userId, null);
    }
}