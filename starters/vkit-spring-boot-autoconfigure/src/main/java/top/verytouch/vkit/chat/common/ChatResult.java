package top.verytouch.vkit.chat.common;

import lombok.Data;

/**
 * 返回结果
 *
 * @author zhouwei
 * @since 2023/12/22
 */
@Data
public class ChatResult<T> {

    private T data;

    private ChatUsage usage;

    public static <T> ChatResult<T> of(T data, ChatUsage usage) {
        ChatResult<T> result = new ChatResult<>();
        result.setData(data);
        result.setUsage(usage);
        return result;
    }

}
