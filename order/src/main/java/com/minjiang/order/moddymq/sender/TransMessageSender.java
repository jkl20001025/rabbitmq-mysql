package com.minjiang.order.moddymq.sender;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.minjiang.order.moddymq.pojo.TransMessage;
import com.minjiang.order.moddymq.service.TransMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/* 消息发送调用方法 */
@Component
@Slf4j
public class TransMessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private TransMessageService transMessageService;

    public void send(String exchange, String Routingkey, Message payload){

        log.info("消息发送(): exchange:{} routingkey:{} payload:{}", exchange, Routingkey, payload);
        try {

            ObjectMapper mapper = new ObjectMapper();
            String payloadStr = mapper.writeValueAsString(payload);
            String messageId = payload.getMessageProperties().getMessageId();
            TransMessage transMessagePO = transMessageService.messageSendReady(messageId,exchange, Routingkey, payloadStr);     //消息入库
            rabbitTemplate.convertAndSend(exchange, Routingkey, payload, new CorrelationData(transMessagePO.getId()));            //发送消息到下一服务

            log.info("message sent, ID:{}", transMessagePO.getId());

        }catch (Exception e){
            log.error(e.getMessage(), e);
        }

    }

}