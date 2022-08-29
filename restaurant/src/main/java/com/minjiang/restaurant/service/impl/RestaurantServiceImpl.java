package com.minjiang.restaurant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minjiang.restaurant.pojo.Restaurant;
import com.minjiang.restaurant.service.RestaurantService;
import com.minjiang.restaurant.mapper.RestaurantMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【restaurant】的数据库操作Service实现
* @createDate 2022-08-19 11:44:09
*/
@Service
public class RestaurantServiceImpl extends ServiceImpl<RestaurantMapper, Restaurant>
    implements RestaurantService{

}




