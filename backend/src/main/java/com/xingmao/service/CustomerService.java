package com.xingmao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingmao.entity.Orders;

import java.util.Map;

public interface CustomerService {

    Map<String, Object> getCustomerStats(Long userId);

    Page<Orders> getRecentOrders(Long userId, int pageNum, int pageSize);
}