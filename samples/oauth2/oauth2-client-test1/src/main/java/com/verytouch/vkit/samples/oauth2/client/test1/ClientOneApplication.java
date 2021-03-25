package com.verytouch.vkit.samples.oauth2.client.test1;

import com.verytouch.vkit.samples.oauth2.client.starter.EnableSso;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSso
@SpringBootApplication
public class ClientOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientOneApplication.class, args);
    }
}
