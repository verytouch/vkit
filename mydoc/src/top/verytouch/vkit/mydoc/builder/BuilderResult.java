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
public class BuilderResult {

    private boolean success;
    private String msg;

    public static BuilderResult ok(String msg) {
        return new BuilderResult(true, msg);
    }

    public static BuilderResult failed(String msg) {
        return new BuilderResult(false, msg);
    }

}
