package top.verytouch.vkit.chat.yiyan;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.verytouch.vkit.chat.common.*;
import top.verytouch.vkit.common.base.Assert;
import top.verytouch.vkit.common.util.HttpUtils;
import top.verytouch.vkit.common.util.JsonUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文心一言接口
 *
 * @author zhouwei
 * @since 2023/12/22
 */
@RequiredArgsConstructor
@Slf4j
public class YiYanService implements ChatService {

    private final YiYanProperties yiYanProperties;
    private final Token token = new Token();

    @Override
    public ChatResult<String> chat(List<ChatMessage> messages) {
        getToken();
        JsonUtils.JsonObject<String, List<ChatMessage>> data = JsonUtils.newObject("messages", messages);
        String response = new HttpUtils(yiYanProperties.getHost())
                .path("/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro")
                .addParam("access_token", token.getAccessToken())
                .body(data.toJson().getBytes(StandardCharsets.UTF_8))
                .post();
        log.info("发起会话完成: request={}, response={}", data, response);
        JsonNode jsonNode = JsonUtils.toTree(response);
        Assert.nonNull(jsonNode, "发起会话失败");
        String result = jsonNode.get("result").asText();
        JsonNode usage = jsonNode.get("usage");
        ChatUsage chatUsage = ChatUsage.of(usage.get("prompt_tokens").asInt(), usage.get("completion_tokens").asInt());
        return ChatResult.of(result, chatUsage);
    }

    @Override
    public List<String> createImage(String prompt, String size, int n) {
        getToken();
        return null;
    }

    public void getToken() {
        if (token.valid()) {
            return;
        }
        String response = new HttpUtils(yiYanProperties.getHost())
                .path("/oauth/2.0/token")
                .addParam("grant_type", "client_credentials")
                .addParam("client_id", yiYanProperties.getClientId())
                .addParam("client_secret", yiYanProperties.getSecretKey())
                .post();
        log.info("获取token完成: {}", response);
        JsonNode root = JsonUtils.toTree(response);
        Assert.nonNull(root, "获取token失败");
        String accessToken = root.get("access_token").asText();
        Assert.nonNull(accessToken, "获取token失败");
        long expiresIn = root.get("expires_in").asLong();
        token.setAccessToken(accessToken);
        token.setExpiresAt(System.currentTimeMillis() + expiresIn);
    }

}
