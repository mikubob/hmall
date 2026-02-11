package com.hmall.api.client;

import com.hmall.api.fallback.TradeClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(value = "trade-service",fallbackFactory = TradeClientFallback.class)
public interface TradeClient {

    // 修改订单状态
    @PutMapping("/orders/{orderId}")
    void markOrderPaySuccess(@PathVariable("orderId") Long orderId);
}