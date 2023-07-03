package top.verytouch.vkit.chat.gpt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * chat配置类
 *
 * @author verytouch
 * @since 2023/7/3 10:45
 *
 */
@Data
@Validated
@ConfigurationProperties(prefix = "vkit.chat.gpt")
public class ChatGPTProperties {

    /**
     * 是否启用，默认false
     */
    private Boolean enabled = false;

    /**
     * 请求地址，默认https://api.openai.com
     */
    private String host = "https://api.openai.com";

    /**
     * apikey
     */
    @NotEmpty(message = "apikey不能为空")
    private String apikey;

    public static final String MODEL_TURBO = "gpt-3.5-turbo";
    public static final String MODEL_DAVINCI3 = "text-davinci-003";
}
