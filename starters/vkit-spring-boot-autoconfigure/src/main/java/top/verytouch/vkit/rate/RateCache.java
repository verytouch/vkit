package top.verytouch.vkit.rate;

/**
 * rate cache
 *
 * @author verytouch
 * @since 2023/10/14 12:01
 */
public interface RateCache {

    void store(String key, long expireAt);

    boolean exist(String key);
}
