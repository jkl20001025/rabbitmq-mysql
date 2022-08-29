package com.minjiang.settlement.mapper;

import com.minjiang.settlement.pojo.Settlement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

/**
* @author Administrator
* @description 针对表【settlement】的数据库操作Mapper
* @createDate 2022-08-19 14:09:37
* @Entity com.minjiang.settlement.pojo.Settlement
*/
public interface SettlementMapper extends BaseMapper<Settlement> {
    @Insert("INSERT INTO settlement (id,order_id, transaction_id, amount, status, date) " +
            "VALUES(#{id},#{orderId}, #{transactionId}, #{amount}, #{status}, #{date})")
    int insert(Settlement settlement);
}




