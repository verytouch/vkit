package top.verytouch.vkit.chat.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * message
 *
 * @author verytouch
 * @since 2023/7/3 11:06
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    /**
     * The role of the messages author. One of system, user, assistant, or function
     */
    private String role;

    /**
     * content is required for all messages except assistant messages with function calls.
     */
    private String content;
}
