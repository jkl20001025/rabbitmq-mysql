package com.minjiang.restaurant.mapper;

import com.minjiang.restaurant.pojo.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author Administrator
* @description 针对表【product】的数据库操作Mapper
* @createDate 2022-08-20 11:13:18
* @Entity com.minjiang.restaurant.pojo.Product
*/
public interface ProductMapper extends BaseMapper<Product> {
    @Select("SELECT id,name,price,restaurant_id AS restaurantId,status,date FROM product WHERE id = #{id} ")
    Product selectProduct(String id);
}




