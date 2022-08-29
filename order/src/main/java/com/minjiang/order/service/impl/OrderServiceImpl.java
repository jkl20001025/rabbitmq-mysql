package com.minjiang.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minjiang.order.config.RabbitConfig;
import com.minjiang.order.moddymq.sender.TransMessageSender;
import com.minjiang.order.pojo.OrderStatus;
import com.minjiang.order.pojo.dto.OrderMessageDTO;
import com.minjiang.order.pojo.po.Order;
import com.minjiang.order.pojo.po.OrderDetailPo;
import com.minjiang.order.pojo.vo.OrderCreateVo;
import com.minjiang.order.service.OrderService;
import com.minjiang.order.mapper.OrderMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

/**
* @author Administrator
* @description 针对表【order_detail】的数据库操作Service实现
* @createDate 2022-08-18 16:46:26
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private TransMessageSender messageSender;
    @Override
    public void createOrder(OrderCreateVo orderCreateVo) {
        /* 订单数据入库 */
        Order order = new Order();
        order.setAddress(orderCreateVo.getAddress());
        order.setAccountId(orderCreateVo.getAccountId());
        order.setProductId(orderCreateVo.getProductId());
        order.setStatus(String.valueOf(OrderStatus.ORDER_CREATEING));
        order.setDate(new Date(System.currentTimeMillis()));
        order.setId(UUID.randomUUID().toString());
        orderMapper.save(order);

        /* 封装订单消息，发送给餐厅交换机 */
        OrderMessageDTO messageDTO = new OrderMessageDTO();
        messageDTO.setOrderID(order.getId());
        messageDTO.setProductId(order.getProductId());
        messageDTO.setAccountId(order.getAccountId());

        String json = JSON.toJSONString(messageDTO);
        MessageProperties properties = new MessageProperties();
//        声明这是一个json类型的内容数据
        properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
//        指定为JSON格式消息
        Message message = new Message(json.getBytes(StandardCharsets.UTF_8),properties);
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(order.getId());        //设置为订单ID，保证全局唯一
        ReturnedMessage returnedMessage= new ReturnedMessage(message,100,"用户创建订单消息",
                RabbitConfig.RESTAURANT_EXCHANGE,RabbitConfig.ORDER_ROUTING_KEY);
        correlationData.setReturned(returnedMessage);       //设置不可路由捕获的消息

        rabbitTemplate.convertAndSend(RabbitConfig.RESTAURANT_EXCHANGE,
                "key.restaurant",
                message,
                correlationData);
//        messageSender.send(RabbitConfig.RESTAURANT_EXCHANGE, "key.restaurant",message);


    }

}




