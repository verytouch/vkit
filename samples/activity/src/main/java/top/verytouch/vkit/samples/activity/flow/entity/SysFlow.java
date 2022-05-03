package top.verytouch.vkit.samples.activity.flow.entity;

import lombok.Data;

/**
 * @author groovy
 * @since 2021-10-17 12:46:10
 */
@Data
public class SysFlow {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 流程设计数据json
     */
    private String data;

    /**
     * 修改时间
     */
    private java.util.Date updateTime;

}
