package top.verytouch.vkit.mydoc.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 接口字段
 *
 * @author verytouch
 * @since 2021-11
 */
@Data
@Accessors(chain = true)
public class ApiField {

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段描述
     */
    private String desc;

    /**
     * 字段类型描述，简洁的java类名
     * 如：String、Date、List<Bean>
     */
    private String type;

    /**
     * 数据类型
     */
    private ClassKind classKind;

    /**
     * 是否必须
     */
    private Boolean required;

    /**
     * 子元素
     */
    private List<ApiField> children;

}
