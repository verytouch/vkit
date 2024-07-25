package top.verytouch.vkit.samples.activity.modler;

import lombok.Data;

import java.util.List;

/**
 * 流程节点
 *
 * @author zhouwei
 * @date 2021/10/16 13:33
 */
@Data
public class Node {
    /**
     * 节点ID
     */
    private String id;
    /**
     * 节点名称
     */
    private String name;
    /**
     * 节点类型 {@link top.verytouch.vkit.samples.activity.modler.NodeType}
     */
    private String type;
    /**
     * 处理人ID
     */
    private String assignee;
    /**
     * 候选人ID
     */
    private List<String> candidatesUsers;
    /**
     * 候选组ID
     */
    private List<String> candidateGroups;
    /**
     * 任务实例数
     */
    private Integer loopCardinality = 1;
    /**
     * 完成条件
     */
    private String completeCondition = "${nrOfCompletedInstances/nrOfInstances>=1}";
}
