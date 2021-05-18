package top.verytouch.vkit.common.util;

import org.junit.Test;

import java.util.Map;

public class MapUtilsTest {

    @Test
    public void testCreate() {
        final Map<String, Object> map = MapUtils.singleEntryHashMap("哈", "很");
        System.out.println(map);
    }

    @Test
    public void tesBuilder() {
        final Map<String, Object> map = MapUtils.Builder.hashMap()
                .put("key", "124")
                .put("page", 1)
                .put("size", 20)
                .build();
        System.out.println(map);
    }

}