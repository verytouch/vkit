package top.verytouch.vkit.samples.activity.command;

import lombok.RequiredArgsConstructor;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;

@RequiredArgsConstructor
public class TaskJumpCommand implements Command<Void> {
    /**
     * 当前任务ID
     */
    private final String currentTaskId;
    /**
     * 目标任务定义key
     */
    private final String targetTaskDefinitionKey;

    @Override
    public Void execute(CommandContext commandContext) {
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        TaskEntity taskEntity = taskEntityManager.findById(currentTaskId);
        if (taskEntity == null) {
            throw new ActivitiException("currentTask not found: " + currentTaskId);
        }
        FlowElement flowElement = ProcessEngines.getDefaultProcessEngine()
                .getRepositoryService()
                .getBpmnModel(taskEntity.getProcessDefinitionId())
                .getMainProcess()
                .getFlowElement(targetTaskDefinitionKey);
        if (flowElement == null) {
            throw new ActivitiException("target task not found: " + targetTaskDefinitionKey);
        }
        // 删除当前任务
        taskEntityManager.deleteTask(taskEntity, "JUMP", false, false);
        // 跳转到目标任务
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager()
                .findById(taskEntity.getExecutionId());
        executionEntity.setCurrentFlowElement(flowElement);
        commandContext.getAgenda().planContinueProcessInCompensation(executionEntity);
        return null;
    }
}
