package com.minjiang.restaurant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minjiang.restaurant.pojo.Product;
import com.minjiang.restaurant.service.ProductService;
import com.minjiang.restaurant.mapper.ProductMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【product】的数据库操作Service实现
* @createDate 2022-08-20 11:13:18
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{

}




