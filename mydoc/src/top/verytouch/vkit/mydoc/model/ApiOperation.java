package top.verytouch.vkit.mydoc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellij.psi.PsiMethod;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 接口
 *
 * @author verytouch
 * @since 2021-11
 */
@Data
@Accessors(chain = true)
public class ApiOperation {

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口请求方式，get、post等
     */
    private String method;

    /**
     * 接口路径
     */
    private String path;

    /**
     * 接口contentType
     */
    private String contentType;

    /**
     * 接口描述
     */
    private String desc;

    /**
     * 接口负责人
     */
    private String author;

    /**
     * param参数，包括
     * 1. @RequestParam注解的参数
     * 2. @RequestPart注解的参数
     * 3. 没有被spring.web注解的参数
     */
    private List<ApiField> requestParam;

    /**
     * 路径参数，@PathVariable注解的参数
     */
    private List<ApiField> pathVariable;

    /**
     * 请求体
     */
    private List<ApiField> requestBody;

    /**
     * 文件上传，requestFile不空时，认为ContentType是multipart/form-data
     * 忽略requestBody，且requestParam移动到requestFile
     */
    private List<ApiField> requestFile;

    /**
     * 请求示例
     */
    private String requestBodyExample;

    /**
     * 返回体
     */
    private List<ApiField> responseBody;

    /**
     * 返回示例
     */
    private String responseBodyExample;

    @JsonIgnore
    private PsiMethod psiMethod;

}
