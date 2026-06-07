package com.xingmao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xingmao.entity.OrderReview;

public interface ReviewService extends IService<OrderReview> {

    OrderReview getByOrderId(Long orderId);

    void createReview(OrderReview review);

    Page<OrderReview> getMyReviews(Long userId, int pageNum, int pageSize);

    void replyReview(Long id, String reply);
}