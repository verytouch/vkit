package com.verytouch.vkit.ocr.ali;

import com.verytouch.vkit.common.exception.BusinessException;
import com.verytouch.vkit.common.util.HttpUtils;
import com.verytouch.vkit.common.util.JsonUtils;
import com.verytouch.vkit.common.util.MapUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 阿里OCR
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Slf4j
public class AliOcrService {

    private final AliOcrProperties properties;

    public AliOcrService(AliOcrProperties properties) {
        this.properties = properties;
    }

    /**
     * 通用识别
     *
     * @param img 图片url/二进制数据的base64编码
     */
    public String general(String img) {
        Map<String, Object> configure = MapUtils.Builder.hashMap()
                .put("min_size", properties.getMinSize())
                .put("output_prob", properties.isOutputProb())
                .put("output_keypoints", properties.isOutputKeyPoints())
                .put("skip_detection", properties.isSkipDetection())
                .put("without_predicting_direction", properties.isWithoutPredictingDirection())
                .build();
        Map<String, Object> body = MapUtils.Builder.hashMap()
                .put("image", img)
                .put("configure", configure)
                .build();
        try {
            return new HttpUtils(properties.getHost() + "/ocr_general")
                .addHeader("Authorization", String.format("APPCODE %s", properties.getAppCode()))
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .body(JsonUtils.toJson(body).getBytes(StandardCharsets.UTF_8))
                .post();
        } catch (Exception e) {
            log.error(String.format("通用OCR请求失败，img=%s", img), e);
            throw new BusinessException("OCR请求失败");
        }
    }

}
