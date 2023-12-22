package top.verytouch.vkit.chat.gpt.pojo;

import lombok.Data;
import top.verytouch.vkit.chat.common.ChatMessage;

/**
 * choice for gpt-3.5-turbo
 *
 * @author verytouch
 * @since 2023/7/3 11:18
 *
 */
@Data
public class TurboChoice {

    private long index;

    private ChatMessage message;

    private String finish_reason;
}
