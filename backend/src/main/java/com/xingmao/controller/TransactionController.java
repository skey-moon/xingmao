package com.xingmao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingmao.entity.Transaction;
import com.xingmao.service.TransactionService;
import com.xingmao.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/list")
    public Result<?> getList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<Transaction> page = transactionService.getPage(pageNum, pageSize, type, startDate, endDate);
        return Result.success(page);
    }

    @GetMapping("/summary")
    public Result<?> getSummary() {
        Map<String, Object> summary = transactionService.getSummary();
        return Result.success(summary);
    }
}