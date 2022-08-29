package com.minjiang.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Autowired
    private RabbitProperties properties;

    public static final String RESTAURANT_EXCHANGE = "exchange.order.restaurant";           //餐厅交换机
    public static final String DELIVERYMAN_EXCHANGE = "exchange.order.deliveryman";         //骑手交换机
    public static final String SETTLEMENT_EXCHANGE = "exchange.order.settlement";           //结算交换机
    public static final String REWARD_EXCHANGE = "exchange.order.reward";                   //奖励交换机

    public static final String ORDER_QUEUE = "queue.order";             //订单服务队列

    public static final String ORDER_ROUTING_KEY = "key.order";         //订单服务路由key

    /*RabbitMQ连接池，从配置文件读取参数*/
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setHost(properties.getHost());
        cachingConnectionFactory.setPort(properties.getPort());
        cachingConnectionFactory.setUsername(properties.getUsername());
        cachingConnectionFactory.setPassword(properties.getPassword());
        cachingConnectionFactory.setVirtualHost(properties.getVirtualHost());
        cachingConnectionFactory.setPublisherReturns(properties.isPublisherReturns());                          //开启连接池的ReturnCallBack支持
        cachingConnectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);      //开启连接池的Publisher-confirm-type支持
        return cachingConnectionFactory;
    }


    /* RabbitListener使用连接池，使用连接池时 spring.rabbitmq.listener.simple 配置不生效 */
    @Bean
    public RabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);         //关闭自动ACK
        factory.setConnectionFactory(connectionFactory);            //使用连接池
        factory.setPrefetchCount(1);                                //设置QOS
        factory.setMessageConverter(new Jackson2JsonMessageConverter());        //设置消息转换器为JSON
        return factory;
    }
//   创建交换机
    @Bean
    public DirectExchange restaurantExchange(){
        return ExchangeBuilder.directExchange(RESTAURANT_EXCHANGE).durable(true).build();
    }
    @Bean
    public DirectExchange deliverymanExchange(){
        return ExchangeBuilder.directExchange(DELIVERYMAN_EXCHANGE).durable(true).build();
    }
    @Bean
    public TopicExchange rewardExchange(){
        return ExchangeBuilder.topicExchange(REWARD_EXCHANGE).durable(true).build();
    }
    @Bean
    public TopicExchange settlementExchange(){
        return ExchangeBuilder.topicExchange(SETTLEMENT_EXCHANGE).durable(true).build();
    }
//    创建队列
    @Bean
    public Queue orderQueue(){
        return QueueBuilder.durable(ORDER_QUEUE).build();
    }
    /* 绑定关系创建 */
    @Bean
    public Binding restaurantBind(@Qualifier("restaurantExchange") DirectExchange exchange,
                                  @Qualifier("orderQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_ROUTING_KEY);
    }

    @Bean
    public Binding deliverymanBind(@Qualifier("deliverymanExchange") DirectExchange exchange,
                                   @Qualifier("orderQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_ROUTING_KEY);
    }

    @Bean
    public Binding settlementBind(@Qualifier("settlementExchange") TopicExchange exchange,
                                  @Qualifier("orderQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_ROUTING_KEY);
    }

    @Bean
    public Binding rewardBind(@Qualifier("rewardExchange") TopicExchange exchange,
                              @Qualifier("orderQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_ROUTING_KEY);
    }
}
