package com.xingmao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingmao.entity.CustomerAddress;
import com.xingmao.mapper.CustomerAddressMapper;
import com.xingmao.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<CustomerAddressMapper, CustomerAddress> implements AddressService {

    @Override
    public List<CustomerAddress> getListByUserId(Long userId) {
        LambdaQueryWrapper<CustomerAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerAddress::getUserId, userId).orderByDesc(CustomerAddress::getIsDefault)
                .orderByDesc(CustomerAddress::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public void addAddress(CustomerAddress address) {
        this.save(address);
    }

    @Override
    public void updateAddress(CustomerAddress address) {
        this.updateById(address);
    }

    @Override
    public void deleteAddress(Long id) {
        this.removeById(id);
    }

    @Override
    public void setDefaultAddress(Long id, Long userId) {
        // 先将该用户的所有地址设为非默认
        LambdaQueryWrapper<CustomerAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerAddress::getUserId, userId);
        List<CustomerAddress> addresses = this.list(wrapper);
        for (CustomerAddress addr : addresses) {
            addr.setIsDefault(0);
            this.updateById(addr);
        }
        // 再将目标地址设为默认
        CustomerAddress target = this.getById(id);
        if (target != null && target.getUserId().equals(userId)) {
            target.setIsDefault(1);
            this.updateById(target);
        }
    }
}