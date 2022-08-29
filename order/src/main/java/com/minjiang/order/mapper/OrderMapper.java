package com.minjiang.order.mapper;

import com.minjiang.order.pojo.po.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minjiang.order.pojo.po.OrderDetailPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* @author Administrator
* @description 针对表【order_detail】的数据库操作Mapper
* @createDate 2022-08-18 16:46:26
* @Entity com.minjiang.order.pojo.po.Order
*/
public interface OrderMapper extends BaseMapper<Order> {
    @Insert("INSERT INTO order_detail (id,status,address," +
            "account_id,product_id,deliveryman_id,settlement_id,reward_id,price,date)" +
            "VALUES(#{id},#{status},#{address}," +
            "#{accountId},#{productId},#{deliverymanId},#{settlementId},#{rewardId},#{price},#{date})")
    void save(Order order);

    @Update("UPDATE order_detail SET status=#{status},address=#{address}," +
            "account_id=#{accountId},product_id=#{productId},deliveryman_id=#{deliverymanId}," +
            "settlement_id=#{settlementId},reward_id=#{rewardId},price=#{price},date=#{date}" +
            "WHERE id=#{id}")
    void update(Order order);

    @Select("SELECT id,status,address," +
            "account_id AS accountId,product_id AS productId," +
            "deliveryman_id AS deliverymanId,settlement_id AS settlementId," +
            "reward_id AS rewardId,price,date " +
            "FROM order_detail " +
            "WHERE id=#{id}")
    Order selectOrder(String id);
}




