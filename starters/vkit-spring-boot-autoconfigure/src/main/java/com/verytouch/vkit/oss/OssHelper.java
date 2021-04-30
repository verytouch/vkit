package com.verytouch.vkit.oss;

import com.verytouch.vkit.common.util.StringUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;

/**
 * OSS帮助类
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
public class OssHelper {

    private static final MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();

    /**
     * 从对象中获取文件名
     *
     * @param object 对象名
     * @return 文件名
     */
    public static String getFileNameOfObject(String object) {
        if (StringUtils.isBlank(object) || !object.contains("/")) {
            return object;
        }
        return object.substring(object.lastIndexOf("/") + 1);
    }

    /**
     * 获取文件的contentType
     *
     * @param file 文件
     * @return contentType
     */
    public static String getContentType(File file) {
        return fileTypeMap.getContentType(file);
    }

    /**
     * 判断contentType是否图片
     *
     * @param contentType 文件的contentType
     * @return true/false
     */
    public static boolean isImage(String contentType) {
        return StringUtils.isNoneBlank(contentType) && contentType.startsWith("image");
    }

}
