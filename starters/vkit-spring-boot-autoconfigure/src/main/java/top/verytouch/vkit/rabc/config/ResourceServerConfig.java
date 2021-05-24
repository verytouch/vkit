package top.verytouch.vkit.rabc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import top.verytouch.vkit.rabc.RbacProperties;
import top.verytouch.vkit.rabc.oauth2.OauthExceptionSerializer;

import java.util.Map;
import java.util.Set;

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
                .exceptionHandling()
                .authenticationEntryPoint(OauthExceptionSerializer::exceptionHandler)
                .accessDeniedHandler(OauthExceptionSerializer::exceptionHandler)
                .and()
                .cors()
                .and()
                .csrf().disable();

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        if (rbacProperties.getOpenPath() != null && rbacProperties.getOpenPath().length > 0) {
            registry.antMatchers(rbacProperties.getOpenPath()).permitAll();
        }
        if (rbacProperties.getPathAnyAuthorityMapping() != null) {
            Set<Map.Entry<String, String[]>> entries = rbacProperties.getPathAnyAuthorityMapping().entrySet();
            entries.forEach(entry -> registry.antMatchers(entry.getKey()).hasAnyAuthority(entry.getValue()));
        }
        registry.anyRequest().authenticated();
    }
}
