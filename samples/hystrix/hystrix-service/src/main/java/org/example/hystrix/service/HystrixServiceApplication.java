package org.example.hystrix.service;

import org.example.hystrix.api.ResultUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@EnableEurekaClient
@RestController
public class HystrixServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixServiceApplication.class, args);
    }

    @GetMapping("hello")
    public ResultUtil hello() throws InterruptedException {
        // 模拟随机超时
        int i = (int) (Math.random() * 5);
        Thread.sleep(i * 1000);
        return ResultUtil.ok().msg(i);
    }

}
