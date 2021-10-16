package top.verytouch.vkit.samples.activity.modler;

import lombok.Data;

import java.util.List;

/**
 * 流程定义
 *
 * @author zhouwei
 * @date 2021/10/16 13:33
 */
@Data
public class Flow {
    /**
     * 流程定义key
     */
    private String key;
    /**
     * 流程名称
     */
    private String name;
    /**
     * 流程节点信息
     */
    private List<Node> nodeList;
    /**
     * 流程流向信息
     */
    private List<Sequence> sequenceList;
}
