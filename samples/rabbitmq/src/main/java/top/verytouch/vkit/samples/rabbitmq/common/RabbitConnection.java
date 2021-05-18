package top.verytouch.vkit.samples.rabbitmq.common;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public final class RabbitConnection {

    private static final String HOST = "47.92.98.183";
    private static final int PORT = 8073;
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";
    private static volatile Connection connection;

    private RabbitConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            synchronized (RabbitConnection.class) {
                if (connection == null) {
                    connection = newConnection();
                }
            }
        }
        return connection;
    }

    public static Connection newConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);

        try {
            return factory.newConnection();
        } catch (Exception e) {
            throw new RuntimeException("获取rabbit-connection失败", e);
        }
    }

}
