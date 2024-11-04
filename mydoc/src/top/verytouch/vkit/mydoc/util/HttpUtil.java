package top.verytouch.vkit.mydoc.util;

import lombok.Data;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * http请求工具类
 *
 * @author verytouch
 * @since 2020/9/17 16:32
 */
@SuppressWarnings("unused")
public class HttpUtil {

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

    public HttpUtil(String url) {
        this.url = url;
    }

    /**
     * 发送get请求
     *
     * @param url 请求地址
     * @return 接口返回的字符串
     */
    public static String get(String url) {
        return new HttpUtil(url).get().asString();
    }

    /**
     * 发送get请求
     *
     * @param url    请求地址，不带参数
     * @param params 请求参数，拼接在url后面
     * @return 接口返回的字符串
     */
    public static String get(String url, Map<String, Object> params) {
        return new HttpUtil(url).params(params).get().asString();
    }

    /**
     * 发送post请求
     *
     * @param url    请求地址，不带参数
     * @param params 请求参数，拼接在url后面
     * @return 接口返回的字符串
     */
    public static String post(String url, Map<String, Object> params) {
        return new HttpUtil(url).params(params).post().asString();
    }

    /**
     * 发送post请求
     *
     * @param url  请求地址
     * @param body 参数，内部转换为json字符串，contentType=application/json
     * @return 接口返回的字符串
     */
    public static String postJson(String url, Object body) {
        return new HttpUtil(url)
                .addHeader("Content-Type", APPLICATION_JSON)
                .body(JsonUtil.toJson(body).getBytes(StandardCharsets.UTF_8))
                .post()
                .asString();
    }

    /**
     * 下载
     */
    public static byte[] download(String url) {
        return new HttpUtil(url).caughtRequest().getBody();
    }

    /**
     * 执行请求，失败抛出RuntimeException
     */
    public HttpResponse caughtRequest(Integer okResponseCode) {
        try {
            HttpResponse response = request();
            if (okResponseCode != null && !okResponseCode.equals(response.getCode())) {
                throw new RuntimeException(response.getMsg());
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Http请求失败：" + e.getMessage());
        }
    }

    /**
     * 执行请求，失败抛出RuntimeException
     */
    public HttpResponse caughtRequest() {
        return caughtRequest(200);
    }

    /**
     * 执行请求
     */
    public HttpResponse request() throws Exception {
        HttpURLConnection connection = null;
        try {
            connection = getConnection(null);
            return new HttpResponse(connection);
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
        URL u = new URI(url).toURL();
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
        return connection;
    }

    public HttpResponse get() {
        return method("GET").caughtRequest();
    }

    public HttpResponse post() {
        return method("POST").caughtRequest();
    }

    /**
     * 设置请求方法
     */
    public HttpUtil method(String method) {
        this.method = method.toUpperCase();
        return this;
    }

    /**
     * 设置header
     */
    public HttpUtil headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * 添加一个header
     */
    public HttpUtil addHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(key, value);
        return this;
    }

    /**
     * 设置参数
     */
    public HttpUtil params(Map<String, Object> params) {
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
    public HttpUtil addParam(String key, String value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(key, encodeParam ? urlEncode(value) : value);
        return this;
    }

    /**
     * 设置编码
     */
    public HttpUtil charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * 参数是否url编码
     */
    public HttpUtil encodeParam(boolean encodeParam) {
        this.encodeParam = encodeParam;
        return this;
    }

    /**
     * 设置请求体
     */
    public HttpUtil body(byte[] body) {
        this.body = body;
        return this;
    }

    /**
     * 读超时，默认10秒
     */
    public HttpUtil readTimeout(Duration duration) {
        this.readTimeout = (int) duration.toMillis();
        return this;
    }

    /**
     * 连接超时，默认30秒
     */
    public HttpUtil connectTimeout(Duration duration) {
        this.connectTimeout = (int) duration.toMillis();
        return this;
    }

    /**
     * 是否缓存，默认false
     */
    public HttpUtil useCaches(boolean useCaches) {
        this.useCaches = useCaches;
        return this;
    }

    /**
     * 是否自动重定向，默认false
     */
    public HttpUtil followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    /**
     * 设置代理
     */
    public HttpUtil proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, charset.displayName());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回值
     */
    @Data
    public static class HttpResponse {

        private final int code;
        private final String msg;
        private final Map<String, String> headers;
        private final byte[] body;

        public HttpResponse(HttpURLConnection connection) throws IOException {
            this.code = connection.getResponseCode();
            this.msg = connection.getResponseMessage();
            this.headers = connection.getHeaderFields().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                    entry -> entry.getValue() != null && !entry.getValue().isEmpty() ? entry.getValue().get(0) : ""));
            this.body = Objects.equals(200, this.code) ? IOUtils.toByteArray(connection.getInputStream()) : null;
        }

        /**
         * 返回字符串
         */
        public String asString() {
            return new String(body);
        }

        /**
         * 返回javaBean，格式必须为json
         */
        public <T> T asBean(Class<T> beanType) {
            return JsonUtil.toObject(new String(body), beanType);
        }

    }
}
