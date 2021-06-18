package top.verytouch.vkit.rbac.util;

import top.verytouch.vkit.common.util.HttpUtils;
import top.verytouch.vkit.common.util.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * IP工具
 *
 * @author verytouch
 * @since 2021/5/13 15:17
 */
@SuppressWarnings("unused")
public class IPUtils {

    /**
     * 获取真实IP地址
     * <p>使用getRealIP代替该方法</p>
     *
     * @param request req
     * @return ip
     */
    public static String getClientIpByReq(HttpServletRequest request) {
        // 获取客户端ip地址
        String clientIp = request.getHeader("X-Forward-For");

        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        /*
         * 对于获取到多ip的情况下，找到公网ip.
         */
        String sIP = null;
        if (clientIp != null && !clientIp.contains("unknown") && clientIp.indexOf(",") > 0) {
            String[] ipArr = clientIp.split(",");
            for (String ip : ipArr) {
                if (!isInnerIP(ip.trim())) {
                    sIP = ip.trim();
                    break;
                }
            }
            /*
             * 如果多ip都是内网ip，则取第一个ip.
             */
            if (null == sIP) {
                sIP = ipArr[0].trim();
            }
            clientIp = sIP;
        }
        if (clientIp != null && clientIp.contains("unknown")) {
            clientIp = clientIp.replaceAll("unknown,", "");
            clientIp = clientIp.trim();
        }
        if ("".equals(clientIp) || null == clientIp) {
            clientIp = "127.0.0.1";
        }
        return clientIp;
    }


    /**
     * 判断IP是否是内网地址
     *
     * @param ipAddress ip地址
     * @return 是否是内网地址
     */
    public static boolean isInnerIP(String ipAddress) {
        /*
         * 私有IP：
         * A类  10.0.0.0-10.255.255.255
         * B类  172.16.0.0-172.31.255.255
         * C类  192.168.0.0-192.168.255.255
         * 当然，还有127这个网段是环回地址
         */
        boolean isInnerIp;
        long ipNum = getIpNum(ipAddress);
        long aBegin = getIpNum("10.0.0.0");
        long aEnd = getIpNum("10.255.255.255");

        long bBegin = getIpNum("172.16.0.0");
        long bEnd = getIpNum("172.31.255.255");

        long cBegin = getIpNum("192.168.0.0");
        long cEnd = getIpNum("192.168.255.255");
        isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd)
                || ipAddress.equals("127.0.0.1");
        return isInnerIp;
    }

    /**
     * 根据IP获取真实地址
     *
     * @param ip ip地址
     */
    @SuppressWarnings("rawtypes")
    public static String getAddress(String ip) {
        try {
            String res = new HttpUtils("https://ip.taobao.com/outGetIpInfo")
                    .addParam("ip", ip)
                    .addParam("accessKey", "alibaba-inc")
                    .post();
            Map map = JsonUtils.fromJson(res, HashMap.class);
            if (!Objects.equals(0, map.get("code"))) {
                return "";
            }
            Map data = (Map) map.get("data");
            return data.get("country") + "/" + data.get("region") + "/" + data.get("city");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static long getIpNum(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        long a = Integer.parseInt(ip[0]);
        long b = Integer.parseInt(ip[1]);
        long c = Integer.parseInt(ip[2]);
        long d = Integer.parseInt(ip[3]);

        return a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
    }

    private static boolean isInner(long userIp, long begin, long end) {
        return (userIp >= begin) && (userIp <= end);
    }

}
