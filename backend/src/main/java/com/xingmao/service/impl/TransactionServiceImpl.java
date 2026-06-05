package com.xingmao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingmao.entity.Orders;
import com.xingmao.entity.Transaction;
import com.xingmao.mapper.OrdersMapper;
import com.xingmao.mapper.TransactionMapper;
import com.xingmao.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements TransactionService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    public Page<Transaction> getPage(int pageNum, int pageSize, String type, String startDate, String endDate) {
        Page<Transaction> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Transaction::getType, type);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(Transaction::getCreateTime, java.time.LocalDate.parse(startDate).atStartOfDay());
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(Transaction::getCreateTime, java.time.LocalDate.parse(endDate).plusDays(1).atStartOfDay());
        }
        wrapper.orderByDesc(Transaction::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();

        // 总收入：已完成(status=2)订单的总金额
        BigDecimal totalIncome = ordersMapper.selectObjs(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getStatus, 2)
                        .select(Orders::getTotalPrice)
        ).stream()
                .filter(obj -> obj != null)
                .map(obj -> obj instanceof BigDecimal ? (BigDecimal) obj : new BigDecimal(obj.toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("totalIncome", totalIncome);

        // 总支出：假设支出基于已取消(status=3)订单的成本（这里简化为0，如果需要可配置）
        BigDecimal totalExpense = ordersMapper.selectObjs(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getStatus, 3)
                        .select(Orders::getTotalPrice)
        ).stream()
                .filter(obj -> obj != null)
                .map(obj -> obj instanceof BigDecimal ? (BigDecimal) obj : new BigDecimal(obj.toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("totalExpense", totalExpense);

        // 净利润
        summary.put("netProfit", totalIncome.subtract(totalExpense));

        // 今日收入：今天完成的订单
        LocalDate today = LocalDate.now();
        BigDecimal todayIncome = ordersMapper.selectObjs(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getStatus, 2)
                        .ge(Orders::getCreateTime, today.atStartOfDay())
                        .select(Orders::getTotalPrice)
        ).stream()
                .filter(obj -> obj != null)
                .map(obj -> obj instanceof BigDecimal ? (BigDecimal) obj : new BigDecimal(obj.toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("todayIncome", todayIncome);

        // 今日支出：今天取消的订单
        BigDecimal todayExpense = ordersMapper.selectObjs(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getStatus, 3)
                        .ge(Orders::getCreateTime, today.atStartOfDay())
                        .select(Orders::getTotalPrice)
        ).stream()
                .filter(obj -> obj != null)
                .map(obj -> obj instanceof BigDecimal ? (BigDecimal) obj : new BigDecimal(obj.toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("todayExpense", todayExpense);

        // 今日交易总额（收入+支出）
        summary.put("todayAmount", todayIncome.add(todayExpense));

        return summary;
    }
}