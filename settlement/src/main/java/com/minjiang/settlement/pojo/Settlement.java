package com.minjiang.settlement.pojo;

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
 * @TableName settlement
 */
@TableName(value ="settlement")
@Data
public class Settlement implements Serializable {
    /**
     * 结算id
     */
    @TableId
    private String id;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 交易id
     */
    private String transactionId;

    /**
     * 金额
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