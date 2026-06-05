package com.xingmao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingmao.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {
}