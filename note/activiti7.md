## 一、整合SpringBoot

### 1、pom文件引入依赖

```xml
<dependency>
    <groupId>org.activiti</groupId>
    <artifactId>activiti-spring-boot-starter</artifactId>
    <version>7.0.0.SR1</version>
    <exclusions>
        <exclusion>
            <groupId>org.activiti.core.common</groupId>
            <artifactId>activiti-spring-identity</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 2、配置Bean

```java
package com.example.config;

import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class ActConfig {

    @Bean
    public ProcessEngine processEngine(ProcessEngineConfiguration processEngineConfiguration) {
        return processEngineConfiguration.buildProcessEngine();
    }

    @Bean
    public SpringProcessEngineConfiguration processEngineConfiguration(
            ThreadPoolTaskExecutor workFlowTaskExecutor,
            UserGroupManager userGroupManager,
            DataSource dataSource,
            PlatformTransactionManager transactionManager) {
        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
        configuration.setDataSource(dataSource);
        configuration.setTransactionManager(transactionManager);
        SpringAsyncExecutor asyncExecutor = new SpringAsyncExecutor();
        asyncExecutor.setTaskExecutor(workFlowTaskExecutor);
        configuration.setAsyncExecutor(asyncExecutor);

        configuration.setDatabaseSchemaUpdate("true");
        configuration.setUserGroupManager(userGroupManager);
        configuration.setHistoryLevel(HistoryLevel.FULL);
        configuration.setDbHistoryUsed(true);
        return configuration;
    }

    @Bean
    public ThreadPoolTaskExecutor workFlowTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(10);
        executor.setKeepAliveSeconds(5*60);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("workFlowTaskExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean
    public UserGroupManager userGroupManager() {
        return new UserGroupManager() {
            private final List<String> GROUPS = Stream.of("销售部", "人事部", "财务部", "董事会").collect(Collectors.toList());
            private final List<String> ROLES = Stream.of("销售", "人事", "财务", "董事").collect(Collectors.toList());
            private final List<String> USERS = Stream.of("销售老张", "销售李经理", "人事老王", "财务老赵", "董事钱董").collect(Collectors.toList());

            @Override
            public List<String> getUserGroups(String user) {
                return GROUPS.stream().filter(g -> g.startsWith(user.substring(0, 2))).collect(Collectors.toList());
            }

            @Override
            public List<String> getUserRoles(String user) {
                return ROLES.stream().filter(user::contains).collect(Collectors.toList());
            }

            @Override
            public List<String> getGroups() {
                return GROUPS;
            }

            @Override
            public List<String> getUsers() {
                return USERS;
            }
        };
    }
}

```

### 3、application.yml

```yaml
server:
  port: 80

spring:
  thymeleaf:
    cache: false

  datasource:
    url: jdbc:mysql://localhost:3306/activity7?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false&allowMultiQueries=true
    username: root
    password: root
    driver-class-name: com.mysql.jdbc.Driver

  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
```

##  二、API

### 1、ProcessEngine

### 2、RepositoryService

### 3、RuntimeService

### 4、TaskService

### 5、HistoryService

## 三、数据库

| 表名              | 作用               |
| ----------------- | ------------------ |
| act_ge_bytearray  | 流程部署资源文件表 |
| act_re_deployment | 流程部署表         |
| act_re_procdef    | 流程定义表         |

## 四、BPMNJS