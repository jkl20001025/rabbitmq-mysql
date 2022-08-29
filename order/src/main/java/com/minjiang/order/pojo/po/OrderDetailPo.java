package com.minjiang.order.pojo.po;

import com.minjiang.order.pojo.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/* 订单落库实体类 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class OrderDetailPo {

    private String id;                //ID
    private OrderStatus status;        //订单状态
    private String address;            //地址
    private String accountId;         //用户ID
    private String productId;         //产品ID
    private String deliverymanId;     //骑手ID
    private String settlementId;      //结算ID
    private String rewardId;          //积分奖励ID
    private BigDecimal price;          //价格
    private LocalDateTime date;        //时间

}