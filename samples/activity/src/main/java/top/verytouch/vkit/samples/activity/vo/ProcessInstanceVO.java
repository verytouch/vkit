package top.verytouch.vkit.samples.activity.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.activiti.engine.history.HistoricProcessInstance;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class ProcessInstanceVO {

    private String id;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String startUserId;
    private Date startTime;
    private Date endTime;
    private List<TaskVO> taskList;
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
