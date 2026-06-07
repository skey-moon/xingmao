package com.xingmao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingmao.entity.Delivery;
import com.xingmao.entity.DeliveryPerson;
import com.xingmao.entity.Orders;
import com.xingmao.entity.Transaction;
import com.xingmao.mapper.DeliveryMapper;
import com.xingmao.mapper.DeliveryPersonMapper;
import com.xingmao.mapper.OrdersMapper;
import com.xingmao.mapper.TransactionMapper;
import com.xingmao.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DeliveryServiceImpl extends ServiceImpl<DeliveryMapper, Delivery> implements DeliveryService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private DeliveryPersonMapper deliveryPersonMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Override
    public Page<Delivery> getPage(int pageNum, int pageSize, Integer status, Long deliveryPersonId) {
        Page<Delivery> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Delivery> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Delivery::getStatus, status);
        }
        if (deliveryPersonId != null) {
            wrapper.eq(Delivery::getDeliveryPersonId, deliveryPersonId);
        }
        wrapper.orderByDesc(Delivery::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional
    public void assignDelivery(Long orderId, String deliveryName, String deliveryPhone) {
        assignDelivery(orderId, deliveryName, deliveryPhone, null);
    }

    @Override
    @Transactional
    public void assignDelivery(Long orderId, String deliveryName, String deliveryPhone, Long deliveryPersonId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setDeliveryName(deliveryName);
        delivery.setDeliveryPhone(deliveryPhone);
        delivery.setDeliveryPersonId(deliveryPersonId);
        delivery.setStatus(0);
        this.save(delivery);

        // 更新配送员的订单数（原子递增）
        if (deliveryPersonId != null) {
            deliveryPersonMapper.incrementOrderCount(deliveryPersonId);
            // 使用FOR UPDATE锁住配送员行，再更新状态
            DeliveryPerson person = deliveryPersonMapper.selectById(deliveryPersonId);
            if (person != null) {
                person.setStatus(1); // 变为配送中
                deliveryPersonMapper.updateById(person);
            }
        }

        // 更新订单状态为配送中
        order.setStatus(1);
        ordersMapper.updateById(order);
    }

    @Override
    @Transactional
    public void autoAssignDelivery(Long orderId) {
        // 查找空闲的配送员（FOR UPDATE锁住，防止重复分配）
        DeliveryPerson person = deliveryPersonMapper.selectAvailablePersonWithLock();

        if (person != null) {
            assignDelivery(orderId, person.getName(), person.getPhone(), person.getId());
        } else {
            // 没有空闲配送员，分配给订单最少的（也加锁）
            person = deliveryPersonMapper.selectLeastBusyWithLock();
            if (person != null) {
                assignDelivery(orderId, person.getName(), person.getPhone(), person.getId());
            } else {
                throw new RuntimeException("没有可用的配送员");
            }
        }
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Delivery delivery = this.getById(id);
        if (delivery == null) {
            throw new RuntimeException("配送记录不存在");
        }

        Integer oldStatus = delivery.getStatus();
        delivery.setStatus(status);

        if (status == 1) {
            delivery.setTakeTime(LocalDateTime.now());
        } else if (status == 2) {
            delivery.setDeliveryTime(LocalDateTime.now());

            // 更新订单状态为已送达待确认(4)，等待客户确认
            Orders order = ordersMapper.selectById(delivery.getOrderId());
            if (order != null) {
                order.setStatus(4);
                ordersMapper.updateById(order);

                // 创建收入流水
                Transaction trans = new Transaction();
                trans.setOrderId(order.getId());
                trans.setOrderNo(order.getOrderNo());
                trans.setAmount(order.getTotalPrice());
                trans.setType("income");
                trans.setDescription("订单完成收入");
                trans.setCreateTime(LocalDateTime.now());
                transactionMapper.insert(trans);
            }

            // 减少配送员的订单数（原子递减）
            if (delivery.getDeliveryPersonId() != null) {
                deliveryPersonMapper.decrementOrderCount(delivery.getDeliveryPersonId());
                // 再查询更新状态
                DeliveryPerson person = deliveryPersonMapper.selectById(delivery.getDeliveryPersonId());
                if (person != null && person.getOrderCount() == 0) {
                    person.setStatus(0); // 变为空闲
                    deliveryPersonMapper.updateById(person);
                }
            }
        }

        this.updateById(delivery);
    }
}