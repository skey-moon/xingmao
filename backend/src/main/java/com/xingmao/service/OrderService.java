package com.xingmao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xingmao.entity.Orders;

import java.util.Map;

public interface OrderService extends IService<Orders> {

    Page<Orders> getPage(int pageNum, int pageSize, Integer status, String orderNo, String startDate, String endDate);

    Page<Orders> getPageByUserId(int pageNum, int pageSize, Long userId, Integer status);

    Map<String, Object> createOrder(Long userId, String address, String remark, java.util.List<Map<String, Object>> items);

    void updateOrderStatus(Long orderId, Integer status);

    void dispatchOrder(Long orderId);

    Map<String, Object> getOrderDetail(Long orderId);

    void confirmReceipt(Long orderId);
}