package top.verytouch.vkit.chat.gpt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * chat-gpt官方接口
 * <a href="https://platform.openai.com/docs/api-reference/introduction">API文档</a>
 *
 * @author verytouch
 * @since 2023/7/3 10:52
 *
 */
@RequiredArgsConstructor
@Getter
public enum ChatGPTApiEnum {

    CHAT_COMPLETIONS("/v1/chat/completions"),
    COMPLETIONS("/v1/completions"),
    EDITS("/v1/edits"),
    CREATE_IMAGE("/v1/images/generations");

    private final String path;
}
