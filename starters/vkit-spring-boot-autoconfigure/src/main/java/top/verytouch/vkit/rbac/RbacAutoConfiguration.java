package top.verytouch.vkit.rbac;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import top.verytouch.vkit.rbac.config.AuthorizationSererConfig;
import top.verytouch.vkit.rbac.config.ResourceServerConfig;
import top.verytouch.vkit.rbac.config.WebSecurityConfig;
import top.verytouch.vkit.rbac.oauth2.*;
import top.verytouch.vkit.rbac.util.ApplicationContextUtils;
import top.verytouch.vkit.rbac.web.RestControllerAdvice;

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
public class RbacAutoConfiguration implements ApplicationContextAware {

    @Autowired
    private RbacProperties rbacProperties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.init(applicationContext);
    }

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
        return userDetails -> {
            ApplicationContextUtils.publishEvent(new LoginEvent(true, userDetails, null));
            return null;
        };
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
