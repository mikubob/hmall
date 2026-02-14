package com.hmall.trade.constants;

public interface MQConstants {
    String DELAY_EXCHANGE_NAME="trade.delay.direct";//延时交换机名称
    String DELAY_ORDER_QUEUE_NAME="trade.delay.order.queue";//延时队列名称
    String DELAY_ORDER_KEY="delay.order.query";//延时队列绑定的key
}
