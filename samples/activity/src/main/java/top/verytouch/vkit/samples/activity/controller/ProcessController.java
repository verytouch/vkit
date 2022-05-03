package top.verytouch.vkit.samples.activity.controller;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import top.verytouch.vkit.common.base.Assert;
import top.verytouch.vkit.common.base.Response;
import top.verytouch.vkit.samples.activity.flow.FlowService;
import top.verytouch.vkit.samples.activity.modler.BpmnUtils;
import top.verytouch.vkit.samples.activity.modler.Flow;
import top.verytouch.vkit.samples.activity.modler.ProcessDeleteReason;
import top.verytouch.vkit.samples.activity.vo.ProcessInstanceVO;
import top.verytouch.vkit.samples.activity.vo.TaskVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程接口
 *
 * @author zhouwei
 * @date 2021/10/16 21:02
 */
@RestController
@RequestMapping("process")
public class ProcessController {

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    FlowService flowService;

    /**
     * 保存流程设计
     */
    @PostMapping("saveFlow")
    public Response<Void> saveFlow(@RequestBody Flow flow) {
        flowService.saveSysFlow(flow);
        return Response.ok();
    }

    /**
     * 获取流程设计
     *
     * @param processDefinitionKey 流程定义KEY
     */
    @GetMapping("getFlow")
    public Response<Flow> getFlow(@RequestParam String processDefinitionKey) {
        return Response.ok(flowService.getFlow(processDefinitionKey));
    }

    /**
     * 发布流程
     */
    @PostMapping("deploy")
    public void deploy(@RequestBody Flow flow, HttpServletResponse response) throws IOException {
        BpmnModel bpmnModel = BpmnUtils.createBpmnModel(flow);
        Deployment deploy = processEngine.getRepositoryService()
                .createDeployment()
                .addBpmnModel(flow.getName() + ".bpmn", bpmnModel)
                .name(flow.getName())
                .deploy();
        ProcessDefinition processDefinition = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .deploymentId(deploy.getId())
                .singleResult();
        flowService.saveSysFlowDeploy(flow, processDefinition.getVersion());
        response.setContentType("image/png");
        FileCopyUtils.copy(BpmnUtils.generateDiagram(bpmnModel), response.getOutputStream());
    }

    /**
     * 启动流程
     *
     * @param processDefinitionKey 流程定义KEY
     * @param variable             流程变量
     */
    @PostMapping("{processDefinitionKey}/start")
    public Response<String> start(@PathVariable String processDefinitionKey, @RequestBody Map<String, Object> variable) {
        processEngine.getIdentityService().setAuthenticatedUserId("admin");
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceByKey(processDefinitionKey, variable);
        return Response.ok(processInstance.getId());
    }

    /**
     * 取消申请
     *
     * @param processInstanceId 流程实例ID
     */
    @PostMapping("{processInstanceId}/cancel")
    public Response<Void> stop(@PathVariable String processInstanceId) {
        processEngine.getRuntimeService()
                .deleteProcessInstance(processInstanceId, ProcessDeleteReason.CANCEL.name());
        return Response.ok();
    }

    /**
     * 流程进度
     *
     * @param processInstanceId 流程实例ID
     */
    @GetMapping("{processInstanceId}/status")
    public Response<ProcessInstanceVO> status(@PathVariable String processInstanceId) {
        HistoryService historyService = processEngine.getHistoryService();
        // 流程实例
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        Assert.nonNull(historicProcessInstance, "流程不存在");
        ProcessInstanceVO processInstanceVO = ProcessInstanceVO.valueOf(historicProcessInstance);
        // 任务
        List<TaskVO> taskList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime()
                .asc()
                .list()
                .stream().map(TaskVO::valueOf)
                .collect(Collectors.toList());
        processInstanceVO.setTaskList(taskList);
        return Response.ok(processInstanceVO);
    }

    /**
     * 历史流程
     */
    @GetMapping("history")
    public Response<List<ProcessInstanceVO>> history() {
        List<ProcessInstanceVO> instanceVOS = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .orderByProcessInstanceStartTime()
                .asc()
                .list().stream().map(ProcessInstanceVO::valueOf)
                .collect(Collectors.toList());
        return Response.ok(instanceVOS);
    }

    /**
     * 流程图片
     *
     * @param processDefinitionId 流程定义ID
     */
    @GetMapping("img")
    public void history(@RequestParam String processDefinitionId, HttpServletResponse response) throws IOException {
        BpmnModel bpmnModel = processEngine.getRepositoryService()
                .getBpmnModel(processDefinitionId);
        InputStream inputStream = BpmnUtils.generateDiagram(bpmnModel);
        response.setContentType("image/png");
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }
}
