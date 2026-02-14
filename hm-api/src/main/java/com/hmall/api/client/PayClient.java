package com.hmall.api.client;

import com.hmall.api.dto.PayOrderDTO;
import com.hmall.api.fallback.PayClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(value = "pay-service",fallbackFactory = PayClientFallback.class)
public interface PayClient {
    @GetMapping("/pay-orders/biz/{id}")
    PayOrderDTO queryPayOrderByBizOrderNo(@PathVariable("id") Long id);

    @PutMapping("/pay-orders/status/{id}/{status}")
    void updatePayOrderStatusByBizOrderNo(@PathVariable("id") Long orderId, @PathVariable("status") Integer status);
}
