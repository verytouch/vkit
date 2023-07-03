package top.verytouch.vkit.chat.gpt.pojo;

import lombok.Data;

/**
 * message
 *
 * @author verytouch
 * @since 2023/7/3 11:06
 *
 */
@Data
public class Message {

    /**
     * The role of the messages author. One of system, user, assistant, or function
     */
    private String role;

    /**
     * content is required for all messages except assistant messages with function calls.
     */
    private String content;
}
