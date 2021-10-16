package top.verytouch.vkit.samples.activity.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;

import java.util.Date;

@Data
@Accessors(chain = true)
public class TaskVO {

    private String id;
    private String key;
    private String name;
    private String assignee;
    private Date createTime;
    private Date endTime;
    private String remark;

    private String executionId;
    private String processInstanceId;
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
