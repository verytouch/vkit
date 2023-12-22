package top.verytouch.vkit.chat.gpt.pojo;

import lombok.Data;
import top.verytouch.vkit.chat.common.ChatUsage;

import java.util.List;

/**
 * 生成图片结果
 *
 * @author verytouch
 * @since 2023/7/3 19:08
 *
 */
@Data
public class CreateImageResponse {

    private long created;

    private List<Data> data;

    private ChatUsage usage;


    @lombok.Data
    public static class Data {

        /**
         * 图片url
         */
        private String url;
    }
}
