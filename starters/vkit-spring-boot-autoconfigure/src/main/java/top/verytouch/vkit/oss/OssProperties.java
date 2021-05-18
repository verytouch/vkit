package top.verytouch.vkit.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

/**
 * OSS配置类
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@ConfigurationProperties(prefix = "vkit.oss")
@Data
@Validated
public class OssProperties {

    /**
     * endpoint
     */
    private String endpoint;

    /**
     * accessId
     */
    private String accessId;

    /**
     * accessKey
     */
    private String accessKey;

    /**
     * 上传链接的有效时间
     */
    private int uploadUrlExpireMinutes = 60;

    /**
     * 是否校验bucket合法性
     */
    private boolean validateBucket = true;

    /**
     * 合法的bucket，若不是其中之一则抛出异常
     */
    private Set<String> validBuckets;
}
