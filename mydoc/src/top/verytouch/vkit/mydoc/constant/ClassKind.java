package top.verytouch.vkit.mydoc.constant;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;

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

    MAP(Pattern.compile("^java.util.*Map|com.alibaba.fastjson.JSONObject", Pattern.CASE_INSENSITIVE), "object"),

    ARRAY(Pattern.compile("^java.util.*(Collection|List|Set)|com.alibaba.fastjson.JSONArray", Pattern.CASE_INSENSITIVE), "array"),

    DIAMOND(Pattern.compile("^[A-Z]$"), "object"),

    ENUM(Pattern.compile("java.lang.Enum"), "object"),

    JAVA(Pattern.compile("^java.*", Pattern.CASE_INSENSITIVE), ""),

    SERVLET(Pattern.compile("javax.servlet.*"), "object"),

    WEB_ANNO(Pattern.compile("org.springframework.web.bind.annotation.*"), "object"),

    OTHERS(Pattern.compile(".*", Pattern.CASE_INSENSITIVE), "object");

    private final Pattern pattern;
    private final String openApiType;

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