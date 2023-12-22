package top.verytouch.vkit.chat.common;

import lombok.Data;

/**
 * token
 *
 * @author zhouwei
 * @since 2023/12/22
 */
@Data
public class Token {

    /**
     * access token
     */
    private String accessToken;

    /**
     * 过期时间
     */
    private long expiresAt;

    public boolean valid() {
        return accessToken != null && expiresAt > System.currentTimeMillis() - 3000;
    }
}
