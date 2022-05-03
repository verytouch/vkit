package top.verytouch.vkit.samples.activity.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.activiti.engine.history.HistoricProcessInstance;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class ProcessInstanceVO {

    /**
     * 流程实例ID
     */
    private String id;
    /**
     * 流程定义ID
     */
    private String processDefinitionId;
    /**
     * 流程定义KEY
     */
    private String processDefinitionKey;
    /**
     * 启动用户ID
     */
    private String startUserId;
    /**
     * 申请时间
     */
    private Date startTime;
    /**
     * 完成时间
     */
    private Date endTime;
    /**
     * 任务列表
     */
    private List<TaskVO> taskList;
    /**
     * 业务对象
     */
    private Object businessObject;

    public static ProcessInstanceVO valueOf(HistoricProcessInstance processInstance) {
        return new ProcessInstanceVO()
                .setId(processInstance.getId())
                .setProcessDefinitionId(processInstance.getProcessDefinitionId())
                .setProcessDefinitionKey(processInstance.getProcessDefinitionKey())
                .setStartTime(processInstance.getStartTime())
                .setStartUserId(processInstance.getStartUserId())
                .setEndTime(processInstance.getEndTime());
    }
}
