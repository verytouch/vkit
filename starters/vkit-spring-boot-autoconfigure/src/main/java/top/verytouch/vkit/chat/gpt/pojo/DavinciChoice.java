package top.verytouch.vkit.chat.gpt.pojo;

import lombok.Data;

/**
 * choice for text-davinci-003
 *
 * @author verytouch
 * @since 2023/7/3 19:15
 *
 */
@Data
public class DavinciChoice {

    private String text;

    private Integer index;

    private String logprobs;

    private String finish_reason;
}
