package com.minjiang.deliveryman;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.minjiang.deliveryman.mapper")
public class DeliverymanApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliverymanApplication.class, args);
    }

}
