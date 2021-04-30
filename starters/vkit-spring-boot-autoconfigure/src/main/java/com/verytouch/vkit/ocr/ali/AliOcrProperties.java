package com.verytouch.vkit.ocr.ali;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 阿里OCR配置类
 *
 * @author verytouch
 * @since 2021/4/29 10:15
 */
@Data
@Validated
@ConfigurationProperties(prefix = "vkit.ocr.ali")
public class AliOcrProperties {
    /**
     * 服务器地址
     */
    private String host = "https://tysbgpu.market.alicloudapi.com/api/predict";
    /**
     * appcode
     */
    private String appCode;

    /**
     * 图片中文字的最小高度，单位像素
     */
    private int minSize = 16;

    /**
     * 是否输出文字框的概率
     */
    private boolean outputProb = true;

    /**
     * 是否输出文字框角点
     */
    private boolean outputKeyPoints = true;

    /**
     * 是否跳过文字检测步骤直接进行文字识别
     */
    private boolean skipDetection = false;

    /**
     * 是否关闭文字行方向预测
     */
    private boolean withoutPredictingDirection = false;
}
