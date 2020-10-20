package com.verytouch.vkit.common.util;

import com.verytouch.vkit.common.base.Assert;
import lombok.Data;
import org.apache.commons.io.IOUtils;

import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * http请求工具类
 *
 * @author verytouch
 * @since  2020/9/17 16:32
 */
@Data
public class HttpUtils {

    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_FORM = "application/x-www-url-form-encoded";

    private String url;
    private String method = "GET";
    private Map<String, String> headers;
    private Map<String, Object> params;
    private String body;
    private Charset charset = StandardCharsets.UTF_8;
    private int connectTimeout = 3000;
    private int readTimeout = 10000;
    private boolean followRedirects = false;
    private boolean useCaches = false;
    private boolean encodeParam = true;

    public HttpUtils(String url) {
        Assert.nonNull(url, "url cannot be null or empty");
        this.url = url;
    }

    /**
     * 发送get请求
     * @param url 请求地址
     * @return 接口返回的字符串
     * @throws Exception 失败抛出异常
     */
    public static String get(String url) throws Exception {
        return new HttpUtils(url).get();
    }

    /**
     * 发送get请求
     * @param url 请求地址，不带参数
     * @param params 请求参数，拼接在url后面
     * @return 接口返回的字符串
     * @throws Exception 失败抛出异常
     */
    public static String get(String url, Map<String, Object> params) throws Exception {
        return new HttpUtils(url).params(params).get();
    }

    /**
     * 发送post请求
     * @param url 请求地址，不带参数
     * @param params 请求参数，拼接在url后面
     * @return 接口返回的字符串
     * @throws Exception 失败抛出异常
     */
    public static String post(String url, Map<String, Object> params) throws Exception {
        return new HttpUtils(url).params(params).post();
    }

    /**
     * 发送post请求
     * @param url 请求地址
     * @param json json格式的参数，contentType=application/json
     * @return 接口返回的字符串
     * @throws Exception 失败抛出异常
     */
    public static String postJson(String url, String json) throws Exception {
        return new HttpUtils(url)
                .addHeader("Content-Type", APPLICATION_JSON)
                .body(json)
                .post();
    }

    public static byte[] download(String url) throws Exception {
        return new HttpUtils(url).request().getBytes();
    }

    public Response request() throws Exception {
        if (params != null && params.size() > 0) {
            url += "?" + params.entrySet().stream().
                    map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(body != null);
            connection.setUseCaches(useCaches);
            connection.setInstanceFollowRedirects(followRedirects);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setRequestMethod(method);
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                    connection.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
                }
            }
            connection.connect();
            if (body != null) {
                try (OutputStream out = connection.getOutputStream()) {
                    IOUtils.copy(new StringReader(body), out, charset);
                }
            }
            Assert.require(connection.getResponseCode() == 200, String.format("request failed: code=%s, message=%s",
                    connection.getResponseCode(), connection.getResponseMessage()));
            return new Response(IOUtils.toByteArray(connection.getInputStream()), connection.getHeaderFields());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String get() throws Exception {
        return method("GET").request().getString();
    }

    public String post() throws Exception {
        return method("POST").request().getString();
    }

    public HttpUtils method(String method) {
        this.method = method;
        return this;
    }

    public HttpUtils headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpUtils addHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(key, value);
        return this;
    }

    public HttpUtils params(Map<String, Object> params) {
        this.params = params;
        if (encodeParam && this.params != null) {
            for (Map.Entry<String, Object> entry : this.params.entrySet()) {
                entry.setValue(urlEncode(entry.getValue().toString()));
            }
        }
        return this;
    }

    public HttpUtils addParam(String key, String value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(key, encodeParam ? urlEncode(value) : value);
        return this;
    }

    public HttpUtils charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public HttpUtils encodeParam(boolean encodeParam) {
        this.encodeParam = encodeParam;
        return this;
    }

    public HttpUtils body(String body) {
        this.body = body;
        return this;
    }

    public HttpUtils readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public HttpUtils connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public HttpUtils useCaches(boolean useCaches) {
        this.useCaches = useCaches;
        return this;
    }

    public HttpUtils followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    public static class Response {
        private final byte[] bytes;
        private final Map<String, String> headers;

        public Response(byte[] bytes, Map<String, List<String>> headers) {
            this.bytes = bytes;
            this.headers = headers.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            entry -> entry.getValue() != null && !entry.getValue().isEmpty() ? entry.getValue().get(0) : ""));
        }

        public String getString() {
            return new String(bytes);
        }
    }
}
