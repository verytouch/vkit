package com.verytouch.vkit.common.util;

import com.verytouch.vkit.common.base.Assert;
import lombok.Data;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
        this.url = url;
    }

    public static String get(String url) throws Exception {
        return new HttpUtils(url).get();
    }

    public static String get(String url, Map<String, Object> params) throws Exception {
        return new HttpUtils(url).params(params).get();
    }

    public static String post(String url, Map<String, Object> params) throws Exception {
        return new HttpUtils(url).params(params).post();
    }

    public static String postJson(String url, String json) throws Exception {
        return new HttpUtils(url)
                .addHeader("Content-Type", APPLICATION_JSON)
                .body(json)
                .post();
    }

    public String request() throws Exception {
        Assert.nonNull(url, "url cannot be null or empty");
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
            try (InputStream in = connection.getInputStream()) {
                return IOUtils.toString(in, charset);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String get() throws Exception {
        method("GET");
        return request();
    }

    public String post() throws Exception {
        method("POST");
        return request();
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
}
