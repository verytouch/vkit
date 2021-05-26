package top.verytouch.vkit.common.util;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpUtilsTest {

    @Test
    public void test() throws Exception {
        final HttpUtils.HttpResponse response = new HttpUtils("https://api.weixin.qq.com/sns/jscode2session")
                // .addHeader("appid", "APPID")
                .addParam("appid", "123456")
                .addParam("secret", "123456")
                .addParam("js_code", "043bLi0w3IHq2V2tHP0w3E8JJL0bLi0E")
                .addParam("grant_type", "authorization_code")
                .request();
        System.out.println(response.getHeaders());
        System.out.println(response.getString());
    }

    @Test
    public void testGet() {
        System.out.println(HttpUtils.get("https://www.jsanai.com/api/selfnews/newslist?type=1"));
    }

    @Test
    public void testPost() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", "MzEwMDA3NA");
        final String post = HttpUtils.post("https://www.jsanai.com/api/selfnews/newsd", params);
        System.out.println(post);
    }

    @Test
    public void testJson() {
        System.out.println(HttpUtils.postJson("http://localhost:8080/hello/json", "{\"data\": \"白日依山尽，黄河入海流。欲穷千里目，更上一层楼。\", \"type\": 1}"));
    }

    @Test
    public void testDownload() throws Exception {
        IOUtils.write(HttpUtils.download("http://localhost:8080/hello/file"), new FileOutputStream(System.getProperty("user.home") + "\\DeskTop\\test.json"));

    }
}
