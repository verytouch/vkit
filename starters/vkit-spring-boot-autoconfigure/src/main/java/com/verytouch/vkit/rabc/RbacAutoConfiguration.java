package com.verytouch.vkit.rabc;

import com.verytouch.vkit.rabc.config.AuthorizationSererConfig;
import com.verytouch.vkit.rabc.config.ResourceServerConfig;
import com.verytouch.vkit.rabc.config.WebSecurityConfig;
import com.verytouch.vkit.rabc.oauth2.AesPasswordEncoder;
import com.verytouch.vkit.rabc.oauth2.InMemoryAuthorizationCodeService;
import com.verytouch.vkit.rabc.oauth2.JwtUserDetailsTokenEnhancer;
import com.verytouch.vkit.rabc.oauth2.ParameterPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;

import java.time.Duration;

/**
 * rbac自动配置
 * 使用时需要注入
 * 1.UserDetailService
 * 2.AnotherClientDetailsService
 *
 * @author verytouch
 * @since 2021/5/14 17:48
 */
@Configuration
@EnableConfigurationProperties(RbacProperties.class)
@ConditionalOnProperty(prefix = "vkit.rbac", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({WebSecurityConfig.class, AuthorizationSererConfig.class, ResourceServerConfig.class})
public class RbacAutoConfiguration {

    @Autowired
    private RbacProperties rbacProperties;

    @Bean
    @ConditionalOnMissingBean(ParameterPasswordEncoder.class)
    public ParameterPasswordEncoder parameterPasswordEncoder() {
       return new AesPasswordEncoder(rbacProperties.getParameterAesKey(), null);
    }

    @Bean
    @ConditionalOnMissingBean(AuthorizationCodeServices.class)
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeService(Duration.ofSeconds(3));
    }

    @Bean
    @ConditionalOnMissingBean(JwtUserDetailsTokenEnhancer.class)
    public JwtUserDetailsTokenEnhancer jwtUserDetailsTokenEnhancer() {
        return userDetails -> null;
    }

}
