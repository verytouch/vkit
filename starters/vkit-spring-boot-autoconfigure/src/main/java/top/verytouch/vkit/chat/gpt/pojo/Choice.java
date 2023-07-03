package top.verytouch.vkit.chat.gpt.pojo;

import lombok.Data;

/**
 * choice
 *
 * @author verytouch
 * @since 2023/7/3 11:18
 *
 */
@Data
public class Choice {

    private long index;

    private Message message;

    private String finish_reason;
}
