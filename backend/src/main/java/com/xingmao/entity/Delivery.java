package com.xingmao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("delivery")
public class Delivery implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long deliveryPersonId;

    private String deliveryName;

    private String deliveryPhone;

    /** 状态: 0-待取货, 1-配送中, 2-已送达 */
    private Integer status;

    private LocalDateTime takeTime;

    private LocalDateTime deliveryTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}