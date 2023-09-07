package top.verytouch.vkit.chat.gpt;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingType;
import lombok.extern.slf4j.Slf4j;
import top.verytouch.vkit.chat.gpt.pojo.*;
import top.verytouch.vkit.common.util.HttpUtils;
import top.verytouch.vkit.common.util.JsonUtils;
import top.verytouch.vkit.common.util.MapUtils;
import top.verytouch.vkit.common.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * chat-gpt接口
 *
 * @author verytouch
 * @since 2023/7/3 10:49
 */
@Slf4j
public class ChatGPTService {

    private final ChatGPTProperties properties;
    private final Encoding encoding;

    public ChatGPTService(ChatGPTProperties properties) {
        this.properties = properties;
        this.encoding = Encodings.newLazyEncodingRegistry().getEncoding(EncodingType.fromName(properties.getTokenEncodingType()).orElse(EncodingType.CL100K_BASE));
    }

    /**
     * 计算token数量
     */
    public int tokens(String content) {
        return encoding.countTokens(content);
    }

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
     * 聊天 gpt-3.5-turbo sse方式
     *
     * @param messages 如果需要上下文，需要把之前的会话信息放入messages数组
     * @param consumer 每次返回的数据
     * @param finisher ChunkResponse为所有请求拼接好的，usage依照拼接好的算
     */
    public void chatCompletions(List<Message> messages, Consumer<String> consumer, BiConsumer<ChunkResponse, Usage> finisher) {
        Map<String, Object> body = MapUtils.Builder.hashMap(String.class, Object.class)
                .put("model", ChatGPTProperties.MODEL_TURBO)
                .put("messages", messages)
                .put("stream", true)
                .build();
        Consumer<List<String>> myFinisher = list -> {
            ChunkResponse response = list.stream().map(this::parseChunk)
                    .filter(Objects::nonNull)
                    .reduce(ChunkResponse::mergeMessage)
                    .orElse(null);
            int promptTokens = messages.stream()
                    .map(Message::getContent)
                    .map(this::tokens)
                    .reduce(0, Math::addExact);
            int completionTokens = 0;
            try {
                completionTokens = response == null ? 0 : this.tokens(response.getChoices().get(0).getDelta().getContent());
            } catch (Exception e) {
                log.error("计算completionTokens异常", e);
            }
            Usage usage = Usage.of(promptTokens, completionTokens);
            finisher.accept(response, usage);
            log.info("chat-gpt-chunk组装后的数据 = {}, usage = {}", response, usage);
        };
        post(JsonUtils.toJson(body), ChatGPTApiEnum.CHAT_COMPLETIONS, consumer, myFinisher);
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
        CreateImageResponse imageResponse = JsonUtils.fromJson(responseString, CreateImageResponse.class);
        Integer completionTokens = imageResponse.getData().stream()
                .map(CreateImageResponse.Data::getUrl)
                .map(this::tokens)
                .reduce(0, Math::addExact);
        imageResponse.setUsage(Usage.of(this.tokens(request.getPrompt()), completionTokens));
        return imageResponse;
    }

    /**
     * chunk字符串转bean
     *
     * @param json json or sse-prefixed json
     */
    public ChunkResponse parseChunk(String json) {
        String prefix = "data: ";
        if (StringUtils.isBlank(json)) {
            return null;
        }
        if (json.startsWith(prefix)) {
            json = json.substring(prefix.length());
        }
        json = json.trim();
        if (!json.startsWith("{") && !json.endsWith("}")) {
            return null;
        }
        return JsonUtils.fromJson(json, ChunkResponse.class);
    }

    public String post(String body, ChatGPTApiEnum apiEnum) {
        String responseString;
        try {
            responseString = prepare(body, apiEnum).request().getString();
        } catch (Exception e) {
            log.error("请求chat-gpt失败", e);
            throw new RuntimeException(e);
        }
        log.info("请求chat-gpt完成, api={}, response={}", apiEnum.getPath(), responseString);
        return responseString;
    }

    public void post(String body, ChatGPTApiEnum apiEnum, Consumer<String> consumer, Consumer<List<String>> finisher) {
        List<String> msgList = new LinkedList<>();
        try {
            prepare(body, apiEnum).sseRequest(msg -> {
                log.debug("请求chat-gpt-chunk返回, api={}, chunk={}", apiEnum.getPath(), msg);
                consumer.accept(msg);
                msgList.add(msg);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            log.info("请求chat-gpt-chunk完成, api={}", apiEnum.getPath());
            finisher.accept(msgList);
        }
    }

    private HttpUtils prepare(String body, ChatGPTApiEnum apiEnum) {
        log.info("请求chat-gpt开始, api={}, params={}", apiEnum.getPath(), body);
        return new HttpUtils(properties.getHost() + apiEnum.getPath())
                .addHeader("Authorization", "Bearer " + properties.getApikey())
                .addHeader("Content-Type", "application/json")
                .body(body.getBytes(StandardCharsets.UTF_8))
                .method("POST")
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(120));
    }

}
