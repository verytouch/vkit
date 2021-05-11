package com.verytouch.vkit.oss.ali;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.verytouch.vkit.common.exception.BusinessException;
import com.verytouch.vkit.oss.OssProperties;
import com.verytouch.vkit.oss.OssService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 阿里OSS
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Slf4j
public class AliOssService implements OssService {

    private final OssProperties properties;
    private final OSS oss;

    public AliOssService(OssProperties properties) {
        this.properties = properties;
        this.oss = new OSSClientBuilder().build(properties.getEndpoint(), properties.getAccessId(), properties.getAccessKey());
    }

    @Override
    public String upload(String bucket, String object, File file) {
        checkBucket(bucket);
        try {
            oss.putObject(bucket, object, new FileInputStream(file));
            return getPreviewUrl(bucket, object);
        } catch (Exception e) {
            log.error(String.format("上传文件失败，bucket=%s，object=%s，file=%s", bucket, object, file.getName()), e);
            throw new BusinessException("上传文件失败");
        }
    }

    @Override
    public String upload(String bucket, String object, InputStream stream, long length, String contentType) {
        checkBucket(bucket);
        try {
            oss.putObject(bucket, object, stream);
            return getPreviewUrl(bucket, object);
        } catch (Exception e) {
            log.error(String.format("上传文件失败，bucket=%s，object=%s", bucket, object), e);
            throw new BusinessException("上传文件失败");
        }
    }

    @Override
    public String getUploadUrl(String bucket, String object) {
        checkBucket(bucket);
        String host = String.format("https://%s.%s", bucket, properties.getEndpoint());
        String dir = "";
        Date expire = new Date(System.currentTimeMillis() + properties.getUploadUrlExpireMinutes() * 1000L);

        PolicyConditions conditions = new PolicyConditions();
        conditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 5L * 1024 * 1024 * 1024);
        conditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
        String policy;
        String signature;
        try {
            policy = oss.generatePostPolicy(expire, conditions);
            signature = oss.calculatePostSignature(policy);
        } catch (Exception e) {
            log.error(String.format("生成上传链接失败，bucket=%s", bucket), e);
            throw new BusinessException("生成上传链接失败");
        }
        policy = BinaryUtil.toBase64String(policy.getBytes(StandardCharsets.UTF_8));

        Map<String, String> params = new LinkedHashMap<>();
        params.put("accessid", properties.getAccessId());
        params.put("policy", policy);
        params.put("signature", signature);
        params.put("dir", dir);
        params.put("expire", String.valueOf(expire.getTime() / 1000));
        return host + "?" + params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }

    @Override
    public String getPreviewUrl(String bucket, String object) {
        return String.format("https://%s.%s/%s", bucket, properties.getEndpoint(), object);
    }

    @Override
    public InputStream download(String bucket, String object) {
        checkBucket(bucket);
        try {
            return oss.getObject(bucket, object).getObjectContent();
        } catch (Exception e) {
            log.error(String.format("下载文件失败，bucket=%s，object=%s", bucket, object), e);
            throw new BusinessException("下载文件失败");
        }
    }

    @Override
    public void delete(String bucket, String object) {
        checkBucket(bucket);
        try {
            oss.deleteObject(bucket, object);
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
