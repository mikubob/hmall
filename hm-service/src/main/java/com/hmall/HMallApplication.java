package com.hmall;

import com.hmall.api.config.DefaultFeignConfig;
import com.hmall.common.config.MvcConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@MapperScan("com.hmall.mapper")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.hmall.api.client", defaultConfiguration = DefaultFeignConfig.class)
@Import(MvcConfig.class) // 导入公共MVC配置
public class HMallApplication {
    public static void main(String[] args) {
        SpringApplication.run(HMallApplication.class, args);
    }
}