package top.verytouch.vkit.mydoc.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import top.verytouch.vkit.mydoc.constant.ClassKind;

import java.util.LinkedList;
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
     * @mock 标记值
     */
    private String mock;

    /**
     * 子元素
     */
    private List<ApiField> children;

    public void addChildren(List<ApiField> children) {
        if (this.children == null) {
            this.children = new LinkedList<>();
        }
        this.children.addAll(children);
    }

    public String getOpenApiType() {
        String apiType = this.getClassKind().getOpenApiType();
        if (StringUtils.isNotBlank(apiType)) {
            return apiType;
        }
        switch (this.getType().toLowerCase()) {
            case "boolean":
                return "boolean";
            case "byte":
            case "short":
            case "int":
            case "long":
            case "integer":
                return "integer";
            case "float":
            case "double":
            case "decimal":
            case "number":
                return "number";
            case "char":
            case "character":
            case "charsequence":
            case "string":
            case "date":
                return "string";
            default:
                return "object";
        }
    }

}
