package com.verytouch.vkit.samples.rabbitmq.common;

import com.rabbitmq.client.BuiltinExchangeType;

/**
 * 1.生产者将消息发送到交换机
 * 2.交换机将消息发送到消费者的消息队列
 * 3.消息队列被消费者消费
 */
public enum RabbitExchange {

    /**
     * 广播，消息发送到所有与之绑定的队列
     */
    EXC_FANOUT(BuiltinExchangeType.FANOUT),
    /**
     * 定向，消息按照Routing Key发送到指定队列
     */
    EXC_DIRECT(BuiltinExchangeType.DIRECT),
    /**
     * 通配，消息按照Routing Key发送到指定队列，可以使用通配符
     * 1.v.kit.# 可匹配v.kit.samples和v.kit.samples.rabbit
     * 2.v.kit.* 可匹配v.kit.samples
     */
    EXC_TOPIC(BuiltinExchangeType.TOPIC);

    private final BuiltinExchangeType type;

    RabbitExchange(BuiltinExchangeType type) {
        this.type = type;
    }

    public BuiltinExchangeType getType() {
        return type;
    }
}
