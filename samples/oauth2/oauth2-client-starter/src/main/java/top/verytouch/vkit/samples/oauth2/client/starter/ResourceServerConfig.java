package top.verytouch.vkit.samples.oauth2.client.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/resource/**")
                .authorizeRequests()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenStore(new JwtTokenStore(jwtAccessTokenConverter()));
    }

    @Bean
    public ClientInfoEndpoint clientInfoEndpoint() {
        return new ClientInfoEndpoint(environment);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(environment.getProperty("security.oauth2.resource.jwt.key-value"));
        return converter;
    }

}
