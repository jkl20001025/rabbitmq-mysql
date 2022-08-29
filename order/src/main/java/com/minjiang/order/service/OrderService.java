package com.minjiang.order.service;

import com.minjiang.order.pojo.po.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.minjiang.order.pojo.vo.OrderCreateVo;

/**
* @author Administrator
* @description 针对表【order_detail】的数据库操作Service
* @createDate 2022-08-18 16:46:26
*/
public interface OrderService extends IService<Order> {

    void createOrder(OrderCreateVo orderCreateVo);
}
