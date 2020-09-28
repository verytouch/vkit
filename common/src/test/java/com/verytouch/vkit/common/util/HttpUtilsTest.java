package com.verytouch.vkit.common.util;

import org.junit.Test;

public class HttpUtilsTest {

    @Test
    public void test() throws Exception {
        final String request = new HttpUtils("http://localhost:7011/api-user/product/pager")
                .addHeader("token", "ecba0375509a4a548b6fc8cabc9575c6")
                .addParam("page", "1")
                .addParam("pageSize", "1")
                .request();
        System.out.println(request);
    }

    @Test
    public void testHttps() throws Exception {
        final String res = new HttpUtils("https://vip.vstecs.com/api-user/department/register/treeList").request();
        System.out.println(res);
    }
}
