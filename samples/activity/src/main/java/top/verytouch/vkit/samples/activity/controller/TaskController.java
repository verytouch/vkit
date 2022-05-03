package top.verytouch.vkit.samples.activity.controller;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.verytouch.vkit.common.base.Assert;
import top.verytouch.vkit.common.base.Response;
import top.verytouch.vkit.common.util.StringUtils;
import top.verytouch.vkit.samples.activity.command.TaskJumpCommand;
import top.verytouch.vkit.samples.activity.modler.ProcessDeleteReason;
import top.verytouch.vkit.samples.activity.vo.TaskVO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务接口
 *
 * @author zhouwei
 * @date 2021/10/16 21:13
 */
@RestController
@RequestMapping("task")
public class TaskController {

    @Autowired
    ProcessEngine processEngine;

    /**
     * 待办
     *
     * @param assignee 用户ID
     */
    @GetMapping("todo")
    public Response<List<TaskVO>> todo(@RequestParam(required = false) String assignee) {
        TaskQuery taskQuery = processEngine.getTaskService()
                .createTaskQuery();
        if (StringUtils.isNotBlank(assignee)) {
            taskQuery.taskAssignee(assignee);
        }
        List<TaskVO> taskVOS = taskQuery.orderByTaskCreateTime()
                .asc()
                .list()
                .stream().map(TaskVO::valueOf)
                .collect(Collectors.toList());
        return Response.ok(taskVOS);
    }

    /**
     * 完成任务
     *
     * @param taskId    任务ID
     * @param variables 流程变量
     */
    @PostMapping("{taskId}/complete")
    public Response<Void> complete(@PathVariable String taskId, @RequestBody Map<String, Object> variables) {
        processEngine.getTaskService().complete(taskId, variables);
        return Response.ok();
    }

    /**
     * 驳回申请，直接终止流程
     *
     * @param taskId 任务ID
     */
    @PostMapping("{taskId}/reject")
    public Response<Void> reject(@PathVariable String taskId) {
        Task task = processEngine.getTaskService()
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        Assert.nonNull(task, "task id not found");
        processEngine.getRuntimeService()
                .deleteProcessInstance(task.getProcessInstanceId(), ProcessDeleteReason.REJECT.name());
        return Response.ok();
    }

    /**
     * 自由跳转
     *
     * @param currentTaskId 当前任务ID
     * @param targetTaskKey 目标任务key
     */
    @PostMapping("jump")
    public Response<Void> jump(@RequestParam String currentTaskId, @RequestParam String targetTaskKey) {
        TaskJumpCommand command = new TaskJumpCommand(currentTaskId, targetTaskKey);
        processEngine.getManagementService()
                .executeCommand(command);
        return Response.ok();
    }
}
