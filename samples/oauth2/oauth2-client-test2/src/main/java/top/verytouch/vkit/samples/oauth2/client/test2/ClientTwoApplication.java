package top.verytouch.vkit.samples.oauth2.client.test2;

import top.verytouch.vkit.samples.oauth2.client.starter.EnableSso;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSso
@SpringBootApplication
public class ClientTwoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientTwoApplication.class, args);
    }
}
