package top.verytouch.vkit.chat.gpt.pojo;

import lombok.Data;

/**
 * usage
 *
 * @author verytouch
 * @since 2023/7/3 11:16
 */
@Data
public class Usage {

    private long prompt_tokens;

    private long completion_tokens;

    private long total_tokens;

    public static Usage of(long prompt, long completion) {
        Usage usage = new Usage();
        usage.setPrompt_tokens(prompt);
        usage.setCompletion_tokens(prompt);
        usage.setTotal_tokens(prompt + completion);
        return usage;
    }

    public Usage add(Usage another) {
        if (another == null) {
            return this;
        }
        this.prompt_tokens += another.getPrompt_tokens();
        this.completion_tokens += another.getCompletion_tokens();
        this.total_tokens += another.getTotal_tokens();
        return this;
    }
}
