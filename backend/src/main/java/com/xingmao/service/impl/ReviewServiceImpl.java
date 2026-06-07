package com.xingmao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingmao.entity.OrderReview;
import com.xingmao.mapper.OrderReviewMapper;
import com.xingmao.service.ReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewServiceImpl extends ServiceImpl<OrderReviewMapper, OrderReview> implements ReviewService {

    @Override
    public OrderReview getByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderReview::getOrderId, orderId);
        return this.getOne(wrapper);
    }

    @Override
    public void createReview(OrderReview review) {
        this.save(review);
    }

    @Override
    public Page<OrderReview> getMyReviews(Long userId, int pageNum, int pageSize) {
        Page<OrderReview> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OrderReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderReview::getUserId, userId).orderByDesc(OrderReview::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public void replyReview(Long id, String reply) {
        OrderReview review = this.getById(id);
        if (review != null) {
            review.setReply(reply);
            review.setReplyTime(LocalDateTime.now());
            this.updateById(review);
        }
    }
}