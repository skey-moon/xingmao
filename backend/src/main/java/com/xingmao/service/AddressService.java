package com.xingmao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xingmao.entity.CustomerAddress;

import java.util.List;

public interface AddressService extends IService<CustomerAddress> {

    List<CustomerAddress> getListByUserId(Long userId);

    void addAddress(CustomerAddress address);

    void updateAddress(CustomerAddress address);

    void deleteAddress(Long id);

    void setDefaultAddress(Long id, Long userId);
}