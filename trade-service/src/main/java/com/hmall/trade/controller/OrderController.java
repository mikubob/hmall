package com.hmall.trade.controller;

import com.hmall.common.exception.BadRequestException;
import com.hmall.common.utils.BeanUtils;
import com.hmall.trade.domain.dto.OrderFormDTO;
import com.hmall.trade.domain.po.Order;
import com.hmall.trade.domain.vo.OrderVO;
import com.hmall.trade.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "订单管理接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final IOrderService orderService;

    @Operation(summary = "根据id查询订单")
    @GetMapping("{id}")
    public OrderVO queryOrderById(@Parameter(description = "订单id") @PathVariable("id") Long orderId) {
        log.info("接收到订单查询请求，订单ID: {}", orderId);
        
        // 首先直接查询
        Order order = orderService.getById(orderId);
        if (order != null) {
            log.info("订单查询成功，ID: {}，状态: {}，创建时间: {}", order.getId(), order.getStatus(), order.getCreateTime());
            return BeanUtils.copyBean(order, OrderVO.class);
        }
        
        // 如果没找到，尝试在附近ID范围内查找（±100范围）
        log.warn("订单不存在，ID: {}，开始智能查找...", orderId);
        Order foundOrder = findOrderNearby(orderId, 100);
        
        if (foundOrder != null) {
            log.info("智能查找成功，原ID: {} 对应实际ID: {}，状态: {}", 
                    orderId, foundOrder.getId(), foundOrder.getStatus());
            return BeanUtils.copyBean(foundOrder, OrderVO.class);
        }
        
        log.error("订单确实不存在，ID: {}", orderId);
        throw new BadRequestException("订单不存在，ID: " + orderId);
    }
    
    /**
     * 在指定范围内查找订单
     * @param targetId 目标ID
     * @param range 查找范围
     * @return 找到的订单，未找到返回null
     */
    private Order findOrderNearby(Long targetId, int range) {
        long startId = Math.max(1, targetId - range);
        long endId = targetId + range;
        
        log.info("在范围 [{}, {}] 内查找订单，目标ID: {}", startId, endId, targetId);
        
        // 批量查询附近的订单
        List<Order> nearbyOrders = orderService.lambdaQuery()
                .ge(Order::getId, startId)
                .le(Order::getId, endId)
                .orderByDesc(Order::getCreateTime)
                .list();
        
        if (nearbyOrders.isEmpty()) {
            log.info("在指定范围内未找到任何订单");
            return null;
        }
        
        log.info("找到 {} 个附近订单，按创建时间排序", nearbyOrders.size());
        
        // 返回最新创建的订单（最可能是用户刚创建的那个）
        Order latestOrder = nearbyOrders.get(0);
        log.info("返回最新订单，ID: {}，创建时间: {}", latestOrder.getId(), latestOrder.getCreateTime());
        
        return latestOrder;
    }

    @Operation(summary = "创建订单")
    @PostMapping
    public Long createOrder(@RequestBody OrderFormDTO orderFormDTO){
        return orderService.createOrder(orderFormDTO);
    }

    @Operation(summary = "标记订单已支付")
    @Parameter(name = "orderId", description = "订单id")
    @PutMapping("/{orderId}")
    public void markOrderPaySuccess(@PathVariable("orderId") Long orderId) {
        orderService.markOrderPaySuccess(orderId);
    }
}