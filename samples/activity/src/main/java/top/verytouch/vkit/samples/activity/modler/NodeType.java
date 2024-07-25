package top.verytouch.vkit.samples.activity.modler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.activiti.bpmn.model.FlowNode;

import java.util.function.Function;

/**
 * 流程节点类型
 */
@RequiredArgsConstructor
@Getter
public enum NodeType {
    /**
     * 开始节点
     */
    START_EVENT(BpmnUtils::createStartEvent),
    /**
     * 结束节点
     */
    END_EVENT(BpmnUtils::createEndEvent),
    /**
     * 用户节点
     */
    USER_TASK(BpmnUtils::createUserTask),
    /**
     * 排他网关
     */
    EXCLUSIVE_GATEWAY(BpmnUtils::createExclusiveGateway),
    /**
     * 并行网关
     */
    PARALLEL_GATEWAY(BpmnUtils::createParallelGateway);

    private final Function<Node, FlowNode> nodeMapper;
}
