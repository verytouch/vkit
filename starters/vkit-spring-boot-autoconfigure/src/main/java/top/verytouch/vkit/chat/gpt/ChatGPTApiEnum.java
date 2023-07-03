package top.verytouch.vkit.chat.gpt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * chat-gpt官方接口
 *
 * @author verytouch
 * @since 2023/7/3 10:52
 *
 */
@RequiredArgsConstructor
@Getter
public enum ChatGPTApiEnum {

    COMPLETIONS("/v1/chat/completions");

    private final String path;
}
