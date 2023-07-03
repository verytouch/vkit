package top.verytouch.vkit.chat.gpt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.verytouch.vkit.chat.gpt.pojo.CompletionsResponse;
import top.verytouch.vkit.chat.gpt.pojo.Message;
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

    public CompletionsResponse completions(String model, List<Message> messages) {
        Map<String, Object> body = MapUtils.Builder.hashMap(String.class, Object.class)
                .put("model", model)
                .put("messages", messages)
                .build();
        String bodyString = JsonUtils.toJson(body);
        log.info("请求chat-gpt开始, api={}, params={}", ChatGPTApiEnum.COMPLETIONS.getPath(), bodyString);
        String responseString;
        try {
            responseString = new HttpUtils(properties.getHost() + ChatGPTApiEnum.COMPLETIONS.getPath())
                    .addHeader("Authorization", "Bearer " + properties.getApikey())
                    .addHeader("Content-Type", "application/json")
                    .body(bodyString.getBytes(StandardCharsets.UTF_8))
                    .method("POST")
                    .connectTimeout(Duration.ofSeconds(30))
                    .readTimeout(Duration.ofSeconds(120))
                    .request()
                    .getString();
        } catch (Exception e) {
            log.error("请求chat-gpt失败", e);
            throw new RuntimeException(e);
        }
        log.info("请求chat-gpt完成, api={}, response={}", ChatGPTApiEnum.COMPLETIONS.getPath(), responseString);
        return JsonUtils.fromJson(responseString, CompletionsResponse.class);
    }

    public static void main(String[] args) {
        String res = "{\n" +
                "  \"id\": \"chatcmpl-7Y5QDV6VgLHZFRYRf0YtOQD7sXdpq\",\n" +
                "  \"object\": \"chat.completion\",\n" +
                "  \"created\": 1688358349,\n" +
                "  \"model\": \"gpt-3.5-turbo-0613\",\n" +
                "  \"choices\": [\n" +
                "    {\n" +
                "      \"index\": 0,\n" +
                "      \"message\": {\n" +
                "        \"role\": \"assistant\",\n" +
                "        \"content\": \"b树是一种自平衡的搜索树，广泛应用于数据库和文件系统中以支持对大量数据的快速查找。它的原理如下：\\n\\n1. b树是一种多路搜索树，其中每个节点可以包含多个子节点，通常称为分支因子。每个节点的子节点数目大于等于2，并且小于等于分支因子。\\n\\n2. b树的每个节点包含一个有序的键值列表。这些键值用于在树中进行搜索和排序。\\n\\n3. b树的根节点存储在内存中，而其他节点则存储在磁盘上。因此，b树的高度相对较小，可以减少磁盘访问次数，提高查找性能。\\n\\n4. b树的节点分为内部节点和叶节点。内部节点存储键值和指向其子节点的指针，叶节点则存储键值和对应的数据。\\n\\n5. 内部节点的键值用于在树中进行搜索，确定要访问的子节点。通过比较键值，可以确定搜索路径，从而快速定位到需要查找的节点。\\n\\n6. b树的插入和删除操作都会改变树的结构。插入操作会按照键值的顺序将新键值插入到叶节点中，同时可能导致节点的分裂。而删除操作会根据不同情况，可能导致节点的合并或者重新分配键值。\\n\\n7. b树的性能取决于分支因子的选择。较大的分支因子可以减少树的高度，提高查找性能，但也会增加插入和删除操作的复杂度。\\n\\n总的来说，b树是一种高效的数据结构，适用于存储大量数据并需要频繁进行查找、插入和删除的场景。通过合理选择分支因子，可以平衡树的高度和操作的复杂度，从而提高系统的性能。\"\n" +
                "      },\n" +
                "      \"finish_reason\": \"stop\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"usage\": {\n" +
                "    \"prompt_tokens\": 14,\n" +
                "    \"completion_tokens\": 547,\n" +
                "    \"total_tokens\": 561\n" +
                "  }\n" +
                "}";
        CompletionsResponse completionsResponse = JsonUtils.fromJson(res, CompletionsResponse.class);
        System.out.println(completionsResponse.getFirstMessage());
    }
}
