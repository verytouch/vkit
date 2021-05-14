package com.verytouch.vkit.rabc.config;

import com.verytouch.vkit.rabc.RbacProperties;
import com.verytouch.vkit.rabc.oauth2.OauthExceptionSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * 资源服务配置
 *
 * @author verytouch
 * @since 2021/5/14 17:42
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig  extends ResourceServerConfigurerAdapter {

    @Autowired
    private RbacProperties rbacProperties;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources
                .authenticationEntryPoint(OauthExceptionSerializer::exceptionHandler)
                .accessDeniedHandler(OauthExceptionSerializer::exceptionHandler);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
                .antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers(rbacProperties.getOpenPath()).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(OauthExceptionSerializer::exceptionHandler)
                .accessDeniedHandler(OauthExceptionSerializer::exceptionHandler);
    }
}
