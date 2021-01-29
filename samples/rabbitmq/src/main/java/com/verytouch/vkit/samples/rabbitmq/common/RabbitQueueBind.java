package com.verytouch.vkit.samples.rabbitmq.common;

import static com.verytouch.vkit.samples.rabbitmq.common.RabbitExchange.EXC_DIRECT;
import static com.verytouch.vkit.samples.rabbitmq.common.RabbitQueue.QUE_DIRECT;
import static com.verytouch.vkit.samples.rabbitmq.common.RabbitQueue.QUE_TOPIC;

/**
 * 消息队列绑定到交换机
 */
public enum RabbitQueueBind {
    SMS_DEV(QUE_DIRECT, EXC_DIRECT, "sms.dev"),
    SMS_TEST(QUE_TOPIC, EXC_DIRECT, "sms.test");

    private final RabbitQueue queue;
    private final RabbitExchange exchange;
    private final String routingKey;

    RabbitQueueBind(RabbitQueue queue, RabbitExchange exchange, String routingKey) {
        this.queue = queue;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public RabbitQueue getQueue() {
        return queue;
    }

    public RabbitExchange getExchange() {
        return exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

}
