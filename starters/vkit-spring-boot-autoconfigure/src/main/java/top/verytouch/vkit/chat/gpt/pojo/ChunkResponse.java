package top.verytouch.vkit.chat.gpt.pojo;

import lombok.Data;

import java.util.List;

/**
 * response for chatCompletions chunk
 *
 * @author verytouch
 * @since 2023/9/6 11:12
 *
 */
@Data
public class ChunkResponse {

    private String id;

    private String object;

    private long created;

    private String model;

    private List<ChunkChoice> choices;

    private Usage usage;
}
