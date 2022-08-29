package com.minjiang.restaurant.mapper;

import com.minjiang.restaurant.pojo.Restaurant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author Administrator
* @description 针对表【restaurant】的数据库操作Mapper
* @createDate 2022-08-19 11:44:09
* @Entity com.minjiang.restaurant.pojo.Restaurant
*/
public interface RestaurantMapper extends BaseMapper<Restaurant> {
    @Select("SELECT id,name,address,status,settlement_id AS settlementId,date FROM restaurant WHERE id = #{id}")
    Restaurant selectRestaurant(String id);
}




