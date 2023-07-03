package top.verytouch.vkit.chat.gpt.pojo;

import lombok.Data;

@Data
public class CompletionsRequest {

    /**
     * ID of the model to use
     */
    private String model;

    /**
     * The prompt(s) to generate completions for, encoded as a string, array of strings, array of tokens, or array of token arrays.
     */
    private Object prompt;

    /**
     * The maximum number of tokens to generate in the completion.
     * Defaults to 16
     */
    private Integer max_tokens;

    /**
     * What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random,
     * while lower values like 0.2 will make it more focused and deterministic.
     * We generally recommend altering this or top_p but not both.
     * Defaults to 1
     */
    private Double temperature;

    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of the tokens with top_p probability mass.
     * So 0.1 means only the tokens comprising the top 10% probability mass are considered.
     * We generally recommend altering this or temperature but not both.
     * Defaults to 1
     */
    private Integer top_p;

    /**
     * How many completions to generate for each prompt.
     * Defaults to 1
     */
    private Integer n;

    /**
     * Whether to stream back partial progress. If set, tokens will be sent as data-only server-sent events as they become available,
     * with the stream terminated by a data: [DONE] message.
     */
    private Boolean stream;

    /**
     * Include the log probabilities on the logprobs most likely tokens, as well the chosen tokens. For example, if logprobs is 5,
     * the API will return a list of the 5 most likely tokens. The API will always return the logprob of the sampled token,
     * so there may be up to logprobs+1 elements in the response.
     */
    private Object logprobs;

    /**
     * Up to 4 sequences where the API will stop generating further tokens. The returned text will not contain the stop sequence.
     */
    private String stop;
}
