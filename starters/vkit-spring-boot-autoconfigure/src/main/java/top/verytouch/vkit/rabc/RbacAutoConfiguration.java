package top.verytouch.vkit.rabc;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import top.verytouch.vkit.rabc.config.AuthorizationSererConfig;
import top.verytouch.vkit.rabc.config.ResourceServerConfig;
import top.verytouch.vkit.rabc.config.WebSecurityConfig;
import top.verytouch.vkit.rabc.oauth2.AesPasswordEncoder;
import top.verytouch.vkit.rabc.oauth2.InMemoryAuthorizationCodeService;
import top.verytouch.vkit.rabc.oauth2.JwtUserDetailsTokenEnhancer;
import top.verytouch.vkit.rabc.oauth2.ParameterPasswordEncoder;
import top.verytouch.vkit.rabc.web.RestControllerAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
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
@ConditionalOnClass({WebSecurityConfigurerAdapter.class, AuthorizationServerConfigurerAdapter.class, ResourceServerConfigurerAdapter.class})
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

    @Bean
    @ConditionalOnProperty(prefix = "vkit.rbac", name = "exceptionHandlerEnabled", havingValue = "true", matchIfMissing = true)
    public RestControllerAdvice restControllerAdvice() {
        return new RestControllerAdvice();
    }

    @Bean
    @ConditionalOnMissingBean(CorsFilter.class)
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

}
