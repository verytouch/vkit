### 项目目的
1. feign+hystrix使用
2. HystrixCommand注解使用
3. 原生HystrixCommand、HystrixObservableCommand
4. hystrix dashboard使用

### 项目结构
1. hystrix-api feign-client及fallback
2. hystrix-eureka 注册中心
3. hystrix-service feign服务提供方
4. hystrix-web 主要测试项目
5. test.http 接口测试

### What Is Hystrix For?
1. 当我们的系统需要借助第三方jar包访问外部依赖时（特别是通过网络），Hystrix用来在延迟控制与错误异常方面给予保护。
2. 在复杂的分布式系统中阻止级联失败（雪崩）。
3. 快速失败并且迅速恢复。
4. 在可能的情况下，退回、优雅的降级。
5. 启动准实时的监控、告警并且可动态操控。