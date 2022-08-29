package com.minjiang.order.moddymq.task;

import com.minjiang.order.moddymq.pojo.TransMessage;
import com.minjiang.order.moddymq.service.TransMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/* 定时处理发送失败消息 */
//@EnableScheduling
@Configuration
@Component
@Slf4j
public class ResendTask {

    @Autowired
    private TransMessageService transMessageService;
    @Autowired
    private RabbitTemplate rabbitTemplate;


//    @Scheduled(fixedDelayString = "5000")
    public void resendMessage(){

        log.info("定时消息服务启动");
        List<TransMessage> messagePOS = transMessageService.listReadyMessages();
        log.info("resendMessage(): messagepos:{}", messagePOS);

        for (TransMessage po: messagePOS) {
            log.info("resendMessage(): po:{}", po);
            if(po.getSequence() > 5){
                log.error("resend too many times!");
                transMessageService.messageDead(po.getId());
                continue;
            }
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            Message message = new Message(po.getPayload().getBytes(), messageProperties);
            message.getMessageProperties().setMessageId(po.getId());
            rabbitTemplate.convertAndSend(
                    po.getExchange(),
                    po.getRoutingkey(),
                    message,
                    new CorrelationData(po.getId()));

            log.info("message sent, ID:{}", po.getId());
            transMessageService.messageResend(po.getId());
        }

    }


}