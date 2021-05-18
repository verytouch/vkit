package top.verytouch.vkit.samples.oauth2.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    /**
     * <ol>
     * <li>请求 -> {@link org.springframework.security.web.FilterChainProxy} -> Servlet</li>
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
                .antMatchers("/user/**", "/api/**")
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();
    }
}
