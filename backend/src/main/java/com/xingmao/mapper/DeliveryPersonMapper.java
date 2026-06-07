package com.xingmao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingmao.entity.DeliveryPerson;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeliveryPersonMapper extends BaseMapper<DeliveryPerson> {
    int incrementOrderCount(Long id);
    int decrementOrderCount(Long id);
    DeliveryPerson selectAvailablePersonWithLock();
    DeliveryPerson selectLeastBusyWithLock();
}