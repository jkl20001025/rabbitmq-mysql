package com.minjiang.reward.status;

/* 订单状态枚举类 */
public enum OrderStatus {

    ORDER_CREATEING,        //订单创建中
    RESTAURANT_CONFIRMED,   //餐厅已确认
    DELIVERTMAN_CONFIREED,  //骑手送达确认
    SETTLEMENT_CONFIREED,   //已结算
    ORDER_CREATED,          //订单已完成
    FALIED;                 //订单结束

}
