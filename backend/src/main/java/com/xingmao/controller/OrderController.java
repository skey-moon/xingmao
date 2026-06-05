package com.xingmao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingmao.entity.Orders;
import com.xingmao.service.OrderService;
import com.xingmao.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public Result<?> getList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<Orders> page = orderService.getPage(pageNum, pageSize, status, orderNo, startDate, endDate);
        return Result.success(page);
    }

    @PostMapping
    public Result<?> createOrder(@RequestBody Map<String, Object> orderReq, Authentication auth) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        Long userId = (Long) auth.getCredentials();
        String address = (String) orderReq.get("address");
        String remark = (String) orderReq.get("remark");
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> items = (java.util.List<Map<String, Object>>) orderReq.get("items");

        Map<String, Object> result = orderService.createOrder(userId, address, remark, items);
        return Result.success("下单成功", result);
    }

    @GetMapping("/{id}")
    public Result<?> getDetail(@PathVariable Long id) {
        try {
            Map<String, Object> detail = orderService.getOrderDetail(id);
            return Result.success(detail);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取详情失败: " + e.getMessage());
        }
    }

    @PutMapping("/status")
    public Result<?> updateStatus(@RequestBody Map<String, Object> statusReq) {
        Long orderId = Long.valueOf(statusReq.get("orderId").toString());
        Integer status = Integer.valueOf(statusReq.get("status").toString());
        orderService.updateOrderStatus(orderId, status);
        return Result.success("状态更新成功");
    }

    @PostMapping("/dispatch/{orderId}")
    public Result<?> dispatchOrder(@PathVariable Long orderId) {
        orderService.dispatchOrder(orderId);
        return Result.success("订单已派送");
    }

    @PostMapping("/confirm/{orderId}")
    public Result<?> confirmReceipt(@PathVariable Long orderId, Authentication auth) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        orderService.confirmReceipt(orderId);
        return Result.success("确认收货成功");
    }

    @GetMapping("/my")
    public Result<?> getMyOrders(Authentication auth,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        Long userId = (Long) auth.getCredentials();
        Page<Orders> page = orderService.getPageByUserId(pageNum, pageSize, userId, status);
        return Result.success(page);
    }
}