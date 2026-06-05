package com.xingmao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingmao.entity.DeliveryPerson;
import com.xingmao.mapper.DeliveryPersonMapper;
import com.xingmao.service.DeliveryPersonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryPersonServiceImpl extends ServiceImpl<DeliveryPersonMapper, DeliveryPerson> implements DeliveryPersonService {

    @Override
    public List<DeliveryPerson> getAvailablePersons() {
        return this.list(new LambdaQueryWrapper<DeliveryPerson>()
                .eq(DeliveryPerson::getStatus, 0)
                .orderByAsc(DeliveryPerson::getOrderCount));
    }

    @Override
    public DeliveryPerson getLeastBusyPerson() {
        return this.getOne(new LambdaQueryWrapper<DeliveryPerson>()
                .eq(DeliveryPerson::getStatus, 0)
                .orderByAsc(DeliveryPerson::getOrderCount)
                .last("LIMIT 1"));
    }

    @Override
    public void incrementOrderCount(Long personId) {
        DeliveryPerson person = this.getById(personId);
        if (person != null) {
            person.setOrderCount(person.getOrderCount() + 1);
            if (person.getOrderCount() > 0) {
                person.setStatus(1); // 变为配送中
            }
            this.updateById(person);
        }
    }

    @Override
    public void decrementOrderCount(Long personId) {
        DeliveryPerson person = this.getById(personId);
        if (person != null) {
            int newCount = Math.max(0, person.getOrderCount() - 1);
            person.setOrderCount(newCount);
            if (newCount == 0) {
                person.setStatus(0); // 变为空闲
            }
            this.updateById(person);
        }
    }
}