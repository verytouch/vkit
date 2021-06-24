package top.verytouch.vkit.oss.minio;

import top.verytouch.vkit.common.exception.BusinessException;
import top.verytouch.vkit.oss.OssHelper;
import top.verytouch.vkit.oss.OssProperties;
import top.verytouch.vkit.oss.OssService;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * MinIo，开源OSS
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Slf4j
public class MinIoService implements OssService {

    private final OssProperties properties;
    private final MinioClient client;


    public MinIoService(OssProperties properties) {
        this.properties = properties;
        this.client = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessId(), properties.getAccessKey())
                .build();
    }

    @Override
    public String upload(String bucket, String object, File file) {
        checkBucket(bucket);
        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(object)
                    .stream(new FileInputStream(file), file.length(), -1)
                    .contentType(OssHelper.getContentType(file))
                    .build();
            client.putObject(args);
            return getPreviewUrl(bucket, object);
        } catch (Exception e) {
            log.error(String.format("上传文件失败，bucket=%s，object=%s，file=%s", bucket, object, file.getName()), e);
            throw new BusinessException("上传失败");
        }
    }

    @Override
    public String upload(String bucket, String object, InputStream stream, long length, String contentType) {
        checkBucket(bucket);
        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(object)
                    .stream(stream, length, -1)
                    .contentType(contentType)
                    .build();
            client.putObject(args);
            return getPreviewUrl(bucket, object);
        } catch (Exception e) {
            log.error(String.format("上传文件失败，bucket=%s，object=%s", bucket, object), e);
            throw new BusinessException("上传失败");
        }
    }

    @Override
    public String getUploadUrl(String bucket, String object) {
        checkBucket(bucket);
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .bucket(bucket)
                .object(object)
                .method(Method.PUT)
                .expiry(properties.getUploadUrlExpireMinutes(), TimeUnit.MINUTES)
                .build();
        try {
            return client.getPresignedObjectUrl(args);
        } catch (Exception e) {
            log.error(String.format("生成上传链接失败，bucket=%s，object=%s", bucket, object), e);
            throw new BusinessException("生成上传链接失败", e);
        }
    }

    @Override
    public String getPreviewUrl(String bucket, String object) {
        return String.format("%s/%s/%s", properties.getEndpoint(), bucket, object);
    }

    @Override
    public String getPreviewUrl(String bucket, String object, Duration duration) {
        checkBucket(bucket);
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .bucket(bucket)
                .object(object)
                .method(Method.GET)
                .expiry((int) duration.toMinutes(), TimeUnit.MINUTES)
                .build();
        try {
            return client.getPresignedObjectUrl(args);
        } catch (Exception e) {
            log.error(String.format("生成预览链接失败，bucket=%s，object=%s", bucket, object), e);
            throw new BusinessException("生成预览链接失败", e);
        }
    }

    @Override
    public InputStream download(String bucket, String object) {
        checkBucket(bucket);
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucket)
                .object(object)
                .build();
        try {
            return client.getObject(args);
        } catch (Exception e) {
            log.error(String.format("下载文件失败，bucket=%s，object=%s", bucket, object), e);
            throw new BusinessException("下载文件失败");
        }
    }

    @Override
    public void delete(String bucket, String object) {
        checkBucket(bucket);
        try {
            client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(object).build());
        } catch (Exception e) {
            log.error(String.format("删除文件失败，bucket=%s，object=%s", bucket, object), e);
            throw new BusinessException("删除文件失败");
        }
    }

    @Override
    public void checkBucket(String bucket) {
        if (properties.isValidateBucket() && !properties.getValidBuckets().contains(bucket)) {
            throw new BusinessException("bucket " + bucket + " 不合法");
        }
    }
}
