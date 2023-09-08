package top.verytouch.vkit.common.base;

import java.util.Objects;

/**
 * enum通用接口
 *
 * @author verytouch
 * @since 2023/9/8 18:25
 */
public interface Enums {

    Object getCode();

    Object getDesc();

    static <T extends Enums> T valueOf(Object code, Class<T> clazz) {
        for (T constant : clazz.getEnumConstants()) {
            if (Objects.equals(constant.getCode(), code)) {
                return constant;
            }
        }
        return null;
    }

    static <T extends Enums> T valueOf(Object code, Class<T> clazz, T defaultVal) {
        T val = valueOf(code, clazz);
        return val != null ? val : defaultVal;
    }

}
