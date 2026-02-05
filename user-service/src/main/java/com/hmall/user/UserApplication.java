package com.hmall.user;

import com.hmall.common.config.MvcConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@MapperScan("com.hmall.user.mapper")
@SpringBootApplication
@Import(MvcConfig.class) // 导入公共MVC配置
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}