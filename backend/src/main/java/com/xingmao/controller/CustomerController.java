package com.xingmao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingmao.entity.Orders;
import com.xingmao.service.CustomerService;
import com.xingmao.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/stats")
    public Result<?> getCustomerStats(Authentication auth) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        Long userId = (Long) auth.getCredentials();
        Map<String, Object> stats = customerService.getCustomerStats(userId);
        return Result.success(stats);
    }

    @GetMapping("/recent-orders")
    public Result<?> getRecentOrders(Authentication auth,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        Long userId = (Long) auth.getCredentials();
        Page<Orders> page = customerService.getRecentOrders(userId, pageNum, pageSize);
        return Result.success(page);
    }
}