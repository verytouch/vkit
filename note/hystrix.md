## 一、feign下源码

* EnableFeignClients -> FeignClientsRegistrar -> registerDefaultConfiguration/registerFeignClients

* FeignClientsConfiguration -> Feign.Builder

* FeignAutoConfiguration -> HystrixTargeter

  

## 二、feign下使用

```yaml
# 防止经过路由转发的请求超时
# 配置类DefaultClientConfigImpl
ribbon:
  ReadTimeout: 60000
  ConnectTimeout: 60000
  # 对所有操作请求都进行重试，默认false，只对Get重试
  OkToRetryOnAllOperations: false
  # 同实例重试次数，默认0
  MaxAutoRetries: 0
  # 可用实例重试次数，默认1
  MaxAutoRetriesNextServer: 0
# 开启断路器
feign:
  hystrix:
	enabled: true
# 断路器超时时间设置参考：(1 + MaxAutoRetries + MaxAutoRetriesNextServer) * ReadTimeout
# 配置类HystrixCommandProperties
hystrix:
  command:
	# default全局有效，service-id指定应用有效
	default:
	  execution:
		timeout:
		  # 如果enabled设置为false，则请求超时交给ribbon控制
		  enabled: true
		isolation:
		  thread:
			# 超时时间，默认1000
			timeoutInMilliseconds: 3000
	  circuitBreaker:
		# 断路器请求量阈值，默认20
		requestVolumeThreshold: 20
		# 断路器错误百分比阈值，默认50
		errorThresholdPercentage: 50
		# 断路器打开持续时间，打开后拒绝所有请求，默认5000
		sleepWindowInMilliseconds: 5000
```



## 三、仪表盘

```yaml
cloud:
  refresh:
    efreshable: none
management:
  endpoint:
	web:
	  exposure:
		include: 'hystrix.stream'
```

