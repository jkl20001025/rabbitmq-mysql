package com.minjiang.restaurant;

import com.minjiang.restaurant.mapper.ProductMapper;
import com.minjiang.restaurant.pojo.Product;
import com.minjiang.restaurant.status.RestaurantStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RestaurantApplicationTests {

    @Autowired
    private ProductMapper productMapper;
    @Test
    void contextLoads() {
        Product product = productMapper.selectById(1);
        System.out.println(product);
    }

}
