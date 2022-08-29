package com.minjiang.settlement.config;

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

    public static final String SETTLEMENT_EXCHANGE = "exchange.order.settlement";
    public static final String SETTLEMENT_QUEUE="queue.settlement";
    public static final String SETTLEMENT_ROUTING_KEY="key.settlement";
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

    @Bean
    public TopicExchange settlementExchange(){
        return ExchangeBuilder.topicExchange(SETTLEMENT_EXCHANGE).durable(true).build();
    }
    @Bean
    public Queue settlementQueue(){
        return QueueBuilder.durable(SETTLEMENT_QUEUE).build();
    }
    @Bean
    public Binding settlementBinding(@Qualifier("settlementExchange")TopicExchange exchange,
                                     @Qualifier("settlementQueue")Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(SETTLEMENT_ROUTING_KEY);
    }
}
