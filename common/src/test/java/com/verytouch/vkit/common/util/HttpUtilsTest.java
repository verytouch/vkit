package com.verytouch.vkit.common.util;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpUtilsTest {

    public static void main(String[] args) {
        System.out.println(StandardCharsets.UTF_8);
    }

    @Test
    public void test() throws Exception {
        final String request = new HttpUtils("https://api.weixin.qq.com/sns/jscode2session")
                // .addHeader("appid", "APPID")
                .addParam("appid", "wxe77d4de1fb91a65a")
                .addParam("secret", "b57fffc70322664a5fb2a7a0e579ee15")
                .addParam("js_code", "043bLi0w3IHq2V2tHP0w3E8JJL0bLi0E")
                .addParam("grant_type", "authorization_code")
                .request();
        System.out.println(request);
    }

    @Test
    public void testPost() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("data", "白日依山尽，黄河入海流。欲穷千里目，更上一层楼。");
        params.put("key", "b2483441a12c13f2420c0c864378b807");
        final String post = HttpUtils.post("http://localhost:8080/hello/crypt", params);
        System.out.println(post);
    }

    @Test
    public void testJson() throws Exception {
        System.out.println(HttpUtils.postJson("http://localhost:8080/hello/json", "{\"data\": \"白日依山尽，黄河入海流。欲穷千里目，更上一层楼。\", \"type\": 1}"));
    }
}
