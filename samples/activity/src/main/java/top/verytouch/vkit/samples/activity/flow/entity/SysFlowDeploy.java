package top.verytouch.vkit.samples.activity.flow.entity;

import lombok.Data;

/**
 * @author groovy
 * @since 2021-10-17 12:46:10
 */
@Data
public class SysFlowDeploy {

    private Integer id;

    private String flowId;

    private String name;

    private String data;

    private Integer version;

    private java.util.Date deployTime;

}
