package com.xingmao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xingmao.entity.Transaction;

import java.util.Map;

public interface TransactionService extends IService<Transaction> {

    Page<Transaction> getPage(int pageNum, int pageSize, String type, String startDate, String endDate);

    Map<String, Object> getSummary();
}