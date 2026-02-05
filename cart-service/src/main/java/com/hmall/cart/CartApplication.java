package com.hmall.cart;

import com.hmall.api.config.DefaultFeignConfig;
import com.hmall.common.config.MvcConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@MapperScan("com.hmall.cart.mapper")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.hmall.api.client",defaultConfiguration = DefaultFeignConfig.class)
@Import(MvcConfig.class) // 导入公共MVC配置
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }
}