package com.minjiang.reward.pojo;

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
 * @TableName reward
 */
@TableName(value ="reward")
@Data
public class Reward implements Serializable {
    /**
     * 奖励id
     */
    @TableId
    private String id;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 积分量
     */
    private BigDecimal amount;

    /**
     * 状态
     */
    private String status;

    /**
     * 时间
     */
    private Date date;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}