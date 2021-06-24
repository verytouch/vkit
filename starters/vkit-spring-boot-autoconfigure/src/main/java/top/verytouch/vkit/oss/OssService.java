package top.verytouch.vkit.oss;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;

/**
 * OSS接口
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@SuppressWarnings("unused")
public interface OssService {

    /**
     * 文件上传
     *
     * @param bucket bucket名称
     * @param object 对象名称，如：avatar/a.jpg
     * @param file   上传的文件
     * @return 文件地址
     */
    String upload(String bucket, String object, File file);

    /**
     * 文件上传
     *
     * @param bucket      bucket名称
     * @param object      对象名称，如：avatar/a.jpg
     * @param stream      上传文件的流
     * @param length      上传文件的大小
     * @param contentType 上传文件的contentType
     * @return 文件地址
     */
    String upload(String bucket, String object, InputStream stream, long length, String contentType);

    /**
     * 获取文件上传连接，通过本连接可以不经过应用服务器直接上传文件到oss
     *
     * @param bucket bucket名称
     * @param object 对象名称，如：avatar/a.jpg
     * @return 文件地址
     */
    String getUploadUrl(String bucket, String object);

    /**
     * 获取文件地址，无超时，要求该文件有公开的读权限
     *
     * @param bucket bucket名称
     * @param object 对象名称，如：avatar/a.jpg
     * @return 文件地址
     */
    String getPreviewUrl(String bucket, String object);

    /**
     * 获取文件预览地址，有超时时间
     *
     * @param bucket   bucket名称
     * @param object   对象名称，如：avatar/a.jpg
     * @param duration 过期时间
     * @return 文件地址
     */
    String getPreviewUrl(String bucket, String object, Duration duration);

    /**
     * 下载文件
     *
     * @param bucket bucket名称
     * @param object 对象名称，如：avatar/a.jpg
     * @return 文件流
     */
    InputStream download(String bucket, String object);

    /**
     * 删除文件
     *
     * @param bucket bucket名称
     * @param object 对象名称，如：avatar/a.jpg
     */
    void delete(String bucket, String object);

    /**
     * 校验bucket是否合法
     *
     * @param bucket bucket名称
     */
    default void checkBucket(String bucket) {
    }

    default boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }
}
