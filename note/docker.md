### 1. 安装

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

# 安装私库 "insecure-registries": ["ip:port"]
docker run -d -p 5000:5000 -v /app/docker-mnt/registry:/var/lib/registry --name docker-registry registry

# 安装docker-compose
curl -L https://get.daocloud.io/docker/compose/releases/download/1.25.1/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
```

### 2.命令

```shell
docker search registry
docker pull registry
docker run -d -v /app/data/reigistry:/ver/lib/registry -p 5000:5000 --name registry registry
docker ps -a
docker exec -it id bash
docker stop id
docker rm id
docker commit cid myregistry
docker images
docker tag myregistry 127.0.0.1:5000/myregistry
docker push 127.0.0.1:5000/myregistry
docker rmi id
docker pull 127.0.0.1:5000/myregistry
docker built -t app:1.0 .
docker cp id:file file
docker-compose up -d

# 根据名称移除容器
docker rm -f $(docker ps -a -q --filter name=crm-*)
# 根据名称移除镜像
docker rmi $(docker images -q 192.168.30.102:5000/crm-*)
docker images | grep "crm" | awk '{print $3}'
for i in $(docker images | grep -e crm-.*1.7 | awk 'BEGIN{OFS=":"}{print $1,$2}'); do docker push $i; done
docker volume ls
docker volume prune
```

### 3. 构建

* Dockerfile

  ```dockerfile
  FROM openjdk:8
  RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
  COPY ./app.jar /app.jar
  ENTRYPOINT ["java", "-server", "-Xmx256m", "-Xms256m",  "-Dfile.encoding=utf-8", "-jar", "/app.jar"]
  CMD ["--spring.profiles.active=pro"]
  ```

* docker-compose.yml

  ```yaml
  version: "3"
  services:
    verytouch-front:
      build: ./verytouch-front/docker
      image: verytouch-front
      restart: always
      container_name: verytouch-front
      ports:
        - 80:80
    verytouch-admin:
      build: ./verytouch-admin/docker
      image: verytouch-admin
      restart: always
      container_name: verytouch-admin
      ports:
        - 8080:8080
      command:
        - --spring.profiles.active=pro
      depends_on:
        - redis
      cap_add:
        - SYS_PTRACE
    redis:
      image: redis
      container_name: redis
      restart: always
      ports:
        - 8379:6379
      command:
        - --requirepass "123456"
    nexus:
      image: sonatype/nexus3
      container_name: nexus
      restart: "no"
      ports:
        - 82:8081
      volumes:
        - /root/docker-volumes/nexus:/nexus-data
      environment:
        INSTALL4J_ADD_VM_PARAMS: -Xms256m -Xmx256m -XX:MaxDirectMemorySize=256m -Djava.util.prefs.userRoot=/nexus-data/javaprefs
  ```

* docker-machine

* docker-swarm

* k8s