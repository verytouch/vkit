package top.verytouch.vkit.common.util;

import lombok.Data;
import org.apache.commons.io.IOUtils;
import top.verytouch.vkit.common.base.Assert;
import top.verytouch.vkit.common.exception.AssertException;
import top.verytouch.vkit.common.exception.BusinessException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * http请求工具类
 *
 * @author verytouch
 * @since 2020/9/17 16:32
 */
@SuppressWarnings("unused")
public class HttpUtils {

    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_FORM = "application/x-www-url-form-encoded";

    private String url;
    private String method = "GET";
    private Map<String, String> headers;
    private Map<String, Object> params;
    private byte[] body;
    private Charset charset = StandardCharsets.UTF_8;
    private int connectTimeout = 3000;
    private int readTimeout = 10000;
    private boolean followRedirects = false;
    private boolean useCaches = false;
    private boolean encodeParam = true;
    private Proxy proxy;

    public HttpUtils(String url) {
        Assert.nonNull(url, "url cannot be null or empty");
        this.url = url;
    }

    /**
     * 发送get请求
     *
     * @param url 请求地址
     * @return 接口返回的字符串
     */
    public static String get(String url) {
        return new HttpUtils(url).get();
    }

    /**
     * 发送get请求
     *
     * @param url    请求地址，不带参数
     * @param params 请求参数，拼接在url后面
     * @return 接口返回的字符串
     */
    public static String get(String url, Map<String, Object> params) {
        return new HttpUtils(url).params(params).get();
    }

    /**
     * 发送post请求
     *
     * @param url    请求地址，不带参数
     * @param params 请求参数，拼接在url后面
     * @return 接口返回的字符串
     */
    public static String post(String url, Map<String, Object> params) {
        return new HttpUtils(url).params(params).post();
    }

    /**
     * 发送post请求
     *
     * @param url  请求地址
     * @param body 参数，内部转换为json字符串，contentType=application/json
     * @return 接口返回的字符串
     */
    public static String postJson(String url, Object body) {
        return new HttpUtils(url)
                .addHeader("Content-Type", APPLICATION_JSON)
                .body(JsonUtils.toJson(body).getBytes(StandardCharsets.UTF_8))
                .post();
    }

    /**
     * 下载
     */
    public static byte[] download(String url) {
        return new HttpUtils(url).caughtRequest().getBytes();
    }

    /**
     * 执行请求，失败抛出BusinessException
     */
    public HttpResponse caughtRequest() {
        try {
            return request();
        } catch (Exception e) {
            throw new BusinessException("Http请求失败：" + e.getMessage());
        }
    }

    /**
     * 执行请求
     */
    public HttpResponse request() throws Exception {
        HttpURLConnection connection = null;
        try {
            connection = getConnection(null);
            return new HttpResponse(IOUtils.toByteArray(connection.getInputStream()), connection.getHeaderFields());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 执行SSE请求
     */
    public void sseRequest(Consumer<String> consumer) throws Exception {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            connection = getConnection(con -> con.setRequestProperty("Accept", "text/event-stream"));
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                consumer.accept(line);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    private HttpURLConnection getConnection(Consumer<HttpURLConnection> beforeConnect) throws Exception {
        if (params != null && !params.isEmpty()) {
            url += "?" + params.entrySet().stream().
                    map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));
        }
        URL u = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) (this.proxy != null ? u.openConnection(this.proxy) : u.openConnection());
        connection.setDoInput(true);
        connection.setDoOutput(body != null);
        connection.setUseCaches(useCaches);
        connection.setInstanceFollowRedirects(followRedirects);
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        connection.setRequestMethod(method);
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                connection.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        if (beforeConnect != null) {
            beforeConnect.accept(connection);
        }
        if (body != null) {
            try (OutputStream out = connection.getOutputStream()) {
                IOUtils.copy(new ByteArrayInputStream(body), out);
            }
        }
        connection.connect();
        if (connection.getResponseCode() != 200) {
            String message = String.format("request failed: code=%s, message=%s", connection.getResponseCode(), connection.getResponseMessage());
            connection.disconnect();
            throw new AssertException(message);
        }
        return connection;
    }

    public String get() {
        return method("GET").caughtRequest().getString();
    }

    public String post() {
        return method("POST").caughtRequest().getString();
    }

    /**
     * 设置请求方法
     */
    public HttpUtils method(String method) {
        this.method = method.toUpperCase();
        return this;
    }

    /**
     * 设置header
     */
    public HttpUtils headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * 添加一个header
     */
    public HttpUtils addHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(key, value);
        return this;
    }

    /**
     * 设置路径
     */
    public HttpUtils path(String path) {
        this.url += path;
        return this;
    }

    /**
     * 设置参数
     */
    public HttpUtils params(Map<String, Object> params) {
        this.params = params;
        if (encodeParam && this.params != null) {
            for (Map.Entry<String, Object> entry : this.params.entrySet()) {
                entry.setValue(urlEncode(entry.getValue().toString()));
            }
        }
        return this;
    }

    /**
     * 添加一个参数
     */
    public HttpUtils addParam(String key, String value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(key, encodeParam ? urlEncode(value) : value);
        return this;
    }

    /**
     * 设置编码
     */
    public HttpUtils charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * 参数是否url编码
     */
    public HttpUtils encodeParam(boolean encodeParam) {
        this.encodeParam = encodeParam;
        return this;
    }

    /**
     * 设置请求体
     */
    public HttpUtils body(byte[] body) {
        this.body = body;
        return this;
    }

    /**
     * 读超时，默认10秒
     */
    public HttpUtils readTimeout(Duration duration) {
        this.readTimeout = (int) duration.toMillis();
        return this;
    }

    /**
     * 连接超时，默认30秒
     */
    public HttpUtils connectTimeout(Duration duration) {
        this.connectTimeout = (int) duration.toMillis();
        return this;
    }

    /**
     * 是否缓存，默认false
     */
    public HttpUtils useCaches(boolean useCaches) {
        this.useCaches = useCaches;
        return this;
    }

    /**
     * 是否自动重定向，默认false
     */
    public HttpUtils followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    /**
     * 设置代理
     */
    public HttpUtils proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回值
     */
    @Data
    public static class HttpResponse {
        private final byte[] bytes;
        private final Map<String, String> headers;

        public HttpResponse(byte[] bytes, Map<String, List<String>> headers) {
            this.bytes = bytes;
            this.headers = headers.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            entry -> entry.getValue() != null && !entry.getValue().isEmpty() ? entry.getValue().get(0) : ""));
        }

        /**
         * 返回字符串
         */
        public String getString() {
            return new String(bytes);
        }

        /**
         * 返回javaBean，格式必须为json
         */
        public <T> T toBean(Class<T> beanType) {
            return JsonUtils.fromJson(new String(bytes), beanType);
        }

        /**
         * 返回List，格式必须为json
         */
        public <T> List<T> toList(Class<T> itemType) {
            return JsonUtils.listFromJson(new String(bytes), itemType);
        }
    }
}
