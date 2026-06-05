package com.xingmao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xingmao.entity.Delivery;
import com.xingmao.entity.Orders;
import com.xingmao.mapper.DeliveryMapper;
import com.xingmao.mapper.FoodMapper;
import com.xingmao.mapper.OrdersMapper;
import com.xingmao.mapper.SysUserMapper;
import com.xingmao.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatServiceImpl implements StatService {

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 食材种类数
        long foodCount = foodMapper.selectCount(null);
        stats.put("foodCount", foodCount);

        // 订单总数
        long orderCount = ordersMapper.selectCount(null);
        stats.put("orderCount", orderCount);

        // 配送中数量
        long deliveryCount = deliveryMapper.selectCount(
                new LambdaQueryWrapper<Delivery>().eq(Delivery::getStatus, 1));
        stats.put("deliveryCount", deliveryCount);

        // 待处理+配送中订单数
        long todayOrderCount = ordersMapper.selectCount(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getStatus, 0)
                        .or()
                        .eq(Orders::getStatus, 1));
        stats.put("todayOrderCount", todayOrderCount);

        // 总用户数
        long userCount = sysUserMapper.selectCount(null);
        stats.put("userCount", userCount);

        return stats;
    }
}