package top.verytouch.vkit.mydoc.model;

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

    MAP(Pattern.compile("^java.util.*Map|com.alibaba.fastjson.JSONObject", Pattern.CASE_INSENSITIVE)),

    ARRAY(Pattern.compile("^java.util.*(Collection|List|Set)|com.alibaba.fastjson.JSONArray", Pattern.CASE_INSENSITIVE)),

    DIAMOND(Pattern.compile("^[A-Z]$")),

    ENUM(Pattern.compile("java.lang.Enum")),

    JAVA(Pattern.compile("^java.*", Pattern.CASE_INSENSITIVE)),

    SERVLET(Pattern.compile("javax.servlet.*")),

    WEB_ANNO(Pattern.compile("org.springframework.web.bind.annotation.*")),

    OTHERS(Pattern.compile(".*", Pattern.CASE_INSENSITIVE));

    private final Pattern pattern;

    public boolean isMatch(PsiType psiType) {
        if (this == ENUM) {
            PsiClass psiClass = PsiTypesUtil.getPsiClass(psiType);
            return psiClass != null && psiClass.isEnum();
        }
        return pattern.matcher(psiType.getCanonicalText()).find();
    }

    public boolean isNotMatch(PsiType psiType) {
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
