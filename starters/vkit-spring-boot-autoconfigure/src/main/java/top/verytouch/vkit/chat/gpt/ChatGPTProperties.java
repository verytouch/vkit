package top.verytouch.vkit.chat.gpt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * chat配置类
 *
 * @author verytouch
 * @since 2023/7/3 10:45
 */
@Data
@ConfigurationProperties(prefix = "vkit.chat.gpt")
public class ChatGPTProperties {

    /**
     * 请求地址，默认https://api.openai.com
     */
    private String host = "https://api.openai.com";

    /**
     * apikey
     */
    private String apikey;

    /**
     * token计算方式，默认 cl100k_base {@link  com.knuddels.jtokkit.api.EncodingType}
     */
    private String tokenEncodingType = "cl100k_base";

    public static final String MODEL_TURBO = "gpt-3.5-turbo";
    public static final String MODEL_DAVINCI3 = "text-davinci-003";
}
