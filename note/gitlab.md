# gitlab

## 一、gitlab安装
```shell
docker volume create gitlab_log
docker volume create gitlab_data
docker volume create gitlab_config
docker run -d --name gitlab --privileged=true \
  -v gitlab_config:/etc/gitlab \
  -v gitlab_log:/var/log/gitlab \
  -v gitlab_data:/var/opt/gitlab \
  -p 2080:2080 \
  -p 22:22 \
  gitlab/gitlab-ce

vi /var/lib/docker/volumes/gitlab_config/_data/gitlab.rb
# 添加这行 external_url 'http://192.168.30.102:2080'
docker restart gitlab
```

## 二、gitlab-runner安装
1. docker-runner
```shell
docker volume create gitlab-runner-config
docker run -it --name gitlab-runner \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v gitlab_runner_config:/etc/gitlab-runner \
  gitlab/gitlab-runner register
# 此时要求输入gitlab的url和token等信息
# url从gitlab页面找
# token从gitlab页面找
# executor: docker
# image: docker:stable
docker start gitlab-runner

# 配置文件在 /var/lib/docker/volumes/gitlab_runner_config/_data
# 如果ci失败，gitlab页面看不到什么错误信息，下面可以帮助调试
docker exec -it gitlab-runner bash
gitlab-runner --debu run
# 此时push一下，看是否接受到jobs，或者打印错误信息
```

2. shell-runner
```shell
wget --content-disposition https://packages.gitlab.com/runner/gitlab-runner/packages/ol/7/gitlab-runner-13.6.0-1.x86_64.rpm/download.rpm
rpm -ivh gitlab-runner-13.6.0-1.x86_64.rpm
/usr/bin/gitlab-runner register
# 此时要求输入gitlab的url和token等信息
# url从gitlab页面找
# token从gitlab页面找
# executor: shell

# 默认用户为gitlab-runner，如果runner没权限执行脚本，可以重新安装指定用户
/usr/bin/gitlab-runner uninstall
/usr/bin/gitlab-runner install --working-directory /home/gitlab-runner --user root
/usr/bin/gitlab-runner restart
```

### 三、.gitlab-ci.yml编写
```yaml
variables:
  DOCKER_HOST: tcp://192.168.30.102:2375/

# 缓存maven本地仓库，不然每次都要下载依赖
cache:
  key: m2repo
  paths:
    - .m2

# 构建、发布
stages:
  - build
  - deploy

# 打jar包
build-jar:
  image: maven:3-jdk-8
  stage: build
  only:
    - master
  tags:
    - 102-runner
  script:
    - mvn clean package -Dmaven.repo.local=.m2 -Dmaven.test.skip=true
  artifacts:
    paths:
      - ./target/hello-1.0.jar
    expire_in: 1 hour

# 发布到102，构建docker镜像再运行
deploy-102:
  stage: deploy
  variables:
    GIT_STRATEGY: none
  dependencies:
    - build-job
  only:
    - master
  tags:
    - 102-runner
  script:
    - ls ./target
    - docker build -t hello .
    - if [ $(docker ps -aq --filter name=hello) ]; then docker rm -f hello && docker rmi $(docker images -f "dangling=true" -q); fi
    - docker run -d -p 3080:8080 --name hello hello

# 发布到104，使用jar包
deploy-104:
  stage: deploy
  variables:
    GIT_STRATEGY: none
  dependencies:
    - build-job
  only:
    - master
  tags:
    - 104-runner
  script:
    - pwd
    - ls
    - if [ "$(netstat -nlp | grep 3080)" ]; then netstat -nlp | grep 3080 | awk '{print $7}' | awk -F"/" '{ print $1 }' | xargs kill -9; fi
    - cp ./target/hello-1.0.jar /app/hello.jar
    - java -jar /app/hello.jar --server.port=3080 > /app/hello.out &
```

## 四、坑点
1. gitlab、gitlab-runner及git的版本尽量保持一致，不然runner可能无法拉代码或不执行script等
2. gitlab使用docker安装时，如果自定义宿主机挂载卷，可能出现权限问题导致无法启动、访问时502或无法重启等
3. gitlab修改配置gitlab.rb后可能重启失败，此时可以删掉容器，重新执行docker run