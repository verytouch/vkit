package top.verytouch.vkit.mydoc.util;

import com.intellij.psi.*;
import top.verytouch.vkit.mydoc.constant.SpecialClassNames;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 注解解析
 *
 * @author verytouch
 * @since 2021-11
 */
public class AnnotationUtil {

    /**
     * 判断是否被注解
     *
     * @param element     被注解的元素
     * @param annotations 注解全限定名
     */
    public static boolean hasAnyAnnotation(PsiJvmModifiersOwner element, String... annotations) {
        return Arrays.stream(annotations)
                .anyMatch(element::hasAnnotation);
    }

    /**
     * 获取注解的属性
     *
     * @param element    被注解的元素
     * @param annotation 注解全限定名
     * @param attribute  注解的属性
     */
    public static String getAnnotationAttribute(PsiJvmModifiersOwner element, String annotation, String attribute) {
        PsiAnnotation psiAnnotation = element.getAnnotation(annotation);
        if (psiAnnotation == null) {
            return null;
        }
        PsiAnnotationMemberValue attributeValue = psiAnnotation.findAttributeValue(attribute);
        if (attributeValue == null) {
            return null;
        }
        return attributeValue.getText()
                .replaceAll("\"", "")
                .trim()
                .replace("{}", "");
    }

    /**
     * 获取有某个注解的参数
     *
     * @param psiParameterList 参数信息
     * @param annotation       注解全限定名
     */
    public static List<PsiParameter> findAnnotatedParameter(PsiParameterList psiParameterList, String annotation) {
        return Arrays.stream(psiParameterList.getParameters())
                .filter(parameter -> parameter != null && parameter.hasAnnotation(annotation))
                .collect(Collectors.toList());
    }

    /**
     * 从方法中获取接口的path和method
     *
     * @param psiMethod 方法
     * @return [path, method, contentType]
     */
    public static String[] getApiPathAndMethod(PsiMethod psiMethod) {
        for (Map.Entry<String, Object> annotation : SpecialClassNames.MAPPING_METHOD_MAP.entrySet()) {
            PsiAnnotation psiAnnotation = psiMethod.getAnnotation(annotation.getKey());
            if (psiAnnotation == null) {
                continue;
            }
            // path
            String path = getAnnotationAttribute(psiMethod, annotation.getKey(), "value");
            path = ApiUtil.getFirstPath(path);
            // contentType
            String consumes = getAnnotationAttribute(psiMethod, annotation.getKey(), "consumes");
            consumes = Objects.toString(consumes, "")
                    .replaceAll("^\\{", "")
                    .replaceAll("}", "");
            consumes = SpecialClassNames.MEDIA_TYPE_MAP.getOrDefault(consumes, consumes).toString();
            if (StringUtils.isBlank(consumes)) {
                consumes = "*/*";
            }
            // method
            String method = getAnnotationAttribute(psiMethod, annotation.getKey(), "method");
            method = Arrays.stream(Objects.toString(method, "").split("\\W"))
                    .filter(SpecialClassNames.MAPPING_METHOD_MAP.values()::contains)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(","));
            if (StringUtils.isBlank(method)) {
                method = annotation.getValue().toString();
                if (StringUtils.isBlank(method)) {
                    method = "GET,POST";
                }
            }
            return new String[]{path, method, consumes};
        }
        return null;
    }
}
