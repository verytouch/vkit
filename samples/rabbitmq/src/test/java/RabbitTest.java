import top.verytouch.vkit.samples.rabbitmq.common.RabbitQueue;
import top.verytouch.vkit.samples.rabbitmq.common.RabbitQueueBind;
import top.verytouch.vkit.samples.rabbitmq.consumer.RabbitConsumer;
import top.verytouch.vkit.samples.rabbitmq.producer.RabbitProducer;
import org.junit.Test;

public class RabbitTest {

    @Test
    public void testProduce() {
        RabbitProducer.send(RabbitQueue.QUE_BASIC, "basic");
    }

    @Test
    public void testConsume() {
        RabbitConsumer.consume(RabbitQueue.QUE_DIRECT);
    }

    @Test
    public void testRouting() {
        RabbitProducer.send(RabbitQueueBind.SMS_TEST, "哇哈哈");
    }

    @Test
    public void test() {
        RabbitConsumer.consumeAllBind(true);
    }

}
