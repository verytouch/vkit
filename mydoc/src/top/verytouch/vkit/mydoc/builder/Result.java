package top.verytouch.vkit.mydoc.builder;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 生成文档结果
 *
 * @author verytouch
 * @since 2024-09
 */
@Data
@AllArgsConstructor
public class Result {

    private boolean success;
    private String msg;

    public static Result ok(String msg) {
        return new Result(true, msg);
    }

    public static Result failed(String msg) {
        return new Result(false, msg);
    }

}
