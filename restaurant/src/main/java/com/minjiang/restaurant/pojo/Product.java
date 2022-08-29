package com.minjiang.restaurant.pojo;

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
 * @TableName product
 */
@TableName(value ="product")
@Data
public class Product implements Serializable {
    /**
     * 产品id
     */
    @TableId
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 地址
     */
    private String restaurantId;

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