package top.verytouch.vkit.chat.gpt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.verytouch.vkit.chat.gpt.pojo.*;
import top.verytouch.vkit.common.util.HttpUtils;
import top.verytouch.vkit.common.util.JsonUtils;
import top.verytouch.vkit.common.util.MapUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * chat-gpt接口
 *
 * @author verytouch
 * @since 2023/7/3 10:49
 */
@RequiredArgsConstructor
@Slf4j
public class ChatGPTService {

    private final ChatGPTProperties properties;

    /**
     * 聊天 gpt-3.5-turbo
     *
     * @param messages 如果需要上下文，需要把之前的会话信息放入messages数组
     */
    public ChatCompletionsResponse chatCompletions(List<Message> messages) {
        Map<String, Object> body = MapUtils.Builder.hashMap(String.class, Object.class)
                .put("model", ChatGPTProperties.MODEL_TURBO)
                .put("messages", messages)
                .build();
        String responseString = post(JsonUtils.toJson(body), ChatGPTApiEnum.CHAT_COMPLETIONS);
        return JsonUtils.fromJson(responseString, ChatCompletionsResponse.class);
    }

    /**
     * 聊天 text-davinci-003
     */
    public CompletionsResponse completions(CompletionsRequest request) {
        request.setModel(ChatGPTProperties.MODEL_DAVINCI3);
        String responseString = post(JsonUtils.toJson(request), ChatGPTApiEnum.COMPLETIONS);
        return JsonUtils.fromJson(responseString, CompletionsResponse.class);
    }

    /**
     * 生成图片
     */
    public CreateImageResponse createImage(CreateImageRequest request) {
        String responseString = post(JsonUtils.toJson(request), ChatGPTApiEnum.CREATE_IMAGE);
        return JsonUtils.fromJson(responseString, CreateImageResponse.class);
    }

    public String post(String body, ChatGPTApiEnum apiEnum) {
        log.info("请求chat-gpt开始, api={}, params={}", apiEnum.getPath(), body);
        String responseString;
        try {
            responseString = new HttpUtils(properties.getHost() + apiEnum.getPath())
                    .addHeader("Authorization", "Bearer " + properties.getApikey())
                    .addHeader("Content-Type", "application/json")
                    .body(body.getBytes(StandardCharsets.UTF_8))
                    .method("POST")
                    .connectTimeout(Duration.ofSeconds(30))
                    .readTimeout(Duration.ofSeconds(120))
                    .request()
                    .getString();
        } catch (Exception e) {
            log.error("请求chat-gpt失败", e);
            throw new RuntimeException(e);
        }
        log.info("请求chat-gpt完成, api={}, response={}", apiEnum.getPath(), responseString);
        return responseString;
    }

}
