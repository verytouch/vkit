package top.verytouch.vkit.mydoc.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.javadoc.PsiDocParamRef;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 注释解析
 *
 * @author verytouch
 * @since 2021-11
 */
public class CommentUtil {

    /**
     * 获取注释描述
     *
     * @param element 被注释的元素
     */
    public static String getDescription(PsiJavaDocumentedElement element) {
        PsiDocComment docComment = element.getDocComment();
        if (docComment == null) {
            return null;
        }
        PsiElement[] descriptionElements = docComment.getDescriptionElements();
        if (descriptionElements.length == 0) {
            return null;
        }
        return Arrays.stream(descriptionElements)
                .filter(item -> item instanceof PsiDocToken)
                .map(PsiElement::getText)
                .collect(Collectors.joining())
                .trim();
    }

    /**
     * 获取注释描述并去掉空白字符html标签等
     *
     * @param element   被注释的元素
     * @param maxLength 最大长度
     */
    public static String getNoSpacesDescription(PsiJavaDocumentedElement element, int maxLength) {
        String description = getDescription(element);
        if (StringUtils.isBlank(description)) {
            return description;
        }
        description = description.replaceAll("\\s+", "")
                .replaceAll("<.+?>", "");
        return StringUtils.substring(description, 0, maxLength);
    }

    /**
     * 获取注释中的tag值
     *
     * @param element 被注释的元素
     * @param tagName tagName
     */
    public static List<String> getTagValue(PsiJavaDocumentedElement element, String tagName) {
        List<String> values = new ArrayList<>();
        PsiDocComment docComment = element.getDocComment();
        if (docComment == null) {
            return values;
        }
        PsiDocTag[] docTags = docComment.findTagsByName(tagName);
        for (PsiDocTag docTag : docTags) {
            String tagValue = docTag.getText().replace("@" + tagName, "").trim();
            values.add(tagValue);
        }
        return values;
    }

    /**
     * 获取注释中的tag值
     *
     * @param element 被注释的元素
     * @param tagName tagName
     */
    public static String getFirstTagValue(PsiJavaDocumentedElement element, String tagName) {
        PsiDocComment docComment = element.getDocComment();
        if (docComment == null) {
            return null;
        }
        PsiDocTag[] docTags = docComment.findTagsByName(tagName);
        for (PsiDocTag docTag : docTags) {
            String tagValue = docTag.getText().replace("@" + tagName, "").trim();
            if (StringUtils.isNotBlank(tagValue)) {
                return tagValue;
            }
        }
        return null;
    }


    /**
     * 是否有注释tag
     *
     * @param element 被注释的元素
     * @param tagName tagName
     */
    public static boolean hasTag(PsiJavaDocumentedElement element, String tagName) {
        PsiDocComment docComment = element.getDocComment();
        if (docComment == null) {
            return false;
        }
        PsiDocTag[] docTags = docComment.findTagsByName(tagName);
        return docTags.length > 0;
    }

    /**
     * 获取注释中@param标注的键值对
     *
     * @param element 备注注释的元素
     */
    public static Map<String, String> getParamNameValue(PsiJavaDocumentedElement element) {
        Map<String, String> map = new HashMap<>();
        PsiDocComment docComment = element.getDocComment();
        if (docComment == null) {
            return map;
        }
        PsiDocTag[] params = docComment.findTagsByName("param");
        for (PsiDocTag param : params) {
            PsiElement[] dataElements = param.getDataElements();
            String key = null, value = null;
            for (PsiElement dataElement : dataElements) {
                if (dataElement instanceof PsiWhiteSpace) {
                    continue;
                }
                if (dataElement instanceof PsiDocParamRef) {
                    key = dataElement.getText();
                } else {
                    value = dataElement.getText();
                }
                if (key != null && value != null) {
                    break;
                }
            }
            map.put(key, value);
        }
        return map;
    }

}
