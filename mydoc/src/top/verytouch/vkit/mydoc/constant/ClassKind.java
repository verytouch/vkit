package top.verytouch.vkit.mydoc.constant;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 数据类型
 *
 * @author verytouch
 * @since 2021-11
 */
@Getter
@RequiredArgsConstructor
public enum ClassKind {

    PRIMARY(Pattern.compile("^[a-z]+$"), "", true),

    MAP(Pattern.compile("^java.util.*Map|com.alibaba.fastjson.JSONObject", Pattern.CASE_INSENSITIVE), "object", true),

    ARRAY(Pattern.compile("^java.util.*(Collection|List|Set)|com.alibaba.fastjson.JSONArray", Pattern.CASE_INSENSITIVE), "array", false),

    DIAMOND(Pattern.compile("^[A-Z]$"), "object", false),

    ENUM(Pattern.compile("java.lang.Enum"), "string", true),

    JAVA(Pattern.compile("^java.*", Pattern.CASE_INSENSITIVE), "", true),

    SERVLET(Pattern.compile("javax.servlet.*"), "object", true),

    FILE(Pattern.compile("org.springframework.web.multipart.*"), "file", true),

    WEB_ANNO(Pattern.compile("org.springframework.web.bind.annotation.*"), "object", true),

    OTHERS(Pattern.compile(".*", Pattern.CASE_INSENSITIVE), "object", false);

    /**
     * 匹配类全限定名的正则
     */
    private final Pattern pattern;

    /**
     * 对应的openApiType，不确定时留空
     */
    private final String openApiType;

    /**
     * 是否忽略子节点，为true时不解析其属性
     */
    private final boolean ignoreChildren;

    public boolean isMatch(PsiType psiType) {
        if (psiType == null) {
            return false;
        }
        if (this == ENUM) {
            PsiClass psiClass = PsiTypesUtil.getPsiClass(psiType);
            return psiClass != null && psiClass.isEnum();
        }
        return pattern.matcher(psiType.getCanonicalText()).find();
    }

    public boolean isNotMatch(PsiType psiType) {
        if (psiType == null) {
            return true;
        }
        return !pattern.matcher(psiType.getCanonicalText()).find();
    }

    public boolean isMatch(String qualifiedName) {
        return StringUtils.isNotBlank(qualifiedName) && pattern.matcher(qualifiedName).find();
    }

    public static ClassKind of(PsiType psiType) {
        return Arrays.stream(ClassKind.values())
                .filter(item -> item.isMatch(psiType))
                .findAny().orElse(ClassKind.OTHERS);
    }
}
