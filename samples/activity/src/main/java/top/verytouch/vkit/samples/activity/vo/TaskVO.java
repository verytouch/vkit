package top.verytouch.vkit.samples.activity.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;

import java.util.Date;

@Data
@Accessors(chain = true)
public class TaskVO {
    /**
     * 任务ID
     */
    private String id;
    /**
     * 任务KEY
     */
    private String key;
    /**
     * 任务名称
     */
    private String name;
    /**
     * 处理人
     */
    private String assignee;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 完成时间
     */
    private Date endTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 流程执行ID
     */
    private String executionId;
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    /**
     * 流程定义ID
     */
    private String processDefinitionId;

    public static TaskVO valueOf(Task task) {
        return new TaskVO()
                .setId(task.getId())
                .setKey(task.getTaskDefinitionKey())
                .setName(task.getName())
                .setAssignee(task.getAssignee())
                .setCreateTime(task.getCreateTime())
                .setExecutionId(task.getExecutionId())
                .setProcessInstanceId(task.getProcessInstanceId())
                .setProcessDefinitionId(task.getProcessDefinitionId());
    }

    public static TaskVO valueOf(HistoricTaskInstance task) {
        return new TaskVO()
                .setId(task.getId())
                .setKey(task.getTaskDefinitionKey())
                .setName(task.getName())
                .setAssignee(task.getAssignee())
                .setCreateTime(task.getCreateTime())
                .setEndTime(task.getEndTime())
                .setRemark(task.getDeleteReason())
                .setExecutionId(task.getExecutionId())
                .setProcessInstanceId(task.getProcessInstanceId())
                .setProcessDefinitionId(task.getProcessDefinitionId());
    }
}
