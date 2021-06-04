package top.verytouch.vkit.rbac.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import top.verytouch.vkit.common.util.StringUtils;
import top.verytouch.vkit.rbac.RbacProperties;
import top.verytouch.vkit.rbac.oauth2.OauthExceptionSerializer;

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
        resources.authenticationEntryPoint(OauthExceptionSerializer::exceptionHandler)
                .accessDeniedHandler(OauthExceptionSerializer::exceptionHandler);
        if (!StringUtils.isBlank(rbacProperties.getResourceId())) {
            resources.resourceId(rbacProperties.getResourceId());
        }
    }

    /**
     * <ol>
     * <li>请求经过{@link org.springframework.security.web.FilterChainProxy}到达Servlet</li>
     * <li>FilterChainProxy内有List<@{@link org.springframework.security.web.SecurityFilterChain}>，每个请求只会交给一个SecurityFilterChain</li>
     * <li>SecurityFilterChain内有List<@{@link javax.servlet.Filter}>，对应每个类配置的HttpSecurity</li>
     *
     * <li>{@link org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter}的order是100</li>
     * <li>ResourceServerConfigurerAdapter的order是3，{@link org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration}</li>
     * <li>当匹配相同的url时，ResourceServerConfigurerAdapter的配置生效</li>
     *
     * <i>{@link org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration}优先级更高0</i>
     * </ol>
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
                // 所有资源都交给ResourceServer而不是WebSecurity保护
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
        // 不需要保护的接口，如：/open/**
        if (rbacProperties.getOpenPath() != null && rbacProperties.getOpenPath().length > 0) {
            registry.antMatchers(rbacProperties.getOpenPath()).permitAll();
        }
        // 统一配置某些接口需要的权限，如：接口/admin/**需要权限admin
        if (rbacProperties.getPathAnyAuthorityMapping() != null) {
            Set<Map.Entry<String, String[]>> entries = rbacProperties.getPathAnyAuthorityMapping().entrySet();
            entries.forEach(entry -> registry.antMatchers(entry.getKey()).hasAnyAuthority(entry.getValue()));
        }
        // 方法级别的权限控制可以使用{@link org.springframework.security.access.prepost.PreAuthorize}等注解
        // 其他接口需要通过认证
        registry.anyRequest().authenticated();
    }
}
