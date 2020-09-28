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
        final String request = new HttpUtils("http://localhost:8080/hello/key")
                .addHeader("token", "ecba0375509a4a548b6fc8cabc9575c6")
                .addParam("algorithm", "AES")
                .addParam("size", "128")
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
