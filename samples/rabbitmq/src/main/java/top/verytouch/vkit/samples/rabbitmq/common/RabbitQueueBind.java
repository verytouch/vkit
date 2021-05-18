package top.verytouch.vkit.samples.rabbitmq.common;

import static top.verytouch.vkit.samples.rabbitmq.common.RabbitExchange.EXC_DIRECT;

/**
 * 消息队列绑定到交换机
 */
public enum RabbitQueueBind {
    SMS_DEV(RabbitQueue.QUE_DIRECT, EXC_DIRECT, "sms.dev"),
    SMS_TEST(RabbitQueue.QUE_TOPIC, EXC_DIRECT, "sms.test");

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
