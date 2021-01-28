## 一、安装

```shell
docker run -d --name nexus3 --restart=always \
    -v nexus_data:/nexus-data \
    -p 2081:8081 \
    sonatype/nexus3:3.29.0
```



## 二、配置

* 新建maven-hosted仓库maven-3rd，允许redeploy
* 新建2个maven-proxy仓库，代理地址分别为中央仓库和aliyun
* 新建maven-group仓库maven-public，加入上面的仓库
* 禁用匿名访问
* 配置定时任务Admin - Compact blob store，清理blob



## 三、setting.xml

```xml
<servers>
    <server>
        <id>nexus</id>  
        <username>admin</username>  
        <password>123456</password>  
    </server>
</servers>
```



## 四、pom.xml

```xml
<repositories>
    <repository>
        <id>nexus</id>
        <name>nexus</name>
        <url>http://localhost:2081/repository/maven-public/</url>
    </repository>
    <repository>
        <id>aliyun</id>
        <name>aliyun</name>
        <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
    </repository>
</repositories>

<!-- deploy -->
<distributionManagement>
    <repository>
        <id>nexus</id>
        <name>nexus</name>
        <url>http://ip:2081/repository/maven-3rd/</url>
    </repository>
</distributionManagement>
```