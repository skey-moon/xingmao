package com.xingmao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("transaction")
public class Transaction implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String orderNo;

    private BigDecimal amount;

    /** 类型: income-收入, expense-支出 */
    private String type;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}