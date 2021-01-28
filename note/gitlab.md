> CI文档：http://localhost/help/ci/yaml/README.md

## 一. gitlab
```shell
# 安装，不用ssh克隆可以去掉22端口映射
# 停止后需要删掉容器重新跑一下这个命令，注意不要清理volume
docker run -d --name gitlab --privileged=true \
	  -v gitlab_config:/etc/gitlab \
	  -v gitlab_log:/var/log/gitlab \
	  -v gitlab_data:/var/opt/gitlab \
	  -p 2080:2080 \
	  -p 22:22 \
	  gitlab/gitlab-ce
	  
# 配置
vi /var/lib/docker/volumes/gitlab_config/_data/gitlab.rb
# 添加这行 external_url 'http://192.168.30.102:2080'
docker restart gitlab
```

## 二. runner
```shell
# 1.rpm安装
rpm -ivh gitlab-runner-13.6.0-1.x86_64.rpm --nodeps --force
# 注册，executor选择shell
/usr/bin/gitlab-runner register
# 切换root用户
/usr/bin/gitlab-runner uninstall
/usr/bin/gitlab-runner install --working-directory /home/gitlab-runner --user root
/usr/bin/gitlab-runner restart
# 非root用户添加权限
chmod -R 777 /app/bin
chmod -R 888 /app/log
vi /etc/sudoers
# gitlab-runner ALL=(ALL)   NOPASSWD:/usr/bin/netstat,/usr/bin/xargs,/usr/bin/kill

# 2.docker安装，注册的时候executor选择docker
docker run -it --name gitlab-runner \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v gitlab_runner_config:/etc/gitlab-runner \
    -v /var/lib/.m2/reposiotry:/var/lib/.m2/repository \
    gitlab/gitlab-runner:latest register
```

## 三. git
```shell
# 卸载老版本
yum remove git
# 安装依赖
yum -y install curl-devel expat-devel gettext-devel openssl-devel zlib-devel gcc perl-ExtUtils-MakeMaker
# vi /etc/yum.repos.d/CentOS-Media.repo，将里面的enabled=1改成enabled=0，然后保存退出
tar zxf git-2.25.1.tar.gz cd git-2.25.1
make prefix=/usr/local/git all
make prefix=/usr/local/git install
# 添加环境变量: /usr/local/git/bin
vi /etc/bashrc 
source /etc/bashrc
git --version
```
