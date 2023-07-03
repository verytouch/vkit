package top.verytouch.vkit.chat.gpt.pojo;

import lombok.Data;

import java.util.List;

/**
 * completions-response
 *
 * @author verytouch
 * @since 2023/7/3 19:30
 */
@Data
public class CompletionsResponse {

    private String id;

    private String object;

    private long created;

    private String model;

    private List<DavinciChoice> choices;

    private Usage usage;
}
