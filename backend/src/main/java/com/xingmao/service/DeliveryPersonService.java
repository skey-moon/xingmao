package com.xingmao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xingmao.entity.DeliveryPerson;

import java.util.List;

public interface DeliveryPersonService extends IService<DeliveryPerson> {

    List<DeliveryPerson> getAvailablePersons();

    DeliveryPerson getLeastBusyPerson();

    void incrementOrderCount(Long personId);

    void decrementOrderCount(Long personId);
}