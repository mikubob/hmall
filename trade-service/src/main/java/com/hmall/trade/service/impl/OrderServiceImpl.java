package com.hmall.trade.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.api.client.CartClient;
import com.hmall.api.client.ItemClient;
import com.hmall.api.client.PayClient;
import com.hmall.api.dto.ItemDTO;
import com.hmall.api.dto.OrderDetailDTO;
import com.hmall.common.exception.BadRequestException;
import com.hmall.common.utils.UserContext;
import com.hmall.trade.constants.MQConstants;
import com.hmall.trade.domain.dto.OrderFormDTO;
import com.hmall.trade.domain.po.Order;
import com.hmall.trade.domain.po.OrderDetail;
import com.hmall.trade.mapper.OrderMapper;
import com.hmall.trade.service.IOrderDetailService;
import com.hmall.trade.service.IOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    private final IOrderDetailService detailService;
    private final ItemClient itemClient;
    private final CartClient cartClient;
    private final RabbitTemplate rabbitTemplate;
    private final PayClient payClient;
    private final IOrderDetailService orderDetailService;

    @Override
    @GlobalTransactional
    public Long createOrder(OrderFormDTO orderFormDTO) {
        log.info("开始创建订单...");
        // 1.订单数据
        Order order = new Order();
        // 1.1.查询商品
        List<OrderDetailDTO> detailDTOS = orderFormDTO.getDetails();
        // 1.2.获取商品id和数量的Map
        Map<Long, Integer> itemNumMap = detailDTOS.stream()
                .collect(Collectors.toMap(OrderDetailDTO::getItemId, OrderDetailDTO::getNum));
        Set<Long> itemIds = itemNumMap.keySet();
        // 1.3.查询商品
        List<ItemDTO> items = itemClient.queryItemByIds(itemIds);
        if (items == null || items.size() < itemIds.size()) {
            throw new BadRequestException("商品不存在");
        }
        // 1.4.基于商品价格、购买数量计算商品总价：totalFee
        int total = 0;
        for (ItemDTO item : items) {
            total += item.getPrice() * itemNumMap.get(item.getId());
        }
        order.setTotalFee(total);
        // 1.5.其它属性
        order.setPaymentType(orderFormDTO.getPaymentType());
        order.setUserId(UserContext.getUser());
        order.setStatus(1);
        // 时间字段由 MyMetaObjectHandler 自动填充
        
        // 1.6.将Order写入数据库order表中
        log.info("准备保存订单，ID将为: {}", order.getId());
        save(order);
        log.info("订单保存完成，实际ID: {}", order.getId());

        // 2.保存订单详情
        List<OrderDetail> details = buildDetails(order.getId(), items, itemNumMap);
        log.info("准备保存订单详情，订单ID: {}，详情数量: {}", order.getId(), details.size());
        detailService.saveBatch(details);
        log.info("订单详情保存完成");

        // 3.扣减库存
        try {
            itemClient.deductStock(detailDTOS);
        } catch (Exception e) {
            throw new RuntimeException("库存不足！");
        }

        // 4.清理购物车商品
        cartClient.deleteCartItemByIds(itemIds);
        log.info("订单创建流程完成，返回订单ID: {}", order.getId());
        // 5.发送延迟消息，检测订单支付状态
        rabbitTemplate.convertAndSend(
                MQConstants.DELAY_EXCHANGE_NAME,// 交换机名称
                MQConstants.DELAY_ORDER_KEY,// 路由键
                order.getId(),// 消息
                message -> {
                    message.getMessageProperties().setDelayLong(10000L);// 延迟时间
                    return message;
                }
        );
        return order.getId();
    }

    @Override
    public void markOrderPaySuccess(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(2);
        order.setPayTime(LocalDateTime.now());
        updateById(order);
    }

    /**
     * 取消订单
     * @param orderId
     */
    @Override
    public void cancelOrder(Long orderId) {
        //1.修改交易订单状态为已关闭
        lambdaUpdate()
                .set(Order::getStatus, 5)
                .eq(Order::getId, orderId)
                .update();
        //2.修改订单详情状态为已取消
        payClient.updatePayOrderStatusByBizOrderNo(orderId,5);
        //3.恢复订单中已经扣除的库存
        List<OrderDetail> list=detailService.lambdaQuery().eq(OrderDetail::getOrderId,orderId).list();
        List<OrderDetailDTO> orderDetailDTOS = BeanUtil.copyToList(list, OrderDetailDTO.class);
        itemClient.restoreStock(orderDetailDTOS);
    }

    private List<OrderDetail> buildDetails(Long orderId, List<ItemDTO> items, Map<Long, Integer> numMap) {
        List<OrderDetail> details = new ArrayList<>(items.size());
        for (ItemDTO item : items) {
            OrderDetail detail = new OrderDetail();
            detail.setName(item.getName());
            detail.setSpec(item.getSpec());
            detail.setPrice(item.getPrice());
            detail.setNum(numMap.get(item.getId()));
            detail.setItemId(item.getId());
            detail.setImage(item.getImage());
            detail.setOrderId(orderId);
            details.add(detail);
        }
        return details;
    }
}
