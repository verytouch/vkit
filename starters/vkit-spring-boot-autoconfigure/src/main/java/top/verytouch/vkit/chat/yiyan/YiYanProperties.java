package top.verytouch.vkit.chat.yiyan;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "vkit.chat.yiyan")
public class YiYanProperties {

    /**
     * 百度AI开放平台的host
     */
    private String host = "https://aip.baidubce.com";

    /**
     * 百度AI开放平台的clientId
     */
    private String clientId;

    /**
     * 百度AI开放平台的secretKey
     */
    private String secretKey;
}
