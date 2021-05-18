package top.verytouch.vkit.rabc.oauth2;

import top.verytouch.vkit.common.util.CryptUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * AES加密传输中的密码
 *
 * @author verytouch
 * @since 2021/5/14 17:33
 */
public class AesPasswordEncoder implements ParameterPasswordEncoder {

    private final SecretKey key;
    private final String algorithm = "AES/ECB/PKCS5Padding";
    private final byte[] iv;

    public AesPasswordEncoder(String keyStr, byte[] iv) {
        this.key = CryptUtils.KeyGen.secretKey("AES", keyStr.getBytes(StandardCharsets.UTF_8));
        this.iv = iv;
    }

    @Override
    public String encode(String password) {
        try {
            return CryptUtils.toHex(CryptUtils.encrypt(password, algorithm, key, iv, -1));
        } catch (Exception e) {
            throw new OauthException("加密失败");
        }
    }

    @Override
    public String decode(String encodedPassword) {
        try {
            return CryptUtils.decrypt(CryptUtils.fromHex(encodedPassword), algorithm, key, iv, -1);
        } catch (Exception e) {
            throw new OauthException("密码格式错误");
        }
    }
}
