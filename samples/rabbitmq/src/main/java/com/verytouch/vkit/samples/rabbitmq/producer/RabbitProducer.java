package com.verytouch.vkit.samples.rabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.verytouch.vkit.samples.rabbitmq.common.RabbitChannel;
import com.verytouch.vkit.samples.rabbitmq.common.RabbitQueue;
import com.verytouch.vkit.samples.rabbitmq.common.RabbitQueueBind;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 * 消息防止丢失：生产者确认，消费者手动ACK，队列、交换机、消息持久化
 */
public class RabbitProducer {

    public static void send(RabbitQueue queue, String message) {
        try (Channel channel = RabbitChannel.newChannel(queue)) {
            channel.basicPublish("", queue.name(), null, message.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(String.format("消息发送失败，queue=%s, message=%s", queue, message));
        }
    }

    public static void send(RabbitQueueBind bind, String message) {
        try (Channel channel = RabbitChannel.newChannel(bind)) {
            channel.basicPublish(bind.getExchange().name(), bind.getRoutingKey(), null, message.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(String.format("消息发送失败，bind=%s, message=%s", bind, message));
        }
    }

    /**
     * 确认机制
     */
    public static void sendConfirm(RabbitQueueBind bind, String message) {
        try (Channel channel = RabbitChannel.newChannel(bind)) {
            channel.confirmSelect();
            channel.basicPublish(bind.getExchange().name(), bind.getQueue().name(), null, message.getBytes());
            // 同步确认
            if (!channel.waitForConfirms()) {
                System.out.println("发送失败");
            }
            // 异步确认
            // channel.addConfirmListener(new ConfirmListener() { ... });
        } catch (Exception e) {
            throw new RuntimeException(String.format("消息发送失败，bind=%s, message=%s", bind, message));
        }
    }

    /**
     * 事物机制
     */
    public static void sendTx(RabbitQueueBind bind, String message) {
        Channel channel = null;
        try {
            channel = RabbitChannel.newChannel(bind);
            channel.txSelect();
            channel.basicPublish(bind.getExchange().name(), bind.getQueue().name(), MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            channel.txCommit();
        } catch (Exception e) {
            if (channel != null) {
                try {
                    channel.txRollback();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            throw new RuntimeException(String.format("消息发送失败，bind=%s, message=%s", bind, message));
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
