package com.minjiang.reward.mapper;

import com.minjiang.reward.pojo.Reward;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

/**
* @author Administrator
* @description 针对表【reward】的数据库操作Mapper
* @createDate 2022-08-19 14:11:43
* @Entity com.minjiang.reward.pojo.Reward
*/
public interface RewardMapper extends BaseMapper<Reward> {
    @Insert("INSERT INTO reward (id,order_id, amount, status, date) VALUES(#{id},#{orderId}, #{amount}, #{status}, #{date})")
    int insert(Reward reward);
}




