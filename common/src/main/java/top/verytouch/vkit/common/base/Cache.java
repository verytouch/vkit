package top.verytouch.vkit.common.base;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存
 * 1.不限制key数量
 * 2.调用方法时才会清理过期key
 * 3.过期时间精确到秒
 * 4.不持久化
 *
 * @author verytouch
 * @since 2021/5/14 15:00
 */
public class Cache<T> {

    private final Map<String, CacheValue<T>> store;

    public Cache() {
        this.store = new ConcurrentHashMap<>();
    }

    /**
     * 存储
     *
     * @param key      键
     * @param data     值
     * @param duration 过期时间，精确到秒
     */
    public void put(String key, T data, Duration duration) {
        clean();
        store.put(key, new CacheValue<>(data, LocalDateTime.now().plusSeconds(duration.getSeconds())));
    }

    /**
     * 存储，不过期
     *
     * @param key  键
     * @param data 值
     */
    public void put(String key, T data) {
        clean();
        store.put(key, new CacheValue<>(data, null));
    }

    /**
     * 获取
     *
     * @param key 键
     * @return 值，没有返回null
     */
    public T get(String key) {
        clean();
        CacheValue<T> value = store.get(key);
        return value == null || value.isExpired() ? null : value.data;
    }

    /**
     * 获取
     *
     * @param key 键
     * @return 值，没有返回defaultVal
     */
    public T getOrDefault(String key, T defaultVal) {
        clean();
        CacheValue<T> value = store.get(key);
        return value == null || value.isExpired() ? defaultVal : value.data;
    }

    /**
     * 移除
     *
     * @param key 键
     * @return 值
     */
    public T remove(String key) {
        clean();
        CacheValue<T> value = store.remove(key);
        return value == null || value.isExpired() ? null : value.data;
    }

    /**
     * 是否存在键
     *
     * @param key 键
     * @return 存在返回true，否则false
     */
    public boolean containsKey(String key) {
        clean();
        return store.containsKey(key);
    }

    /**
     * 移除所有过期的key
     */
    public void clean() {
        store.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * 缓存对象
     *
     * @param <T>
     */
    private static class CacheValue<T> {
        /**
         * 缓存的数据
         */
        T data;
        /**
         * 过期时间，null表示永不过期
         */
        LocalDateTime expireAt;

        public CacheValue(T data, LocalDateTime expireAt) {
            this.data = data;
            this.expireAt = expireAt;
        }

        public boolean isExpired() {
            return expireAt != null && LocalDateTime.now().isAfter(expireAt);
        }
    }
}
