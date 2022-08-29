package com.minjiang.order.moddymq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minjiang.order.moddymq.pojo.TransMessage;
import com.minjiang.order.moddymq.service.TransMessageService;
import com.minjiang.order.moddymq.mapper.TransMessageMapper;
import com.minjiang.order.moddymq.status.TransMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【trans_message】的数据库操作Service实现
* @createDate 2022-08-19 19:47:37
*/
@Service
public class TransMessageServiceImpl extends ServiceImpl<TransMessageMapper, TransMessage>
    implements TransMessageService{

    @Autowired
    private TransMessageMapper transMessageMapper;

    private static final String serviceName = "orderService";


    @Override
    public TransMessage messageSendReady(String id,String exchange, String Routingkey, String body) {
        TransMessage transMessage = new TransMessage();
        transMessage.setId(id);
        transMessage.setService(serviceName);
        transMessage.setExchange(exchange);
        transMessage.setRoutingkey(Routingkey);
        transMessage.setPayload(body);
        transMessage.setDate(new Date(System.currentTimeMillis()));
        transMessage.setSequence(0);
        transMessage.setType(String.valueOf(TransMessageType.SEND));
        transMessageMapper.insert(transMessage);
        return transMessage;
    }

    @Override
    public void messageSendSuccess(String id) {
        transMessageMapper.delete(id, serviceName);
    }


    @Override
    public TransMessage messageSendReturn(String id, String exchange, String Routingkey, String body) {
        return messageSendReady(id,exchange, Routingkey, body);
    }


    @Override
    public List<TransMessage> listReadyMessages() {
        return transMessageMapper.selectByTypeAndService(TransMessageType.SEND.toString() ,serviceName);
    }


    /* 在修改不可路由消息状态时需要使用分布式锁进行保障，保证同一时间只有一个任务可以进行状态，这里进行省略  */
    @Override
    public void messageResend(String id) {
        TransMessage transMessage = transMessageMapper.selectByIdAndService(id, serviceName);
        transMessage.setSequence(transMessage.getSequence() + 1);
        transMessageMapper.updateById(transMessage);
    }


    /* 在修改不可路由消息状态时需要使用分布式锁进行保障，保证同一时间只有一个任务可以进行状态，这里进行省略 */
    @Override
    public void messageDead(String id) {
        TransMessage transMessage = transMessageMapper.selectByIdAndService(id, serviceName);
        transMessage.setType(String.valueOf(TransMessageType.DEAD));
        transMessageMapper.updateById(transMessage);
    }

}




