package top.verytouch.vkit.mydoc.model;

import lombok.Data;

import java.util.List;

/**
 * 接口分类
 *
 * @author verytouch
 * @since 2021-11
 */
@Data
public class ApiGroup {

    /**
     * 分类名称
     */
    private String name;

    /**
     * 接口路径
     */
    private String path;

    /**
     * 分类描述
     */
    private String desc;

    /**
     * 接口负责人
     */
    private String author;

    /**
     * 接口列表
     */
    private List<ApiOperation> operationList;

}
