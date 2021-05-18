package top.verytouch.vkit.samples.rabbitmq.common;

public enum RabbitQueue {
    /**
     * 基本消息模型：一个生产者对应一个消费者
     */
    QUE_BASIC,

    /**
     * 工作队列模型：一个生产者对应多个消费者，一条消息只被一个消费者消费
     */
    QUE_WORK,

    /**
     * 订阅模型：一条消息可被多个消费者消费
     */
    QUE_FANOUT,
    QUE_DIRECT,
    QUE_TOPIC
}
