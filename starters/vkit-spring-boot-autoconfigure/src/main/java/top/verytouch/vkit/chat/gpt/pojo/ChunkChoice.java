package top.verytouch.vkit.chat.gpt.pojo;

import lombok.Data;
import top.verytouch.vkit.chat.common.ChatMessage;

/**
 * choice for chatCompletions chunk
 *
 * @author verytouch
 * @since 2023/9/6 11:09
 *
 */
@Data
public class ChunkChoice {

    private Integer index;
    private ChatMessage delta;
    private String finish_reason;

}
