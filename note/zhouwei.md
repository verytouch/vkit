# 我都干了些啥



## 1. docker

docker和docker-compose安装在192.168.30.102，这台机器外网出口被限制了，需要找运维配白名单。目前配了有：

* https://registry.npmjs.org/ 
* https://registry.npm.taobao.org/
* https://hub.docker.com/ 
* https://index.docker.io
* https://2e0u7z77.mirror.aliyuncs.com
* https://repo1.maven.org/maven2/
* http://maven.aliyun.com/nexus/content/groups/public/
* https://oss-cn-shenzhen.aliyuncs.com

### 1.1 安装及配置

```shell
# 安装docker
yum remove docker docker-client docker-client-latest docker-common docker-latest docker-latest-logrotate docker-logrotate docker-engine
yum install -y yum-utils device-mapper-persistent-data lvm2
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
yum install docker-ce docker-ce-cli containerd.io

# 配置docker镜像仓库 {"registry-mirrors":["https://reg-mirror.qiniu.com/"]}
vim /etc/docker/daemon.json

# 配置docker远程访问 ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock
vim /usr/lib/systemd/system/docker.service

# 安装docker-compose
curl -L https://get.daocloud.io/docker/compose/releases/download/1.25.1/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
```

### 1.2 注意事项

gitlab和nexu的数据卷是通过docker volume管理的，可以通过docker volume ls 和 docker volume inspect [name]命令查看，请注意不要误清理掉。

| volume        | 容器内路径      | 用途                                       |
| ------------- | --------------- | ------------------------------------------ |
| gitlab_config | /etc/gitlab     | gitlab配置目录                             |
| gitlab_log    | /var/log/gitlab | gitlab日志目录                             |
| gitlab_data   | /var/opt/gitlab | gitlab数据目录，项目代码所在目录，**重要** |
| nexus_data    | /nexus-data     | nexus仓库目录                              |



## 2. gitlab

crm前后端和小程序代码都在该gitlab上，版本为gitlab-ce=13.6.2-ce.0。

gitlab是用docker安装的，gitlab-runner是通过rpm包安装的，安装包可以在192.16.30.104的/app目录下找到。

### 2.1 安装和备份

#### 2.1.1 安装

有时候gitlab容器停止后起不来，删掉容器后重新run一下即可，注意不要清理volume。

```shell
# 创建volume用来映射容器内的目录
docker volume create gitlab_config
docker volume create gitlab_log
docker volume create gitlab_data
# 安装，启用ssh克隆可以添加22端口映射
docker run -d --name gitlab --privileged=true \
	  -v gitlab_config:/etc/gitlab \
	  -v gitlab_log:/var/log/gitlab \
	  -v gitlab_data:/var/opt/gitlab \
	  -p 2080:2080 \
	  gitlab/gitlab-ce:13.6.2-ce.0
	  
# 配置,添加这行 external_url 'http://210.22.20.163:2080'
vi /var/lib/docker/volumes/gitlab_config/_data/gitlab.rb
# 重新加载以下配置
docker exec -it gitlab gitlab-ctl reconfigure
```

#### 2.1.2 备份

官方文档：https://docs.gitlab.com/ee/raketasks/backup_restore.html#uploading-backups-to-a-remote-cloud-storage

备份目录：oss://custom-test/gitlab。

数据备份基于命令gitlab-rake gitlab:backup:create，配置正确会自动上传到阿里OSS。

配置文件gitlab.rb和gitlab-secrets.json也需要备份，通过[ossutil](https://help.aliyun.com/document_detail/207217.html?spm=a2c4g.11186623.6.564.104764d3hNGq8L)上传到阿里OSS。

```shell
# 修改配置 vi /var/lib/docker/volumes/gitlab_config/_data/gitlab.rb
# 备份文件保留3天
gitlab_rails['backup_keep_time'] = 259200
# 自动上传到阿里云oss
gitlab_rails['backup_upload_connection'] = {
  'provider' => 'aliyun',
  'aliyun_accesskey_id' => 'accesskey_id',
  'aliyun_accesskey_secret' => 'accesskey_secret',
  'aliyun_oss_endpoint' => 'endpoint',
  'aliyun_oss_bucket' => 'bucket',
  'aliyun_oss_location' => 'shenzhen'
}
gitlab_rails['backup_upload_remote_directory'] = 'gitlab'
# 使配置文件生效
docker exec gitlab gitlab-ctl reconfigure

# 定时备份，vi /etc/crontab  
00 01 * * * root sh /app/gitlab_backup.sh
# gitlab_backup.sh内容
cd /var/lib/docker/volumes/gitlab_config/_data
/app/ossutil cp gitlab.rb oss://custom-test/gitlab/gitlab.rb.$(date +%s)
/app/ossutil cp gitlab-secrets.json oss://custom-test/gitlab/gitlab-secrets.json.$(date +%s)
docker exec gitlab gitlab-rake gitlab:backup:create SKIP=artifacts
```

#### 2.1.3 恢复和迁移

首先要保证gitlab版本为13.6.2-ce.0，把备份的tar文件和另外两个配置文件放到对应目录，执行以下命令：

```shell
docker exec -it gitlab chmod 777 /var/opt/gitlab/backups/1234_gitlab_backup.tar
docker exec -it gitlab gitlab-rake gitlab:backup:restore BACKUP=1234
# 不执行以下命令部分页面会报500
docker exec -it gitlab gitlab-ctl reconfigure
```



### 2.2 gitlab-runner

安装gitlab-runner之前需要安装新版本的git，安装包可以在192.16.30.104的/app目录下找到，参考安装方式：

```shell
# 卸载老版本git
yum remove git
# 安装依赖
yum -y install curl-devel expat-devel gettext-devel openssl-devel zlib-devel gcc perl-ExtUtils-MakeMaker
# 通过源码安装git
tar zxf git-2.25.1.tar.gz cd git-2.25.1
make prefix=/usr/local/git all
make prefix=/usr/local/git install
# 添加环境变量: /usr/local/git/bin
vi /etc/bashrc 
source /etc/bashrc
git --version

# 通过rpm安装runner
rpm -ivh gitlab-runner-13.6.0-1.x86_64.rpm --nodeps --force
# 注册，executor选择shell
/usr/bin/gitlab-runner register
# 默认为gitlab-runner用户，测试环境可以切换到root用户
/usr/bin/gitlab-runner uninstall
/usr/bin/gitlab-runner install --working-directory /home/gitlab-runner --user root
/usr/bin/gitlab-runner restart
```

已注册的runner可以在Admin Area -> Overview -> Runners处维护（需要root账号登录gitlab）。

其中specific类型的runner需要分配了某个项目之后，那个项目才能使用该runner。这里是部分信息：

| 机器           | 用户          | 类型     | 用途                  |
| -------------- | ------------- | -------- | --------------------- |
| 192.168.30.102 | root          | shared   | 构建                  |
| 192.168.30.104 | root          | specific | 发布到测试环境104节点 |
| 192.168.30.101 | gitlab-runner | shared   | 发布到测试环境101节点 |
| 39.100.118.173 | gitlab-runner | specific | 发布到生产环境        |

注意，gitlab-runner用户需要以下权限：

| 目录/命令      | 权限 | 用途                   | 参考授权                                                     |
| -------------- | ---- | ---------------------- | ------------------------------------------------------------ |
| /opt/log/crm   | 读写 | 日志目录               | chmod -R 777 /opt/log/crm                                    |
| /app/crm       | 读写 | 应用目录               | chmod -R 777 /app/crm                                        |
| netstat、xargs | 执行 | 根据端口查询、停止应用 | /etc/sudoer添加一行：gitlab-runner ALL=(ALL)   NOPASSWD:/usr/bin/netstat,/usr/bin/xargs |

### 2.3 CI脚本

语法可以参考：http://192.168.30.102:2080/help/ci/yaml/README.md，思路如下：

![](http://192.168.30.104:2587/doc/crm/crm-gitlab.svg)

生产环境发布时，上图红色数字标识的三个步骤需要手动触发。即：先发布系统升级页面，再发布后端应用（包括sql），最后发布正式页面。

脚本说明：

​		项目根目录下的.gitlab-ci.yml文件为构建入口文件，定义了通用的variables、stages、构建job和抽象job。具体发布应用服务的job写在各自模块下的.gitlab-ci.yml文件中，需要被入口文件inlcude进来。

​		抽象job是由点开头的job，其作用为抽取通用的定义，别的job可以用extends继承达到复用目的。如测试环境构建crm-api时，继承了.env_test + .app_api + .script_download_and_restart。

| 抽象job名称                  | 抽象属性                           | 作用           |
| ---------------------------- | ---------------------------------- | -------------- |
| .env_test                    | 触发分支、应用启动参数             | 测试环境       |
| .env_production              | 触发分支、应用启动参数             | 生产环境       |
| .app_*                       | 触发文件、各应用的名称、路径、端口 | 某个应用       |
| .script_download             | 脚本                               | 下载jar包      |
| .script_restart              | 脚本                               | 重启应用       |
| .script_download_and_restart | 脚本                               | 下载并重启应用 |



## 3. nexus

nexus内网地址为http://192.168.30.102:2081，主要用作maven私库。

里面有crm的jar包和smart-doc-crm的jar包，通过gitlab自动构建时该私库必不可少。

### 3.1 通过docker安装

```shell
docker volume create nexus_data
docker run -d --name nexus3 --restart=always \
    -v nexus_data:/nexus-data \
    -p 2081:8081 \
    sonatype/nexus3:3.29.0
```

### 3.2 仓库说明

| 名称          | 类型      | 作用                                                         |
| ------------- | --------- | ------------------------------------------------------------ |
| maven-central | maven     | 中央仓库代理                                                 |
| maven-aliyun  | maven     | 阿里中央仓库代理                                             |
| vstecs-crm    | maven     | ***crm项目构建时配置了此仓库***，有各版本的jar包             |
| maven-3rd     | maven     | 三方库，smart-doc-crm构建时配置了此仓库                      |
| maven-public  | maven     | 组合仓库，组合了以上仓库，**crm项目的repositories配置了此仓库** |
| 其他          | maven/npm | 如maven-release、maven-snapshot仓库和npm-*仓库等，未被正式使用 |

### 3.3 定时任务

Administration -> System -> Tasks里面有两个定时任务，每天定时清理blob，不然会迅速占满磁盘，导致构建失败。



## 4. smart-doc-crm

用来生成CRM接口文档，可生成html文档、markdown文档、postmant接口集合等。

修改自开源项目[smart-doc](https://gitee.com/smart-doc-team/smart-doc)，其原理为扫描源码上的注释，修改后的代码放到了gitlab上。

### 4.1 AllInOne.html

html文档模板，修改内容有：

* 添加接口详细说明
* 删除回参说明
* 删除版本字段
* 外部css文件内部化

### 4.2 ApiConfig

接口文档配置类，修改内容有：

* serverUrl拆分为systemServerUrl、customServerUrl、apiServerUrl和gatewayServerUrl
* 添加loginDoc，可手工构建登录接口以生成文档

### 4.3 ItemEvent

新建的类，主要用于postman请求登录接口完成后执行脚本，把token放入环境变量crmToken中。



## 5. crm-kit

spring-boot-starter风格的组件集合，里面封装了各类通用组件，默认不启用。每个组件一个目录，目录下包含以下部分：

* XXXConfigurationProperties：默认配置类，可覆盖
* XXXKit：装载到ApplicationContext中的Bean
* XXXAutoConfiguration：自动配置类，用来初始化Kit

其中，XXXAutoConfiguration应该通过spring.factories引入。

### 5.1 组件

| 名称       | Bean                                              | 作用                          |
| ---------- | ------------------------------------------------- | ----------------------------- |
| bis        | BisAPIKit                                         | bis接口                       |
| erp        | ErpAPIKit                                         | erp接口                       |
| idata      | IDataAPIKit                                       | idata接口                     |
| qichacha   | QiChaChaAPIKit                                    | 企查查接口                    |
| tianyancha | TianYanChaAPIKit                                  | 天眼查接口                    |
| email      | EmailKit                                          | 邮件发送                      |
| sms        | SmsKit                                            | 短信发送（阿里、信信客）      |
| threadpool | ThreadPoolMonitorController、ThreadPoolMonitorKit | 线程池信息搜集定时器、查询API |

### 5.2 threadpool接口

threadpool定时器默认在system和custome开启，接口如下：

| 路径                                      | 作用                   | 影响范围 | 操作权限                  |
| ----------------------------------------- | ---------------------- | -------- | ------------------------- |
| /api-**/monitor/threadPool/getPools       | 获取所有应用的线程池   | 所有应用 | /api-custom：system_debug |
| /api-**/monitor/threadPool/getLogList     | 查询线程池状态         | 所有应用 | /api-custom：system_debug |
| /api-**/monitor/threadPool/collectLog     | 收集一次线程池状态     | 当前应用 | 暂未授权                  |
| /api-**/monitor/threadPool/startTimer     | 开始定时收集线程池状态 | 当前应用 | 暂未授权                  |
| /api-**/monitor/threadPool/stopTimer      | 停止定时收集线程池状态 | 当前应用 | 暂未授权                  |
| /api-**/monitor/threadPool/getTimerStatus | 查询定时器状态         | 当前应用 | 暂未授权                  |



## 6. crm-hystrix

这也是一个spring-boot-starter，用来作服务的熔断和降级，因为超时等原因使用一段时间后暂停使用。实现的功能如下：

### 6.1 全局fallback

* **FeignHystrixExtensionAutoConfiguration**中注入GlobalFailFastHystrixTargeter覆盖默认targeter
* **GlobalFailFastHystrixTargeter**在FeignClient没有定义fallback和fallbackFactory时使用**FailFastFallbackFactory**

### 6.2 默认配置

默认配置位置：resources/application-hystrix-default.yml。

使用方式：修改应用项目的配置文件（默认配置也可被该配置文件重写），加入以下代码即可

```properties
spring.profiles.include=hystrix-default
```

### 6.3  token问题

hystrix的默认隔离策略为线程隔离，这种策略会导致token丢失。解决方式以下二选一：

* 重写线程并发策略：配置hystrix.extension.requestAttributeStrategy=true时，重写线程并发策略以传递RequestAttribute
* 配置隔离策略为信号量：鉴于官方推荐使用线程隔离方式，本starter默认使用第一种方式



## 7. crm-xxl-job

定时任务模块，来源于开源项目[xxl-job](https://www.xuxueli.com/xxl-job/)，分为调度中心和执行器两个部分。

调度中心和执行器分开部署，执行器里定义任务代码，需要向调度中心注册，由调度中心管理和调度。

### 7.1 调度中心

仅修改了配置文件，如日志目录、端口、多环境配置等。

调度中心需要连接数据库，sql脚本忘了保存，可以从测试环境得到或[查看新版本](https://gitee.com/xuxueli0323/xxl-job/blob/master/doc/db/tables_xxl_job.sql)。

执行器定义业务代码之后，需要在调度中心管理页面新建任务、填写执行参数并且启动之后才能定时执行。

测试环境地址：http://192.168.30.104:7022/xxl-job-admin/。

### 7.2 执行器

执行器同时向调度中心和eureka注册，接收到调度中心调度后，任务代码通过feign调用具体的业务代码。

这里只使用了Bean模式，即某个ApplicationContext中的bean，它的每个被@XxlJob注解的方法都可作为"任务"被调度中心管理和调度。

注意***任务方法除了方法名称外要完全一致***。



## 8. 小程序

每个小程序都应该在调用业务接口之前获取用户的openid

SysAppEnum中配置，其中属性说明如下：

| 名称          | 说明                                                         |
| ------------- | ------------------------------------------------------------ |
| appId         | 在微信公众台申请到的appId                                    |
| appSecret     | 在微信公众台申请到的appSecret，小程序登获取openid和session_key时需要用到 |
| appName       | 小程序名称                                                   |
| userTable     | 小程序用户表，一键登录和通过openid放刷需要用到               |
| urlPrefix     | 该小程序下的接口前缀，免登录和token隔离需要用到              |
| loginRequired | 为true时需要登录crm系统，否则不需要，放开urlPrefix前缀开头的接口 |

### 8.1 免登录配置

RbacService.hasPermission调用了SysAppEnum.loginNotRequired方法来判断是否是免登录接口，是则放行：

```java
// 通过url判断是否时某个小程序的接口，如果是且该小程序配置了免登录，放开该接口
public static boolean loginNotRequired(String url) {
    SysAppEnum appEnum = fromUrlPrefix(url);
    return appEnum != null && !appEnum.loginRequired;
}

public static SysAppEnum fromUrlPrefix(String url) {
    if (StringUtil.isEmpty(url)) {
        return null;
    }
    SysAppEnum[] values = SysAppEnum.values();
    for (SysAppEnum value : values) {
        if (url.startsWith(value.getUrlPrefix())) {
            return value;
        }
    }
    return null;
}
```

### 8.2 通过openid防刷

crm-api添加了com.vstecs.crm.api.wechat.OpenidFilter，会校验请求header中的openid，openid不正确时该请求会被拦截：

```java
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
    throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    String url = "/api-open" + httpServletRequest.getRequestURI();
    SysAppEnum appEnum = SysAppEnum.fromUrlPrefix(url);
    // 不需要校验的app和登录接口，放行
    if (!CHECK_OPENID_APP_SET.contains(appEnum) || url.endsWith("/login")) {
        chain.doFilter(request, response);
        return;
    }
    // 未配置用户表，放行
    if (appEnum == null || appEnum.getUserTable() == null) {
        chain.doFilter(request, response);
        return;
    }
    String openid = httpServletRequest.getHeader("openid");
    // openid为空，拦截
    if (StringUtil.isEmpty(openid)) {
        error(response, "openid不正确");
        return;
    }
    // openid不正确，拦截
    String sql = "select count(*) from %s where OPEN_ID = ?";
    Integer openidCount = jdbcTemplate.queryForObject(String.format(sql, appEnum.getUserTable()), Integer.class, openid);
    if (openidCount == null || openidCount < 1) {
        error(response, "openid不正确");
        return;
    }

    chain.doFilter(request, response);
}
```

### 8.3 微信手机号一键登录

处理逻辑：

1. 小程序调用wx.login()方法获得code，调用crm-api的登录接口
2. crm-api调用微信服务器的jscode2session接口，更新数据库该小程序用户表中的open_id和session_key
3. 小程序用户点击一键登录按钮并授权小程序获取手机号
4. 小程序获得加密数据，调用crm-zuul的loginByWechatMobile接口
5. crm-zuul通过app_id和open_id查询数据库中的session_key，然后解密得到手机号
6. crm-zuul根据手机号查询用户，查询到即视为登录成功，返回token和权限信息

相关代码：

1. com.vstecs.crm.api.wechat.custom.controller.WechatCustomController.updateSessionKey
2. com.vstecs.crm.zuul.authentication.wehcatMobile

### 8.4 多端token隔离

该功能也是通过RbacService.hasPermission实现，它调用了RbacService.isRightTokenForUri方法。

逻辑为：根据token获取app，再判断app配置的urlPrefix和该次请求的url是否匹配，不匹配则认为无权访问：

```java
private boolean isRightTokenForUri(String token, String uri) {
    String appId = jedisUtil.get(RedisConstant.PRE_LOGIN_APP + token);
    if ("website".equals(appId)) {
        // 网页端的token，可以调这些应用之外所有的接口
        return SysAppEnum.notInUrlPrefixes(uri);
    }
    SysAppEnum appEnum = SysAppEnum.fromAppId(appId);
    // 没有查到appId，认为没有限制，交给别的认证逻辑
    if (appEnum == null) {
        return true;
    }
    // 其他应用的token只能用于该应用的接口
    return uri.startsWith(appEnum.getUrlPrefix());
} 
```



## 9. crm-admin

[spring-boot-admin](https://codecentric.github.io/spring-boot-admin/2.0.1/)项目，分为服务端和客户端，客户端作为被监控的项目向服务端注册。

其原理是通过客户端暴露的[spring-boot-actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-endpoints)端点，

测试环境地址：http://192.168.30.104:7033/。

### 9.1 客户端注册

* 通过客户端注册

  客户端引入spring-boot-admin-starter-client包，添加spring.boot.admin.client和management相关配置即可。

  crm-xxl-job-admin采用此方式注册。

* 通过注册中心注册

  服务端和客户端同时向eureka注册，客户端添加management相关配置即可。

  其他应用采用此方式注册。

### 9.2 客户端状态通知

生产环境配置了notify.feishu.url，当客户端状态变更时，会将消息推送到飞书【服务状态提醒】群。

```java
@Component
@Slf4j
public class StatusChangeNotifier extends AbstractStatusChangeNotifier {

    @Value("${notify.feishu.url:}")
    private String feiShuUrl;

    public StatusChangeNotifier(InstanceRepository repository) {
        super(repository);
    }

    /**
     * 当配置了飞书webhook地址时，客户端状态变更消息推送到该地址
     */
    @Override
    protected boolean shouldNotify(InstanceEvent event, Instance instance) {
        if (event instanceof InstanceStatusChangedEvent) {
            log.info("{}", getNotifyMessage((InstanceStatusChangedEvent) event, instance));
            return StringUtils.hasText(feiShuUrl);
        }
        return false;
    }

    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        // 发送消息到飞书
        return Mono.fromRunnable(() -> FeiShuMessageSender.sendText(feiShuUrl, getNotifyMessage((InstanceStatusChangedEvent) event, instance)));
    }

    private StatusChangeMessage getNotifyMessage(InstanceStatusChangedEvent event, Instance instance) {
        Registration registration = instance.getRegistration();
        StatusChangeMessage message = new StatusChangeMessage();
        message.setInstanceId(instance.getId().getValue());
        message.setAppName(registration.getName().toUpperCase());
        message.setServiceUrl(registration.getServiceUrl());
        message.setFromStatus(getLastStatus(instance.getId()));
        message.setToStatus(event.getStatusInfo().getStatus());
        return message;
    }

}
```

