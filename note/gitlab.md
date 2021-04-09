版本为gitlab-ce=13.6.2-ce.0。

gitlab是用docker安装的，gitlab-runner是通过rpm包安装的，安装包可以在192.16.30.104的/app目录下找到。

### 1 安装和备份

#### 1.1 安装

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

#### 1.2 备份

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

#### 1.3 恢复和迁移

首先要保证gitlab版本为13.6.2-ce.0，把备份的tar文件和另外两个配置文件放到对应目录，执行以下命令：

```shell
docker exec -it gitlab chmod 777 /var/opt/gitlab/backups/1234_gitlab_backup.tar
docker exec -it gitlab gitlab-rake gitlab:backup:restore BACKUP=1234
# 不执行以下命令部分页面会报500
docker exec -it gitlab gitlab-ctl reconfigure
```



### 2 gitlab-runner

安装gitlab-runner之前需要安装新版本的git。参考安装方式：

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
# vi /etc/gitlab-runner/config.toml 修改并发job数 concurrent = 10
# 重启
/usr/bin/gitlab-runner restart
```

已注册的runner可以在Admin Area -> Overview -> Runners处维护（需要root账号登录gitlab）。

注意，gitlab-runner用户需要以下权限：

| 目录/命令      | 权限 | 用途                   | 参考授权                                                     |
| -------------- | ---- | ---------------------- | ------------------------------------------------------------ |
| /opt/log/crm   | 读写 | 日志目录               | chmod -R 777 /opt/log/crm                                    |
| /app/crm       | 读写 | 应用目录               | chmod -R 777 /app/crm                                        |
| netstat、xargs | 执行 | 根据端口查询、停止应用 | /etc/sudoer添加一行：gitlab-runner ALL=(ALL)   NOPASSWD:/usr/bin/netstat,/usr/bin/xargs |



### 3 CI脚本

```
# 运行gitlab-runner的用户需要以下权限：
# 目录：/app/crm、/opt/log
# 命令：sudo netstat -nlp、sudo xargs kill -9
#
# 发布参数：
# APP_PATH    应用路径，如：./crm-api
# APP_NAME    应用名称，如：crm-api
# CRM_VERSION 版本号，如：1.0
# PORT        端口，如：8080
# RUN_PARAMS  运行参数，如：--spring.profiles.active=test
# NEXUS_CRM   私库地址
# NEXUS_USER  私库用户名
# NEXUS_PASS  私库密码

variables:
  NEXUS_CRM: 仓库中app的父目录
  NEXUS_USER: 用户名
  NEXUS_PASS: 密码
  CRM_VERSION: 1.0

# 构建、发布到节点1、发布到节点2
stages:
  - build
  - prepare
  - deploy_node1
  - deploy_node2

# 在102上打包
build:
  stage: build
  only:
    # 仅某些分支触发CI
    refs:
      - master
      - test
      - product
    # 仅某些文件修改触发CI
    changes:
      - pom.xml
      - crm-*/**/*
  tags:
    - 102-runner
  script:
    - mvn clean deploy -Dmaven.repo.local=/opt/repository -Dmaven.test.skip=true

# 发布环境
.env_test:
  only:
    refs:
      - master
      - test
  variables:
    RUN_PARAMS: --spring.profiles.active=test

.env_product:
  when: manual
  only:
    refs:
      - product
  variables:
    RUN_PARAMS: --spring.profiles.active=prod

# 需要发布的应用
.app_eureka:
  only:
    changes:
      - crm-eureka/**/*
  variables:
    APP_PATH: ./crm-eureka
    IMAGE_NAME: crm-eureka
    APP_NAME: crm-eureka
    PORT: 8010

.app_zuul:
  only:
    changes:
      - crm-zuul/**/*
  variables:
    APP_PATH: ./crm-zuul
    IMAGE_NAME: crm-zuul
    APP_NAME: crm-zuul
    PORT: 8020

.app_api:
  only:
    changes:
      - crm-api/**/*
      - crm-common/**/*
  variables:
    APP_PATH: ./crm-api
    IMAGE_NAME: crm-api
    APP_NAME: crm-api
    PORT: 8030

# 发布脚本
# /app/crm/*.jar是运行中的应用
# /app/crm/${CRM_VERSION}/*.jar是备份应用，每个版本号一个备份，版本号相同时直接覆盖

# 下载jar包
.script_download:
  script: &downloadApp
    - echo "应用下载中..."
    - echo "APP=${APP_NAME}, VERSION=${CRM_VERSION}"
    - if [ ! -d /app/crm/${CRM_VERSION} ]; then mkdir -p /app/crm/${CRM_VERSION}; fi
    - if [ -f /app/crm/${CRM_VERSION}/${APP_NAME}.jar ]; then rm -f /app/crm/${CRM_VERSION}/${APP_NAME}.jar; fi
    - wget --user $NEXUS_USER --password $NEXUS_PASS -O /app/crm/${CRM_VERSION}/${APP_NAME}.jar ${NEXUS_CRM}/${APP_NAME}/${CRM_VERSION}/${APP_NAME}-${CRM_VERSION}.jar

# 重启应用
.script_restart:
  script: &restartApp
    - echo "应用重启中..."
    - echo "APP=${APP_NAME}, VERSION=${CRM_VERSION}, PARAMS=${RUN_PARAMS}"
    - if [ "$(sudo netstat -nlp | grep $PORT)" ]; then sudo netstat -nlp | grep $PORT | awk '{print $7}' | awk -F "/" '{ print $1 }' | sudo xargs kill -9; fi
    - if [ -f /app/crm/$APP_NAME.jar ]; then rm -f /app/crm/$APP_NAME.jar; fi
    - cp /app/crm/${CRM_VERSION}/${APP_NAME}.jar /app/crm/${APP_NAME}.jar
    - nohup java -jar /app/crm/$APP_NAME.jar $RUN_PARAMS > /app/crm/$APP_NAME.out 2>&1 &

# 下载并重启，适用于内网环境
.script_download_and_restart:
  script:
    - *downloadApp
    - *restartApp

.delayed:
  when: delayed
  start_in: 10 seconds

# 具体发布的job在各自模块，可选择上面的环境、应用、脚本继承
# 可以注释掉某行，这样就不会触发该模块的CI了
include:
  - local: /crm-eureka/.gitlab-ci.yml
  - local: /crm-zuul/.gitlab-ci.yml
  - local: /crm-api/.gitlab-ci.yml

```

