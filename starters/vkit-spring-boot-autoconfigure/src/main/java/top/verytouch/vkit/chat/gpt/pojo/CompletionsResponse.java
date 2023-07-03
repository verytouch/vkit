package top.verytouch.vkit.chat.gpt.pojo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * completions-response
 *
 * @author verytouch
 * @since 2023/7/3 11:19
 */
@Data
public class CompletionsResponse {

    private String id;

    private String object;

    private long created;

    private String model;

    private List<Choice> choices;

    private Usage usage;

    /**
     * get content from first choice.
     * return null if failed
     */
    public String getFirstMessage() {
        if (CollectionUtils.isEmpty(choices)) {
            return null;
        }
        Message message = choices.get(0).getMessage();
        if (message == null) {
            return null;
        }
        return message.getContent();
    }
}
