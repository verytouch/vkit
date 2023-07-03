package top.verytouch.vkit.chat.gpt.pojo;

import lombok.Data;

/**
 * usage
 *
 * @author verytouch
 * @since 2023/7/3 11:16
 *
 */
@Data
public class Usage {

    private long prompt_tokens;

    private long completion_tokens;

    private long total_tokens;
}
