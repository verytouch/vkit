package top.verytouch.vkit.samples.activity.modler;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.ActivitiException;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.activiti.validation.ProcessValidatorFactory;
import org.activiti.validation.ValidationError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class BpmnUtils {

    public static BpmnModel createBpmnModel(Flow flow) {
        Process process = new Process();
        process.setId(flow.getKey());
        process.setName(flow.getName());
        // 节点
        Map<String, FlowNode> flowNodeMap = new HashMap<>(flow.getNodeList().size());
        for (Node node : flow.getNodeList()) {
            FlowNode flowNode = createFlowNode(node);
            process.addFlowElement(flowNode);
            flowNodeMap.put(node.getId(), flowNode);
        }
        // 连线
        for (Sequence sequence : flow.getSequenceList()) {
            FlowNode source = flowNodeMap.get(sequence.getSourceId());
            FlowNode target = flowNodeMap.get(sequence.getTargetId());
            SequenceFlow sequenceFlow = createSequenceFlow(sequence, source, target);
            process.addFlowElement(sequenceFlow);
        }
        BpmnModel model = new BpmnModel();
        model.addProcess(process);
        // 布局
        new BpmnAutoLayout(model).execute();
        // 验证
        validate(model);
        return model;
    }

    public static InputStream generateDiagram(BpmnModel model) {
        return new DefaultProcessDiagramGenerator().generateDiagram(model,
                "png",
                "宋体", "宋体",
                "宋体",
                BpmnUtils.class.getClassLoader());
    }

    public static FlowNode createFlowNode(Node node) {
        NodeType type = NodeType.valueOf(node.getType());
        return type.getNodeMapper().apply(node);
    }

    public static StartEvent createStartEvent(Node node) {
        StartEvent startEvent = new StartEvent();
        startEvent.setId(node.getId());
        startEvent.setName(node.getName());
        return startEvent;
    }

    public static EndEvent createEndEvent(Node node) {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(node.getId());
        endEvent.setName(node.getName());
        return endEvent;
    }

    public static UserTask createUserTask(Node node) {
        UserTask userTask = new UserTask();
        userTask.setId(node.getId());
        userTask.setName(node.getName());
        if (StringUtils.isNotBlank(node.getAssignee())) {
            userTask.setAssignee(node.getAssignee());
        }
        return userTask;
    }

    public static ExclusiveGateway createExclusiveGateway(Node node) {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId(node.getId());
        exclusiveGateway.setName(node.getName());
        return exclusiveGateway;
    }

    public static SequenceFlow createSequenceFlow(Sequence sequence, FlowNode source, FlowNode target) {
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId(sequence.getId());
        sequenceFlow.setName(sequence.getName());
        sequenceFlow.setSourceRef(source.getId());
        sequenceFlow.setTargetRef(target.getId());
        if (StringUtils.isNotBlank(sequence.getCondition())) {
            sequenceFlow.setConditionExpression(sequence.getCondition());
        }
        // 出线
        List<SequenceFlow> outgoingFlows = source.getOutgoingFlows();
        if (outgoingFlows == null) {
            outgoingFlows = new ArrayList<>();
            source.setOutgoingFlows(outgoingFlows);
        }
        outgoingFlows.add(sequenceFlow);
        // 进线
        List<SequenceFlow> incomingFlows = target.getIncomingFlows();
        if (incomingFlows == null) {
            incomingFlows = new ArrayList<>();
            target.setIncomingFlows(incomingFlows);
        }
        incomingFlows.add(sequenceFlow);
        return sequenceFlow;
    }

    public static void validate(BpmnModel model) {
        List<ValidationError> errors = new ProcessValidatorFactory()
                .createDefaultProcessValidator()
                .validate(model);
        if (CollectionUtils.isEmpty(errors)) {
            return;
        }
        String problem = errors.stream()
                .map(ValidationError::getProblem)
                .collect(Collectors.joining("\n"));
        throw new ActivitiException(problem);
    }
}
