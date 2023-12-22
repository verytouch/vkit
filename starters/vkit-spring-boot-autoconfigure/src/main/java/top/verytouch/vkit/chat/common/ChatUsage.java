package top.verytouch.vkit.chat.common;

import lombok.Data;

/**
 * usage
 *
 * @author verytouch
 * @since 2023/7/3 11:16
 */
@Data
public class ChatUsage {

    private long prompt_tokens;

    private long completion_tokens;

    private long total_tokens;

    public static ChatUsage of(long prompt, long completion) {
        ChatUsage usage = new ChatUsage();
        usage.setPrompt_tokens(prompt);
        usage.setCompletion_tokens(completion);
        usage.setTotal_tokens(prompt + completion);
        return usage;
    }

    public ChatUsage add(ChatUsage another) {
        if (another == null) {
            return this;
        }
        this.prompt_tokens += another.getPrompt_tokens();
        this.completion_tokens += another.getCompletion_tokens();
        this.total_tokens += another.getTotal_tokens();
        return this;
    }
}
