package com.minjiang.order.moddymq.service;

import com.minjiang.order.moddymq.pojo.TransMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【trans_message】的数据库操作Service
* @createDate 2022-08-19 19:47:37
*/
public interface TransMessageService extends IService<TransMessage> {

    /* 发送前暂存消息 */
    TransMessage messageSendReady(String id,String exchange, String Routingkey, String body);


    /**
     * 设置消息发送成功
     *
     * @param id 消息ID
     */
    void messageSendSuccess(String id);


    /* 设置消息返回 */
    TransMessage messageSendReturn(String id, String exchange, String Routingkey, String body);


    /* 查询应发未发消息 */
    List<TransMessage> listReadyMessages();


    /**
     * 记录消息发送次数
     * @param id id
     */
    void messageResend(String id);


    /**
     * 消息重发多次，放弃
     * @param id id
     */
    void messageDead(String id);
}
