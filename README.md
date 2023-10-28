# vkit

## common
* common dependency for java project
```xml
<dependency>
    <groupId>top.verytouch.vkit</groupId>
    <artifactId>common</artifactId>
    <version>${latest-version}</version>
</dependency>
```

## mydoc
generate api-doc for springboot project
* download intellij community
* import this module
* build the module
* install the plugin

## samples
```shell
mvn clean package
java -jar ${samples-name}.${latest.verison}.jar
```
* activity: samples of activity
* hystrix: samples of activity
* oauth2: samples of oauth2
* rabbitmq: samples of rabbitmq

## starters
some useful starters for java web development
```xml
<dependency>
    <groupId>top.verytouch.vkit</groupId>
    <artifactId>${starter-name}</artifactId>
    <version>${latest-version}</version>
</dependency>
```
* alipay-spring-boot-starter: alipay
* captcha-spring-boot-starter: image, tencent
* chatgpt-spring-boot-starter: chatgpt-api
* ocr-spring-boot-starter: tencent-ocr
* oss-spring-boot-starter: minio, ali-oss
* rbac-spring-boot-starter: rbac framework based oauth2

## tools
* external: external tools for idea
* note: note
* script: script
* sql: sql

## todo list
1. starts: rate limit