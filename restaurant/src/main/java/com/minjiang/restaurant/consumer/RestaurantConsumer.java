package com.minjiang.restaurant.consumer;

import com.alibaba.fastjson.JSON;
import com.minjiang.restaurant.config.RabbitConfig;
import com.minjiang.restaurant.mapper.ProductMapper;
import com.minjiang.restaurant.mapper.RestaurantMapper;
import com.minjiang.restaurant.pojo.Product;
import com.minjiang.restaurant.pojo.Restaurant;
import com.minjiang.restaurant.pojo.dto.OrderMessageDTO;
import com.minjiang.restaurant.status.ProductStatus;
import com.minjiang.restaurant.status.RestaurantStatus;
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

import static com.minjiang.restaurant.status.OrderStatus.FALIED;
import static com.minjiang.restaurant.status.OrderStatus.RESTAURANT_CONFIRMED;

@Component
public class RestaurantConsumer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RestaurantMapper restaurantMapper;
    @Autowired
    private ProductMapper productMapper;
    @RabbitListener(queues = RabbitConfig.RESTAURANT_QUEUE,
                    containerFactory = "rabbitListenerContainerFactory")
    public void restaurant(Message message, Channel channel) throws IOException {
        OrderMessageDTO messageDTO = JSON.parseObject(new String(message.getBody()), OrderMessageDTO.class);
        Product product = productMapper.selectProduct(messageDTO.getProductId());                //获取数据库中的商品数据
        Restaurant restaurant = restaurantMapper.selectRestaurant(product.getRestaurantId());    //通过商品获取对应的餐厅数据
        messageDTO.setPrice(product.getPrice());
        messageDTO.setOrderStatus(RESTAURANT_CONFIRMED);
        /* 判断当前店家是否已经打烊且订单对应商品库存是否充足，更新订单状态 */
        if(restaurant.getStatus().equals(RestaurantStatus.OPEN.toString()) && product.getStatus().equals(ProductStatus.AVALIABLE.toString()) ){
            messageDTO.setConfirmed(true);
            messageDTO.setOrderStatus(RESTAURANT_CONFIRMED);     //餐厅已确认
            messageDTO.setPrice(product.getPrice());
        }else{
            messageDTO.setOrderStatus(FALIED);   //订单创建失败
            messageDTO.setConfirmed(false);
        }
//        消息转发
        String jsonString = JSON.toJSONString(messageDTO);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentEncoding("utf-8");
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        Message message1 = new Message(jsonString.getBytes(StandardCharsets.UTF_8), messageProperties);
        CorrelationData correlationData = new CorrelationData();
        ReturnedMessage returnedMessage = new ReturnedMessage(message1,300,"餐厅收到订单消息，发送给订单服务队列",
                RabbitConfig.RESTAURANT_EXCHANGE,
                "key.order");
        correlationData.setReturned(returnedMessage);
        correlationData.setId(messageDTO.getOrderID());     //设置为订单ID

        rabbitTemplate.convertAndSend(RabbitConfig.RESTAURANT_EXCHANGE,"key.order",message1,correlationData);


        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);        //手动消息应答
    }

}
