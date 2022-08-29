package com.minjiang.order;

import com.minjiang.order.mapper.OrderMapper;
import com.minjiang.order.pojo.po.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderApplicationTests {

    @Autowired
    private OrderMapper orderMapper;
    @Test
    void contextLoads() {
        Order order = orderMapper.selectOrder("0ad8b41c-e54c-4f9a-a6c6-cf327d481833");
        System.out.println(order);
    }

}
