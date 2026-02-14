package com.hmall.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.pay.domain.dto.PayApplyDTO;
import com.hmall.pay.domain.dto.PayOrderFormDTO;
import com.hmall.pay.domain.po.PayOrder;


/**
 * <p>
 * 支付订单 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-16
 */
public interface IPayOrderService extends IService<PayOrder> {

    /**
     * 申请支付订单
     * @param applyDTO
     * @return
     */
    String applyPayOrder(PayApplyDTO applyDTO);

    /**
     * 尝试使用余额支付订单
     * @param payOrderFormDTO
     */
    void tryPayOrderByBalance(PayOrderFormDTO payOrderFormDTO);

    /**
     * 修改支付订单状态
     * @param orderId
     * @param status
     */
    void updatePayOrderStatusByBizOrderNo(Long orderId, Integer status);
}
