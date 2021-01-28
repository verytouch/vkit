### maven项目发布到中央仓库

> https://my.oschina.net/huangyong/blog/226738

1. 注册github账号，新建项目

2. 注册sonatype账号，新建issue，需要对方审批，自己要做一些回应，注意邮件

   > https://issues.sonatype.org/browse/OSSRH-61054?filter=-2
   >
   > verytouch     Sonatype_607579848

3. 安装gpg，生成密钥对，公钥发布到密钥服务器

```shell
gpg --gen-key
gpg --keyserver hkp://pool.sks-keyservers.net --send-keys 33F38C0F7F755D60
gpg --keyserver hkp://keyserver.ubuntu.com --send-keys 33F38C0F7F755D60
gpg --keyserver hkp://pgp.mit.edu --send-keys 33F38C0F7F755D60

-- windows图形界面版本 https://www.gpg4win.org/

09CAD21DBC86F097DB6F6E19F325F34A2ABD05D6
sonatype123456
```

4. 修改项目配置，setting.xml和pom.xml

   ```xml
   <!-- setting.xml -->
   <server>
       <id>oss</id>
       <username>用户名</username>
       <password>密码</password>
   </server>
   
   <!-- pom.xml -->
   <name>vkit</name>
   <description>verytouch's kit.</description>
   <url>https://github.com/verytouch/vkit</url>
   <developers>
       <developer>
           <name>verytouch</name>
           <email>verytouch@qq.com</email>
       </developer>
   </developers>
   <licenses>
       <license>
           <name>GNU GENERAL PUBLIC LICENSE</name>
           <url>https://opensource.org/licenses/GPL-3.0</url>
           <distribution>repo</distribution>
       </license>
   </licenses>
   <scm>
       <connection>scm:git:git@github.com:verytouch/vkit.git</connection>
       <developerConnection>scm:git:git@github.com:verytouch/vkit.git</developerConnection>
       <url>https://github.com/verytouch/vkit</url>
   </scm>
   <profiles>
       <profile>
           <id>release</id>
           <build>
               <plugins>
                   <!-- Source -->
                   <plugin>
                       <groupId>org.apache.maven.plugins</groupId>
                       <artifactId>maven-source-plugin</artifactId>
                       <version>2.2.1</version>
                       <executions>
                           <execution>
                               <phase>package</phase>
                               <goals>
                                   <goal>jar-no-fork</goal>
                               </goals>
                           </execution>
                       </executions>
                   </plugin>
                   <!-- Javadoc -->
                   <plugin>
                       <groupId>org.apache.maven.plugins</groupId>
                       <artifactId>maven-javadoc-plugin</artifactId>
                       <version>2.9.1</version>
                       <executions>
                           <execution>
                               <phase>package</phase>
                               <goals>
                                   <goal>jar</goal>
                               </goals>
                           </execution>
                       </executions>
                   </plugin>
                   <!-- GPG -->
                   <plugin>
                       <groupId>org.apache.maven.plugins</groupId>
                       <artifactId>maven-gpg-plugin</artifactId>
                       <version>1.6</version>
                       <executions>
                           <execution>
                               <phase>verify</phase>
                               <goals>
                                   <goal>sign</goal>
                               </goals>
                           </execution>
                       </executions>
                   </plugin>
               </plugins>
           </build>
           <distributionManagement>
               <snapshotRepository>
                   <id>oss</id>
                   <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
               </snapshotRepository>
               <repository>
                   <id>oss</id>
                   <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
               </repository>
           </distributionManagement>
       </profile>
   </profiles>
   ```

   

5. 打包上传

   ```
   mvn clean install deploy -P release -Dgpg.passphrase=sonatype123456
   ```

6. 发布并回复sonatype

   * 登录https://oss.sonatype.org/#stagingRepositories
   * 找到自己的项目，close
   * release
   * 回复第一步的issue
   * 等待一两个小时，https://search.maven.org/搜索发布的项目