package com.hmall.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.trade.domain.dto.OrderFormDTO;
import com.hmall.trade.domain.po.Order;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
public interface IOrderService extends IService<Order> {

    /**
     * 创建订单
     * @param orderFormDTO
     * @return
     */
    Long createOrder(OrderFormDTO orderFormDTO);

    /**
     * 标记订单支付成功
     * @param orderId
     */
    void markOrderPaySuccess(Long orderId);

    /**
     * 取消订单
     * @param orderId
     */
    void cancelOrder(Long orderId);
}
