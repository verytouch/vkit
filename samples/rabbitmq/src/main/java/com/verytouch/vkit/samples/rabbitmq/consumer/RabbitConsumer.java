package com.verytouch.vkit.samples.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import com.verytouch.vkit.samples.rabbitmq.common.RabbitChannel;
import com.verytouch.vkit.samples.rabbitmq.common.RabbitQueue;
import com.verytouch.vkit.samples.rabbitmq.common.RabbitQueueBind;

import java.io.IOException;

/**
 * 消费者
 * 防止重复消费：根据业务逻辑自己保证幂等
 */
public class RabbitConsumer {

    public static void consume(RabbitQueue queue) {
        try {
            Channel channel = RabbitChannel.newChannel(queue);
            channel.basicConsume(queue.name(), true, new AckConsumer(queue, channel, true));
        } catch (Exception e) {
            throw new RuntimeException("消费失败，queue=" + queue);
        }
        waitHere();
    }

    public static void consumeAllBind(boolean autoAck) {
        try {
            for (RabbitQueueBind value : RabbitQueueBind.values()) {
                Channel channel = RabbitChannel.newChannel(value);
                channel.basicConsume(value.getQueue().name(), autoAck, new AckConsumer(value, channel, autoAck));
            }
        } catch (IOException e) {
            throw new RuntimeException("消费失败");
        }
        waitHere();
    }

    private static void waitHere() {
        for (;;) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
