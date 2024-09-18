package top.verytouch.vkit.mydoc.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 文档类型
 *
 * @author verytouch
 * @since 2021-12
 */
@Getter
@RequiredArgsConstructor
public enum DocType {

    MARK_DOWN("markdown", ".md", "markdown.ftl"),
    HTML("html", ".html", "html.ftl"),
    JSON("json model", ".json", ""),
    POSTMAN("postman", ".postman.json", ""),
    WORD("word", ".docx", "word.ftl"),
    IDEA_HTTP("idea http", ".http", ""),
    NONE("api", "", ""),
    CURL("curl", "", ""),
    API_FOX("apifox", "", ""),
    OPEN_API("openapi", "", ""),
    JSON_SCHEMA("json schema", ".json", "");

    /**
     * 默认模板目录
     */
    public static final String TEMPLATE_DIR = "template";

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 导出文件的后缀
     */
    private final String suffix;

    /**
     * 模板文件的名称
     */
    private final String templateFileName;
}
