package top.verytouch.vkit.common.base;

import top.verytouch.vkit.common.util.MapUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AssertTest {

    @Test
    public void testNonEmpty() {
        List<Object> list = Stream.of(
                "",
                null,
                " ",
                new Integer[0],
                new ArrayList<>(),
                new HashMap<>(),
                MapUtils.singleEntryHashMap("aa", 11)
        ).collect(Collectors.toList());

        int i = 0;
        for (Object o : list) {
            try {
                Assert.nonEmpty(o, "空的");
            } catch (Exception e) {
                System.out.println(i);
            }
            i++;
        }
    }
}