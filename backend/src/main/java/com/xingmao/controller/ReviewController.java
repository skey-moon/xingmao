package com.xingmao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingmao.entity.OrderReview;
import com.xingmao.entity.Orders;
import com.xingmao.service.OrderService;
import com.xingmao.service.ReviewService;
import com.xingmao.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/order/{orderId}")
    public Result<?> getReviewByOrderId(@PathVariable Long orderId) {
        OrderReview review = reviewService.getByOrderId(orderId);
        return Result.success(review);
    }

    @PostMapping
    public Result<?> createReview(@RequestBody OrderReview review, Authentication auth) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        Long userId = (Long) auth.getCredentials();

        // 校验订单属于当前用户
        Orders order = orderService.getById(review.getOrderId());
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error(403, "无权评价此订单");
        }
        // 校验订单已完成
        if (order.getStatus() != 2) {
            return Result.error("只能评价已完成的订单");
        }
        //校验是否已评价
        OrderReview existing = reviewService.getByOrderId(review.getOrderId());
        if (existing != null) {
            return Result.error("该订单已评价");
        }

        review.setUserId(userId);
        reviewService.createReview(review);
        return Result.success("评价成功");
    }

    @GetMapping("/my")
    public Result<?> getMyReviews(Authentication auth,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        Long userId = (Long) auth.getCredentials();
        Page<OrderReview> page = reviewService.getMyReviews(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @PutMapping("/{id}/reply")
    public Result<?> replyReview(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication auth) {
        if (auth == null) {
            return Result.unauthorized("请先登录");
        }
        String reply = body.get("reply");
        if (reply == null || reply.trim().isEmpty()) {
            return Result.error("回复内容不能为空");
        }
        reviewService.replyReview(id, reply);
        return Result.success("回复成功");
    }
}