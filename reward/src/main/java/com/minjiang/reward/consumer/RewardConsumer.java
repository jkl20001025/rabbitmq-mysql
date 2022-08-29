package com.minjiang.reward.consumer;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.minjiang.reward.config.RabbitConfig;
import com.minjiang.reward.mapper.RewardMapper;
import com.minjiang.reward.pojo.Reward;
import com.minjiang.reward.pojo.dto.OrderMessageDTO;
import com.minjiang.reward.status.OrderStatus;
import com.minjiang.reward.status.RewardStatus;
import com.rabbitmq.client.Channel;
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

@Component
public class RewardConsumer {
    @Autowired
    private RewardMapper rewardMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @RabbitListener(queues = RabbitConfig.REWARD_QUEUE,
                containerFactory = "rabbitListenerContainerFactory")
    public void reward(Message message, Channel channel) throws IOException {
        OrderMessageDTO messageDTO = JSON.parseObject(new String(message.getBody()), OrderMessageDTO.class);
        Reward reward = new Reward();
        String rewardId = IdUtil.randomUUID();
        reward.setId(rewardId);
        reward.setAmount(messageDTO.getPrice());
        reward.setDate(new Date(System.currentTimeMillis()));
        reward.setStatus(RewardStatus.SUCCESS.toString());
        reward.setOrderId(messageDTO.getOrderID());
        rewardMapper.insert(reward);

        messageDTO.setRewardId(rewardId);
        messageDTO.setOrderStatus(OrderStatus.ORDER_CREATED);

        String jsonString = JSON.toJSONString(messageDTO);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.setContentEncoding("utf-8");
        Message msg = new Message(jsonString.getBytes(StandardCharsets.UTF_8), messageProperties);
        ReturnedMessage returnedMessage = new ReturnedMessage(msg, 300, "奖励完成，正在返回消息给订单",
                RabbitConfig.REWARD_EXCHANGE, "key.order");
        CorrelationData correlationData = new CorrelationData();
        correlationData.setReturned(returnedMessage);
        correlationData.setId(messageDTO.getOrderID());
        rabbitTemplate.convertAndSend( RabbitConfig.REWARD_EXCHANGE, "key.order",msg,correlationData);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
