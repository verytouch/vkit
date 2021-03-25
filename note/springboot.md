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

* **prepareEnvironment**：加载环境变量、配置文件和命令行参数等，*listeners.environmentPrepared*

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
  * load：createBeanDefinitionLoader并加载注册，*listeners.contextLoaded*

* **refreshContext：加载bean，启动web容器**
  * prepareRefresh，校验必须配置等
  * obtainFreshBeanFactory，获取beanFactory
  * prepareBeanFactory
  * postProcessBeanFactory
  * invokeBeanFactoryPostProcessors
  * registerBeanPostProcessors
  * initMessageSource
  * initApplicationEventMulticaster
  * **onRefresh**：启动web容器，默认为tomcat
  * registerListeners
  * finishBeanFactoryInitialization
  * finishRefresh
  * 注册shutdownhook

* afterRefresh：空方法
* 停止计时器，*listeners.started*
* **callRunners**：调用所有runner方法，*listeners.running*



## 三、自定义starter



## 四、actuator