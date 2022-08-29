package com.minjiang.settlement.consumer;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.minjiang.settlement.config.RabbitConfig;
import com.minjiang.settlement.mapper.SettlementMapper;
import com.minjiang.settlement.pojo.Settlement;
import com.minjiang.settlement.pojo.dto.OrderMessageDTO;
import com.minjiang.settlement.status.SettlementStatus;

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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.minjiang.settlement.status.OrderStatus.SETTLEMENT_CONFIREED;

@Component
@Slf4j
public class SettlementConsumer {
    @Autowired
    private SettlementMapper settlementMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @RabbitListener(queues = RabbitConfig.SETTLEMENT_QUEUE,
                    containerFactory = "rabbitListenerContainerFactory")
    public void Settlement(Message message, Channel channel) throws IOException {
        OrderMessageDTO messageDTO = JSON.parseObject(new String(message.getBody()), OrderMessageDTO.class);
        Settlement settlement = new Settlement();
        String settleId = IdUtil.randomUUID();
        settlement.setOrderId(messageDTO.getOrderID());
        settlement.setAmount(messageDTO.getPrice());
        settlement.setStatus(SettlementStatus.SUCCESS.toString());
        settlement.setTransactionId(IdUtil.fastUUID());
        settlement.setId(settleId);
        settlement.setDate(new Date(System.currentTimeMillis()));
        settlementMapper.insert(settlement);
//        插入成功后，修改订单状态
        messageDTO.setOrderStatus(SETTLEMENT_CONFIREED);
        messageDTO.setSettlementId(settleId);
//        发送消息
        String jsonString = JSON.toJSONString(messageDTO);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentEncoding("utf-8");
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);

        Message msg = new Message(jsonString.getBytes(StandardCharsets.UTF_8), messageProperties);
        ReturnedMessage returnedMessage = new ReturnedMessage(msg, 300, "结算成功，正在返回消息给订单交换机",
                RabbitConfig.SETTLEMENT_EXCHANGE, "key.order");
        CorrelationData correlationData = new CorrelationData();
        correlationData.setReturned(returnedMessage);
        correlationData.setId(messageDTO.getOrderID());
        rabbitTemplate.convertAndSend(RabbitConfig.SETTLEMENT_EXCHANGE, "key.order",msg,correlationData);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);        //手动消息应答

    }
}
