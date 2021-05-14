package com.verytouch.vkit.rabc.oauth2;

import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * 为了在外部注入AnotherClientDetailsService
 * XXX 直接注入ClientDetailsService会导致StackOverflow
 *
 * @author verytouch
 * @since 2021/5/14 17:30
 */
public interface AnotherClientDetailsService extends ClientDetailsService {

}
