## 一、命令

```shell
# 服务
systemctl enable docker
systemctl disable docker

# 端口进程
lsof -i:port
netstat -aptn
if [ "$(netstat -nlp | grep 7001)" ]; 
	then netstat -nlp | grep 7001 | awk '{print $7}' | awk -F "/" '{ print $1 }' | xargs kill 
fi

# 压缩解压
unzip aaa.zip -d /app/aaa
tar -zxvf git-2.19.0.tar.gz

# cpu、内存
top
top -Hp pid
free

# 磁盘
df -h
du -sh /app
du -xh --max-depth=1 /app
```

