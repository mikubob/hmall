package com.hmall.api.client;

import com.hmall.api.fallback.UserClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service",fallbackFactory = UserClientFallback.class)
public interface UserClient {

    // 扣减用户余额
    @PutMapping("/users/money/deduct")
    void deductMoney(@RequestParam("pw") String pw, @RequestParam("amount") Integer amount);
}
