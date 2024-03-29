package top.verytouch.vkit.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Map工具类
 *
 * @author verytouch
 * @since 2020/9/29 16:35
 */
@SuppressWarnings("unused")
public class MapUtils {

    /**
     * 返回一个元素的Map
     *
     * @param k 键
     * @param v 值
     * @return HashMap
     */
    public static <K, V> Map<K, V> singleEntryHashMap(K k, V v) {
        Map<K, V> map = new HashMap<>(2);
        map.put(k, v);
        return map;
    }

    /**
     * Map构建
     */
    public static class Builder<K, V> {

        private final Map<K, V> map;

        private Builder(Map<K, V> map) {
            this.map = map;
        }

        public static <K, V> Builder<K, V> map(Supplier<Map<K, V>> mapSupplier) {
            return new Builder<>(mapSupplier.get());
        }

        public static <K, V> Builder<K, V> hashMap(Class<K> kClass, Class<V> vClass) {
            return new Builder<>(new HashMap<>());
        }

        public static Builder<String, Object> hashMap(int initialCapacity) {
            return new Builder<>(new HashMap<>(initialCapacity));
        }


        public static Builder<String, Object> hashMap() {
            return new Builder<>(new HashMap<>());
        }

        public Builder<K, V> put(K k, V v) {
            this.map.put(k, v);
            return this;
        }

        public Builder<K, V> replace(K k, V v) {
            this.map.replace(k, v);
            return this;
        }

        public Builder<K, V> remove(K k) {
            this.map.remove(k);
            return this;
        }

        public Map<K, V> build() {
            return this.map;
        }
    }

    /**
     * Map读取
     */
    @SuppressWarnings("rawtypes")
    public static class Reader {

        private Map map;

        private Reader(Map map) {
            this.map = map;
        }

        public static Reader map(Map map) {
            return new Reader(map);
        }

        /**
         * 让reader进入key对应的map
         */
        public Reader skin(Object key) {
            this.map = (Map) this.map.get(key);
            return this;
        }

        /**
         * 返回map
         */
        public Map getMap(Object key) {
            return (Map) this.map.get(key);
        }

        /**
         * 返回list
         */
        @SuppressWarnings("unchecked")
        public List<Map> getList(Object key) {
            return (List<Map>) this.map.get(key);
        }
    }
}
