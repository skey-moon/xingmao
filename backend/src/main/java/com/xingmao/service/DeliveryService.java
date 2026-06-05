package com.xingmao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xingmao.entity.Delivery;

public interface DeliveryService extends IService<Delivery> {

    Page<Delivery> getPage(int pageNum, int pageSize, Integer status, Long deliveryPersonId);

    void assignDelivery(Long orderId, String deliveryName, String deliveryPhone);

    void assignDelivery(Long orderId, String deliveryName, String deliveryPhone, Long deliveryPersonId);

    void autoAssignDelivery(Long orderId);

    void updateStatus(Long id, Integer status);
}