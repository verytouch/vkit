package top.verytouch.vkit.samples.rabbitmq.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class AckConsumer extends DefaultConsumer {

    private final Object id;
    private final boolean autoAck;

    public AckConsumer(Object id, Channel channel, boolean autoAck) {
        super(channel);
        this.id = id;
        this.autoAck = autoAck;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String s = new String(body);
        Channel channel = getChannel();
        System.out.println(id + "收到消息：" + s);
        if (!autoAck) {
            channel.basicAck(envelope.getDeliveryTag(), false);
        }
    }
}
