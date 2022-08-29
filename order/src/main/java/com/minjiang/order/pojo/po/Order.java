package com.minjiang.order.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName order_detail
 */
@TableName(value ="order_detail")
@Data
public class Order implements Serializable {
    /**
     * 订单id
     */
    @TableId
    private String id;

    /**
     * 状态
     */
    private String status;

    /**
     * 订单地址
     */
    private String address;

    /**
     * 用户id
     */
    private String accountId;

    /**
     * 产品id
     */
    private String productId;

    /**
     * 骑手id
     */
    private String deliverymanId;

    /**
     * 结算id
     */
    private String settlementId;

    /**
     * 积分奖励id
     */
    private String rewardId;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 时间
     */
    private Date date;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}