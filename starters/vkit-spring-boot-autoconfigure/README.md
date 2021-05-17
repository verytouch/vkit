### 1.引入依赖

```xml

<dependency>
    <groupId>com.verytouch.vkit</groupId>
    <artifactId>rbac-spring-boot-starter</artifactId>
    <version>2.2.0</version>
</dependency>
```

### 2.添加配置

```yaml
vkit:
  rbac:
    parameter-aes-key: 0123456789123456
    jwt-sing-key: 123456
```

### 3.注入Bean

```java

@Configuration
public class BeanConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用于加载户信息，通常从数据库查询。必须注入
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> new User()
                .setId(1)
                .setDepartmentId(new Random().nextInt())
                .setUsername(username)
                .setPassword(passwordEncoder.encode("123456"))
                .setRoles(Stream.of("ADMIN", "TEST").collect(Collectors.toSet()));
    }

    /**
     * 用于查询客户端信息，必须注入
     */
    @Bean
    public AnotherClientDetailsService anotherClientDetailsService() {
        return s -> {
            BaseClientDetails any = new BaseClientDetails();
            any.setClientId(s);
            any.setClientSecret(passwordEncoder.encode("123456"));
            any.setAuthorizedGrantTypes(Stream.of("captcha", "password", "refresh_token").collect(Collectors.toList()));
            any.setAuthorities(new ArrayList<>());
            any.setScope(Collections.singletonList("all"));
            return any;
        };
    }

    /**
     * 用于增强jwt，可选
     */
    @Bean
    public JwtUserDetailsTokenEnhancer jwtUserDetailsTokenEnhancer() {
        return userDetails -> {
            User user = Assert.instanceOf(userDetails, User.class, "userDetails class not right");
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("departmentId", user.getDepartmentId());
            map.put("roles", user.getRoles());
            return map;
        };
    }

    /**
     * 401/404等非Controller抛出的异常处理，可选
     */
    @Bean
    public BasicErrorController basicErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties) {
        return new ResponseErrorController(errorAttributes, serverProperties.getError());
    }

    /**
     * 其他一些默认的可覆盖的bean，参考com.verytouch.vkit.rabc.RbacAutoConfiguration
     */

}
```