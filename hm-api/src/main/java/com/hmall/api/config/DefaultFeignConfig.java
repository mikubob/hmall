package com.hmall.api.config;

import com.hmall.api.fallback.CartClientFallbackFactory;
import com.hmall.api.fallback.ItemClientFallbackFactory;
import com.hmall.api.fallback.TradeClientFallback;
import com.hmall.api.fallback.UserClientFallback;
import com.hmall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLogLevel(){
        return Logger.Level.NONE;
    }

    @Bean
    public RequestInterceptor userInfoRequestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                Long userId = UserContext.getUser();
                if (userId != null) {
                    requestTemplate.header("user-info", userId.toString());
                }
            }
        };
    }

    @Bean
    public ItemClientFallbackFactory itemClientFallbackFactory(){
        return new ItemClientFallbackFactory();
    }
    @Bean
    public CartClientFallbackFactory cartClientFallbackFactory(){
        return new CartClientFallbackFactory();
    }
    @Bean
    public TradeClientFallback tradeClientFallback(){
        return new TradeClientFallback();
    }
    @Bean
    public UserClientFallback userClientFallback(){
        return new UserClientFallback();
    }
}