package top.verytouch.vkit.samples.activity.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.verytouch.vkit.common.util.JsonUtils;
import top.verytouch.vkit.common.util.StringUtils;
import top.verytouch.vkit.samples.activity.flow.entity.SysFlow;
import top.verytouch.vkit.samples.activity.flow.entity.SysFlowDeploy;
import top.verytouch.vkit.samples.activity.flow.mapper.SysFlowDeployMapper;
import top.verytouch.vkit.samples.activity.flow.mapper.SysFlowMapper;
import top.verytouch.vkit.samples.activity.modler.Flow;

import java.util.Date;
import java.util.List;

@Service
public class FlowService {

    @Autowired
    SysFlowMapper sysFlowMapper;

    @Autowired
    SysFlowDeployMapper sysFlowDeployMapper;

    public Flow getFlow(String key) {
        SysFlow sysFlow = getSysFlow(key);
        if (sysFlow == null || StringUtils.isBlank(sysFlow.getData())) {
            return null;
        }
        return json2Flow(sysFlow.getData());
    }

    public Flow getFlow(String key, int version) {
        SysFlowDeploy sysFlowDeploy = getSysFlowDeploy(key, version);
        if (sysFlowDeploy == null || StringUtils.isBlank(sysFlowDeploy.getData())) {
            return null;
        }
        return json2Flow(sysFlowDeploy.getData());
    }

    public void saveSysFlow(Flow flow) {
        SysFlow sysFlow = flow2SysFlow(flow);
        SysFlow dbData = sysFlowMapper.getById(flow.getKey());
        if (dbData == null) {
            sysFlowMapper.insert(sysFlow);
        } else {
            sysFlowMapper.updateById(sysFlow);
        }
    }

    public SysFlow getSysFlow(String key) {
        return sysFlowMapper.getById(key);
    }

    public void saveSysFlowDeploy(Flow flow, int version) {
        SysFlowDeploy sysFlowDeploy = new SysFlowDeploy();
        sysFlowDeploy.setFlowId(flow.getKey());
        sysFlowDeploy.setName(flow.getName());
        sysFlowDeploy.setVersion(version);
        sysFlowDeploy.setData(JsonUtils.toJson(flow));
        sysFlowDeploy.setDeployTime(new Date());
        sysFlowDeployMapper.insert(sysFlowDeploy);
    }

    public SysFlowDeploy getSysFlowDeploy(String flowId, int version) {
        if (StringUtils.isBlank(flowId)) {
            return null;
        }
        SysFlowDeploy param = new SysFlowDeploy();
        param.setFlowId(flowId);
        param.setVersion(version);
        List<SysFlowDeploy> list = sysFlowDeployMapper.getList(param);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<SysFlowDeploy> getDeployHistory(String flowId) {
        SysFlowDeploy param = new SysFlowDeploy();
        param.setFlowId(flowId);
        return sysFlowDeployMapper.getList(param);
    }

    private SysFlow flow2SysFlow(Flow flow) {
        SysFlow sysFlow = new SysFlow();
        sysFlow.setId(flow.getKey());
        sysFlow.setName(flow.getName());
        sysFlow.setUpdateTime(new Date());
        sysFlow.setData(JsonUtils.toJson(flow));
        return sysFlow;
    }

    private Flow json2Flow(String jsonData) {
        return JsonUtils.fromJson(jsonData, Flow.class);
    }
}
