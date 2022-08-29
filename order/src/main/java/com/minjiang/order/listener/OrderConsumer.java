package com.minjiang.order.listener;

import com.alibaba.fastjson.JSON;
import com.minjiang.order.config.RabbitConfig;
import com.minjiang.order.mapper.OrderMapper;
import com.minjiang.order.pojo.OrderStatus;
import com.minjiang.order.pojo.dto.OrderMessageDTO;
import com.minjiang.order.pojo.po.Order;
import com.minjiang.order.pojo.po.OrderDetailPo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
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

@Component
@Slf4j
public class OrderConsumer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OrderMapper orderMapper;

    @RabbitListener(queues = RabbitConfig.ORDER_QUEUE,
            containerFactory = "rabbitListenerContainerFactory")
    public void order(Message message, Channel channel) throws IOException {
        OrderMessageDTO messageDTO = JSON.parseObject(new String(message.getBody()),OrderMessageDTO.class);
        //获取订单入库数据
        Order order = orderMapper.selectOrder(messageDTO.getOrderID());

        Message deliveryMsg = null;
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        CorrelationData correlationData = new CorrelationData();

        switch (messageDTO.getOrderStatus()){
            /*餐厅已确认*/
            case RESTAURANT_CONFIRMED:
                /*判断是否创建订单且有价格，没有认为订单创建失败*/
                if (messageDTO.getConfirmed() && null != messageDTO.getPrice()){

                    /* 更新数据库的订单 */
                    order.setStatus(String.valueOf(OrderStatus.RESTAURANT_CONFIRMED));
                    order.setPrice(messageDTO.getPrice());
                    orderMapper.update(order);

                    /* 给骑手微服务发送消息 */
                    String json = JSON.toJSONString(messageDTO);
                    deliveryMsg = new Message(json.getBytes(StandardCharsets.UTF_8),messageProperties);
                    ReturnedMessage returnedMessage = new ReturnedMessage(deliveryMsg,200,"餐厅已确认订单,消息发送给骑手服务",
                            RabbitConfig.DELIVERYMAN_EXCHANGE,"key.deliveryman");
                    correlationData.setReturned(returnedMessage);
                    correlationData.setId(messageDTO.getOrderID());     //设置订单ID
                    rabbitTemplate.convertAndSend(RabbitConfig.DELIVERYMAN_EXCHANGE,"key.deliveryman",
                            deliveryMsg,correlationData);
                    log.info("已更新数据库订单状态,给骑手服务发送消息!");

                }else{
                    order.setStatus(String.valueOf(OrderStatus.FALIED));
                    orderMapper.update(order);
                }
                break;
            /*骑手已确认*/
            case DELIVERTMAN_CONFIREED:
                if (StringUtils.isNotBlank(messageDTO.getDeliverymanId())){
                    /* 更新数据库的订单 */
                    order.setStatus(String.valueOf(OrderStatus.DELIVERTMAN_CONFIREED));
                    order.setDeliverymanId(messageDTO.getDeliverymanId());
                    orderMapper.update(order);
                    /* 给结算微服务发送消息 */
                    String json = JSON.toJSONString(messageDTO);
                    deliveryMsg = new Message(json.getBytes(StandardCharsets.UTF_8), messageProperties);
                    ReturnedMessage returnedMessage = new ReturnedMessage(deliveryMsg,200,
                            "骑手已完成配送",RabbitConfig.SETTLEMENT_EXCHANGE,"key.settlement");
                    correlationData.setReturned(returnedMessage);
                    correlationData.setId(order.getId());
                    rabbitTemplate.convertAndSend(RabbitConfig.SETTLEMENT_EXCHANGE,"key.settlement",
                            deliveryMsg,correlationData);
                    log.info("已更新数据库订单状态,给结算服务发送消息!");
                }else{
                    order.setStatus(String.valueOf(OrderStatus.FALIED));
                    orderMapper.update(order);
                }
                break;
            /*结算已确认*/
            case SETTLEMENT_CONFIREED:
                if (StringUtils.isNotBlank(messageDTO.getSettlementId())){
                    order.setStatus(String.valueOf(OrderStatus.SETTLEMENT_CONFIREED));
                    order.setSettlementId(messageDTO.getSettlementId());
                    orderMapper.update(order);

                    String json = JSON.toJSONString(messageDTO);
                    deliveryMsg = new Message(json.getBytes(StandardCharsets.UTF_8),messageProperties);
                    ReturnedMessage returnedMessage = new ReturnedMessage(deliveryMsg, 200, "结算以确认",
                            RabbitConfig.REWARD_EXCHANGE, "key.reward");
                    correlationData.setReturned(returnedMessage);
                    correlationData.setId(order.getId());
                    rabbitTemplate.convertAndSend( RabbitConfig.REWARD_EXCHANGE, "key.reward",deliveryMsg,correlationData);
                    log.info("已更新数据库订单状态,给奖励服务发送消息!");
                }else{
                    order.setStatus(String.valueOf(OrderStatus.FALIED));
                    orderMapper.update(order);
                }
                break;
//                奖励已完成，更新订单信息
            case ORDER_CREATED:
                if (StringUtils.isNotBlank(messageDTO.getRewardId())){
                    order.setStatus(String.valueOf(OrderStatus.ORDER_CREATED));
                    order.setRewardId(messageDTO.getRewardId());
                    orderMapper.update(order);
                }else{
                    order.setStatus(String.valueOf(OrderStatus.FALIED));
                    orderMapper.update(order);
                }
                break;
            default: break;
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);    //手动消息应答
    }
}
