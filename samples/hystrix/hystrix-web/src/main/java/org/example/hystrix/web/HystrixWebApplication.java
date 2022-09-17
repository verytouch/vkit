package org.example.hystrix.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import feign.Logger;
import org.example.hystrix.api.HelloFeign;
import org.example.hystrix.api.ResultUtil;
import org.example.hystrix.web.command.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SpringBootApplication(scanBasePackages = "org.example.hystrix")
@EnableEurekaClient
@EnableFeignClients(basePackages = "org.example.hystrix.api")
@RestController
// 引入Dashboard开始
@EnableHystrixDashboard
@EnableCircuitBreaker
// 引入Dashboard结束
public class HystrixWebApplication {

    @Autowired
    private HelloFeign helloFeign;

    @Autowired
    private CommandService commandService;

    public static void main(String[] args) {
        SpringApplication.run(HystrixWebApplication.class, args);
    }

    /*
     * feign方式
     */
    @GetMapping("hello")
    public Object hello() {
        return helloFeign.hello();
    }

    /*
     * 命令方式
     */
    @GetMapping("command/{name}")
    public Object command(@PathVariable("name") String name) {
        try {
            Method method = CommandService.class.getMethod(name);
            return method.invoke(commandService);
        } catch (NoSuchMethodException e) {
            return ResultUtil.fail().msg("no such method：" + name);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return ResultUtil.fail().msg(e.getMessage());
        }
    }

    /*
     * 注解方式
     */
    @HystrixCommand(defaultFallback = "defaultFallback")
    @GetMapping("annotation")
    public ResultUtil annotation(@RequestParam(value = "failed", required = false, defaultValue = "") String failed) {
        if ("1".equals(failed)) {
            throw new RuntimeException();
        }
        return ResultUtil.ok().msg("hi");
    }

    private ResultUtil defaultFallback() {
        return ResultUtil.fail().msg("熔断");
    }

    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        /*
         * NONE，无记录（DEFAULT）。
         * BASIC，只记录请求方法和URL以及响应状态代码和执行时间。
         * HEADERS，记录基本信息以及请求和响应头。
         * FULL，记录请求和响应的头文件，正文和元数据。
         * */
        return Logger.Level.FULL;
    }

}
