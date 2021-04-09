## 一、注解

* SpringBootApplication
* EnableAutoConfiguration
* SpringBootConfiguration
* Configuration
* ComponentScan
* AutoConfigurationPackage
* Import



## 二、启动过程

### 1. new SpringApplication()

* deduceWebApplicationType：根据系统中是否存在相关Class判断应用类型，通常为SERVlET
* **setInitializers**：使用SpringFactoriesLoader加载spring.factories下的ApplicationContextInitializer
* **setListeners**：加载spring.factories下的ApplicationListener
* deduceMainApplicationClass：new RuntimeException().getStackTrace()判断main方法所在类

### 2. run()

* StopWatch：开启一个计时器

* configureHeadlessProperty：配置java.awt.headless=true，模拟外设

* getRunListeners：加载spring.factories下的SpringApplicationRunListener，*listeners.starting*

* **prepareEnvironment**：加载环境、系统环境、jvm参数、配置文件和命令行参数等，*listeners.environmentPrepared*

  如果是spring-cloud项目，其BootstrapApplicationListener监听了environmentPrepared事件，内部会启动一个父上下文，再执行一次run方法

* configureIgnoreBeanInfo：配置spring.beaninfo.ignore

* printBanner：打印banner

* **createApplicationContext**：根据webApplicationType加载并实例化对应的ConfigurableApplicationContext对象

* exceptionReporters：加载spring.factories下的SpringBootExceptionReporter

* **prepareContext**
  
  * setEnvironment：关联环境
  * postProcessApplicationContext：配置Bean生成器以及资源加载器
  
  * applyInitializers：使用Initializers初始化ApplicationContext，*listeners.contextPrepared*
  * registerSingleton：注册springApplicationArguments和springBootBanner
  * getAllSources：获取要加载的资源，一般为主类的class对象
  * load：注册启动类BeanDefinition，*listeners.contextLoaded*
* **refreshContext：加载bean，启动web容器**
  
  * prepareRefresh，一些准备工作
  * **obtainFreshBeanFactory**，获取beanFactory，原生的spring会在这里加载bean的定义
  * prepareBeanFactory，beanFactory设置一些属性，注册环境和后置处理器
  * postProcessBeanFactory，空方法，留给子类实现
  * **invokeBeanFactoryPostProcessors**：扫描业务Bean，加载为BeanDefinition
  * registerBeanPostProcessors
  * initMessageSource：国际化
  * initApplicationEventMulticaster
  * **onRefresh**：创建web容器，默认为tomcat
  * registerListeners：注册监听器
  * **finishBeanFactoryInitialization**：实例化业务并注册Bean
  * finishRefresh：启动web容器
* 注册shutdownhook
  
* afterRefresh：空方法

* 停止计时器，*listeners.started*

* **callRunners**：调用所有runner方法，*listeners.running*



## 三、自定义starter



## 四、actuator