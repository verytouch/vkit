package top.verytouch.vkit.samples.activity.modler;

import lombok.Data;

/**
 * 流程连线
 *
 * @author zhouwei
 * @date 2021/10/16 13:34
 */
@Data
public class Sequence {
    /**
     * 连线ID
     */
    private String id;
    /**
     * 连线名称
     */
    private String name;
    /**
     * 源节点ID
     */
    private String sourceId;
    /**
     * 目标节点ID
     */
    private String targetId;
    /**
     * 流程条件表达式
     */
    private String condition;
}
