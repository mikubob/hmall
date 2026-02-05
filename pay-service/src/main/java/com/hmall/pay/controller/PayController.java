package com.hmall.pay.controller;

import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.BeanUtils;
import com.hmall.pay.domain.dto.PayApplyDTO;
import com.hmall.pay.domain.dto.PayOrderFormDTO;
import com.hmall.pay.domain.vo.PayOrderVO;
import com.hmall.pay.enums.PayType;
import com.hmall.pay.service.IPayOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "支付相关接口")
@RestController
@RequestMapping("pay-orders")
@RequiredArgsConstructor
public class PayController {

    private final IPayOrderService payOrderService;

    @Operation(summary = "生成支付单")
    @PostMapping
    public String applyPayOrder(@RequestBody PayApplyDTO applyDTO){
        if(!PayType.BALANCE.equalsValue(applyDTO.getPayType())){
            // 目前只支持余额支付
            throw new BizIllegalException("抱歉，目前只支持余额支付");
        }
        return payOrderService.applyPayOrder(applyDTO);
    }

    @Operation(summary = "尝试基于用户余额支付")
    @Parameter(name = "id", description = "支付单id")
    @PostMapping("{id}")
    public void tryPayOrderByBalance(@PathVariable("id") Long id, @RequestBody PayOrderFormDTO payOrderFormDTO){
        payOrderFormDTO.setId(id);
        payOrderService.tryPayOrderByBalance(payOrderFormDTO);
    }

    @Operation(summary = "查询支付单")
    @GetMapping
    public List<PayOrderVO> queryPayOrders(){
        return BeanUtils.copyList(payOrderService.list(), PayOrderVO.class);
    }
}