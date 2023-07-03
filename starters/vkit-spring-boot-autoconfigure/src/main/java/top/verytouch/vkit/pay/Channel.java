package top.verytouch.vkit.pay;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 支付方式
 */
@RequiredArgsConstructor
@Getter
public enum Channel {

    WX_NATIVE(1001, "微信扫一扫支付"),

    WX_JSAPI(1002, "微信公众号支付"),

    WX_H5(1003, "微信H5支付"),

    WS_APP(1004, "微信APP支付"),

    ALI_NATIVE(2002, "支付宝扫一扫支付");

    private final Integer code;

    private final String desc;
}
