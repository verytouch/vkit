package top.verytouch.vkit.pay.ali;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 支付宝配置类
 *
 * @author verytouch
 * @since 2021/4/30 10:32
 */
@ConfigurationProperties("vkit.pay.ali")
@Data
public class AlipayProperties {

    /**
     * 接口服务地址
     */
    private String gatewayUrl = "https://openapi.alipay.com/gateway.do";

    /**
     * 授权地址
     */
    private String authorizeUrl = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm";

    /**
     * 授权回调地址
     */
    private String redirectUri;

    /**
     * appId
     */
    private String appId;

    /**
     * 应用私钥，自己生成
     * 用来给请求签名，公钥需要上传给支付宝
     */
    private String privateKey;

    /**
     * 支付宝公钥，支付宝生成
     * 用来验签消息是否来自支付宝
     */
    private String alipayPublicKey;

    /**
     * 消息格式
     */
    private String format = "json";

    /**
     * 消息编码
     */
    private String charset = "utf-8";

    /**
     * 加密算法
     */
    private String signType = "RSA2";

}
