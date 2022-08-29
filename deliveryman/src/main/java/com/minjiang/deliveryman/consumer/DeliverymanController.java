package com.minjiang.deliveryman.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minjiang.deliveryman.config.RabbitConfig;
import com.minjiang.deliveryman.mapper.DeliverymanMapper;
import com.minjiang.deliveryman.pojo.Deliveryman;
import com.minjiang.deliveryman.pojo.dto.OrderMessageDTO;
import com.minjiang.deliveryman.status.DeliverymanStatus;
import com.minjiang.deliveryman.status.OrderStatus;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class DeliverymanController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private DeliverymanMapper deliverymanMapper;
    @RabbitListener(queues = RabbitConfig.DELIVERYMAN_QUEUE,
                    containerFactory = "rabbitListenerContainerFactory")
    public void deliveryman(Message message, Channel channel) throws IOException {
        OrderMessageDTO messageDTO = JSON.parseObject(new String(message.getBody()), OrderMessageDTO.class);

        List<Deliveryman> deliverymans = deliverymanMapper.selectAvaliableDeliveryman(DeliverymanStatus.AVALIABLE);
        if (deliverymans.size()>0){
            Deliveryman deliveryman = deliverymans.get(0);
            messageDTO.setDeliverymanId(deliveryman.getId());
            messageDTO.setOrderStatus(OrderStatus.DELIVERTMAN_CONFIREED);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.setContentEncoding("UTF-8");
        String jsonString = JSON.toJSONString(messageDTO);
        Message msg = new Message(jsonString.getBytes(StandardCharsets.UTF_8),messageProperties);
        ReturnedMessage returnedMessage = new ReturnedMessage(msg, 300, "骑手服务正在发送信息给订单",
                RabbitConfig.DELIVERYMAN_EXCHANGE, "key.order");
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(messageDTO.getOrderID());
        correlationData.setReturned(returnedMessage);
        rabbitTemplate.convertAndSend(RabbitConfig.DELIVERYMAN_EXCHANGE, "key.order",msg,correlationData);
            log.info("已配送成功");
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);        //手动消息应答
        }else{
//            messageDTO.setOrderStatus(OrderStatus.FALIED);
            log.info("当前无空闲骑手，请耐心等待");
        }

    }
}
