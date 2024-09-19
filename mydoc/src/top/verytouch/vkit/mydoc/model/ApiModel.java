package top.verytouch.vkit.mydoc.model;

import lombok.Data;
import lombok.experimental.Accessors;
import top.verytouch.vkit.mydoc.config.ConfigStorage;

import java.util.List;

/**
 * 模板渲染数据
 *
 * @author verytouch
 * @since 2021-12
 */
@Data
@Accessors(chain = true)
public class ApiModel {

    /**
     * 文档名称
     */
    private String name;

    /**
     * 数据
     */
    private List<ApiGroup> data;

    /**
     * 配置
     */
    private ConfigStorage config;

}
