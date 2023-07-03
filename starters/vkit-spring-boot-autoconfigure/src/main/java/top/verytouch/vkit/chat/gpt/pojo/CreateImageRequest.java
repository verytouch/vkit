package top.verytouch.vkit.chat.gpt.pojo;

import lombok.Data;

/**
 * 生成图片参数
 *
 * @author verytouch
 * @since 2023/7/3 19:04
 *
 */
@Data
public class CreateImageRequest {

    /**
     * A text description of the desired image(s). The maximum length is 1000 characters.
     */
    private String prompt;

    /**
     * The number of images to generate. Must be between 1 and 10.
     * Defaults to 1
     */
    private Integer n;

    /**
     * The size of the generated images. Must be one of 256x256, 512x512, or 1024x1024.
     * Defaults to 1024x1024
     */
    private String size;

    /**
     * The format in which the generated images are returned. Must be one of url or b64_json.
     * Defaults to url
     */
    private String response_format;

    /**
     * A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     */
    private String user;
}
