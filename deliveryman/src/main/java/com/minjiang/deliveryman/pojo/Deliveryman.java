package com.minjiang.deliveryman.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName deliveryman
 */
@TableName(value ="deliveryman")
@Data
public class Deliveryman implements Serializable {
    /**
     * 骑手id
     */
    @TableId
    private String id;

    /**
     * 名称
     */
    private String name;

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