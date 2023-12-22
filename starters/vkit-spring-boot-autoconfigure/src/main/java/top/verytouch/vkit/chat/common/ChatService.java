package top.verytouch.vkit.chat.common;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天服务
 *
 * @author zhouwei
 * @since 2023/12/22
 */
public interface ChatService {

    /**
     * 对话
     */
    ChatResult<String> chat(List<ChatMessage> messages);

    /**
     * 生成图片
     */
    List<String> createImage(String prompt, String size, int n);

    default ChatResult<String> chat(String prompt) {
        List<ChatMessage> messages = new ArrayList<>(1);
        messages.add(new ChatMessage("user", prompt));
        return chat(messages);
    }

    default String createImage(String prompt) {
        List<String> image = createImage(prompt, "1024*768", 1);
        return CollectionUtils.isEmpty(image) ? null : image.get(0);
    }

}
