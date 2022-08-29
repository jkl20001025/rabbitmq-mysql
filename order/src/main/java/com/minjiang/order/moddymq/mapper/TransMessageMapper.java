package com.minjiang.order.moddymq.mapper;

import com.minjiang.order.moddymq.pojo.TransMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
* @author Administrator
* @description 针对表【trans_message】的数据库操作Mapper
* @createDate 2022-08-19 19:47:37
* @Entity com.minjiang.order.moddymq.pojo.TransMessage
*/
public interface TransMessageMapper extends BaseMapper<TransMessage> {
    @Insert("INSERT INTO trans_message (id, type, service, " +
            "exchange, routingKey, queue, sequence, payload," +
            "date) " +
            "VALUES(#{id}, #{type}, #{service},#{exchange}," +
            "#{Routingkey},#{queue},#{sequence}, #{payload},#{date})")
    int insert(TransMessage transMessagePO);


    @Update("UPDATE trans_message set type=#{type}, " +
            "service = #{service}, exchange = #{exchange},"+
            "routingKey = #{Routingkey}, queue = #{queue}, " +
            "sequence = #{sequence}, payload = #{payload}, " +
            "date = #{date} " +
            "where id = #{id} and service = #{service}")
    void update(TransMessage transMessagePO);


    @Select("SELECT id, type, service, exchange, " +
            "routingKey, queue, sequence, " +
            "payload, date " +
            "FROM trans_message " +
            "where id = #{id} and service = #{service}")
    TransMessage selectByIdAndService(@Param("id") String id, @Param("service") String service);


    @Select("SELECT id, type, service, exchange,routingKey, queue, sequence, " +
            "payload, date " +
            "FROM trans_message " +
            "WHERE type = #{type} and service = #{service}")
    List<TransMessage> selectByTypeAndService(@Param("type")String type, @Param("service")String service);


    @Delete("DELETE FROM trans_message " +
            "where id = #{id} and service = #{service}")
    void delete(@Param("id") String id, @Param("service") String service);
}




