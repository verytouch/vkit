package com.verytouch.vkit.samples.rabbitmq.common;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

public final class RabbitChannel {

    private RabbitChannel() {}

    public static Channel newChannel(RabbitQueue queue) {
        return new Builder(RabbitConnection.getConnection())
                .queue(queue)
                .build();
    }

    public static Channel newChannel(RabbitQueueBind bind) {
        return new Builder(RabbitConnection.getConnection())
                .bind(bind)
                .build();
    }

    public static final class Builder {
        private final Connection connection;
        private RabbitQueue queue;
        private RabbitExchange exchange;
        private RabbitQueueBind bind;

        public Builder(Connection connection) {
            this.connection = connection;
        }

        public Builder queue(RabbitQueue queue) {
            this.queue = queue;
            return this;
        }

        public Builder exchange(RabbitExchange exchange) {
            this.exchange = exchange;
            return this;
        }

        public Builder bind(RabbitQueueBind bind) {
            this.bind = bind;
            return this;
        }

        public Channel build() {
            if (bind != null) {
                this.queue = bind.getQueue();
                this.exchange = bind.getExchange();
            }
            if (queue == null) {
               throw new RuntimeException("获取rabbit-channel失败，queue不能为空");
            }
            try {
                Channel channel = connection.createChannel();
                channel.queueDeclare(queue.name(), true, false, false, null);
                if (exchange != null) {
                    channel.exchangeDeclare(exchange.name(), exchange.getType());
                }
                if (bind != null) {
                    channel.queueBind(queue.name(), bind.getExchange().name(), bind.getRoutingKey());
                }
                return channel;
            } catch (IOException e) {
                throw new RuntimeException("获取rabbit-channel失败", e);
            }
        }
    }
}
