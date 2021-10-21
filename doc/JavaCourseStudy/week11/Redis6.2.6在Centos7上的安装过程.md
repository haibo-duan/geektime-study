# 1.系统环境
操作系统：
```shell
[root@m161p114 software]# lsb_release -a
LSB Version:	:core-4.1-amd64:core-4.1-noarch:cxx-4.1-amd64:cxx-4.1-noarch:desktop-4.1-amd64:desktop-4.1-noarch:languages-4.1-amd64:languages-4.1-noarch:printing-4.1-amd64:printing-4.1-noarch
Distributor ID:	CentOS
Description:	CentOS Linux release 7.9.2009 (Core)
Release:	7.9.2009
Codename:	Core
```
redis安装的源码文件：
[redis-6.2.6.tar.gz](https://download.redis.io/releases/redis-6.2.6.tar.gz)

该文件下载后，放置在 /opt/software目录

# 2.gcc升级
redis6的源码需要用gcc版本为9的环境进行编译。首先需要确认，gcc及gcc-c++ 已经安装。如果没有安装，执行如下命令：
```shell
yum -y install gcc gcc-c++
```
当前系统的gcc环境为：
```shell
[root@m161p114 ~]# gcc -v
Using built-in specs.
COLLECT_GCC=gcc
COLLECT_LTO_WRAPPER=/usr/libexec/gcc/x86_64-redhat-linux/4.8.5/lto-wrapper
Target: x86_64-redhat-linux
Configured with: ../configure --prefix=/usr --mandir=/usr/share/man --infodir=/usr/share/info --with-bugurl=http://bugzilla.redhat.com/bugzilla --enable-bootstrap --enable-shared --enable-threads=posix --enable-checking=release --with-system-zlib --enable-__cxa_atexit --disable-libunwind-exceptions --enable-gnu-unique-object --enable-linker-build-id --with-linker-hash-style=gnu --enable-languages=c,c++,objc,obj-c++,java,fortran,ada,go,lto --enable-plugin --enable-initfini-array --disable-libgcj --with-isl=/builddir/build/BUILD/gcc-4.8.5-20150702/obj-x86_64-redhat-linux/isl-install --with-cloog=/builddir/build/BUILD/gcc-4.8.5-20150702/obj-x86_64-redhat-linux/cloog-install --enable-gnu-indirect-function --with-tune=generic --with-arch_32=x86-64 --build=x86_64-redhat-linux
Thread model: posix
gcc version 4.8.5 20150623 (Red Hat 4.8.5-44) (GCC) 
```
通过如下命令升级：
```shell
yum -y install centos-release-scl
yum -y install devtoolset-9-gcc devtoolset-9-gcc-c++ devtoolset-9-binutils

scl enable devtoolset-9 bash
```

配置环境变量：
vim /etc/profile
在末尾追加：
```shell
source /opt/rh/devtoolset-9/enable
```
这样系统就能确保每次启动都能开启gcc9环境。

现在查看gcc版本：
```shell
[root@m161p114 software]# gcc -v
Using built-in specs.
COLLECT_GCC=gcc
COLLECT_LTO_WRAPPER=/opt/rh/devtoolset-9/root/usr/libexec/gcc/x86_64-redhat-linux/9/lto-wrapper
Target: x86_64-redhat-linux
Configured with: ../configure --enable-bootstrap --enable-languages=c,c++,fortran,lto --prefix=/opt/rh/devtoolset-9/root/usr --mandir=/opt/rh/devtoolset-9/root/usr/share/man --infodir=/opt/rh/devtoolset-9/root/usr/share/info --with-bugurl=http://bugzilla.redhat.com/bugzilla --enable-shared --enable-threads=posix --enable-checking=release --enable-multilib --with-system-zlib --enable-__cxa_atexit --disable-libunwind-exceptions --enable-gnu-unique-object --enable-linker-build-id --with-gcc-major-version-only --with-linker-hash-style=gnu --with-default-libstdcxx-abi=gcc4-compatible --enable-plugin --enable-initfini-array --with-isl=/builddir/build/BUILD/gcc-9.3.1-20200408/obj-x86_64-redhat-linux/isl-install --disable-libmpx --enable-gnu-indirect-function --with-tune=generic --with-arch_32=x86-64 --build=x86_64-redhat-linux
Thread model: posix
gcc version 9.3.1 20200408 (Red Hat 9.3.1-2) (GCC) 
```

# 3.编译及安装redis
在/opt目录中执行解压：
```shell
tar -zxvf /opt/software/redis-6.2.6.tar.gz 
```
然后执行make:
```shell
cd /opt/redis-6.2.6
make
make install PREFIX=/usr/local/redis
```
配置redis环境变量，在/etc/profile中增加如下内容：
```shell
export REDIS_HOME=/usr/local/redis
export PATH=$PATH:$REDIS_HOME/bin
```
之后执行source加载环境变量：
```shell
source /etc/profile
```
执行如下命令。说明环境变量配置成功：
```shell
[root@m161p114 utils]# redis-server --version
Redis server v=6.2.6 sha=00000000:0 malloc=jemalloc-5.1.0 bits=64 build=336fe6a5b7d02b06
```

# 4.通过install_server.sh配置servie服务
如果出现如下提示：
```shell
[root@m161p114 utils]# ./install_server.sh 
Welcome to the redis service installer
This script will help you easily set up a running redis server

This systems seems to use systemd.
Please take a look at the provided example service unit files in this directory, and adapt and install them. Sorry!
```
需要用vim将install_server.sh中的如下代码注释掉:
```shell
#if [ "${_pid_1_exe##*/}" = systemd ]
#then
#       echo "This systems seems to use systemd."
#       echo "Please take a look at the provided example service unit files in this directory, and adapt and install them. Sorry!"
#       exit 1
#fi
```
这是因为，centos7最开始的版本采用systemd配置服务。而新版本的centos又支持兼容之前的centos6中的配置。redis在此做了一个保护。
```shell
cd /opt/redis-6.2.6/utils
[root@m161p114 utils]# ./install_server.sh

Please select the redis port for this instance: [6379] 
Selecting default: 6379
Please select the redis config file name [/etc/redis/6379.conf] 
Selected default - /etc/redis/6379.conf
Please select the redis log file name [/var/log/redis_6379.log] /opt/redis/logs/redis_6379.log
Please select the data directory for this instance [/var/lib/redis/6379] /opt/redis/data/6379
Please select the redis executable path [/usr/local/redis/bin/redis-server] /usr/local/redis/bin/redis-server
Selected config:
Port           : 6379
Config file    : /etc/redis/6379.conf
Log file       : /opt/redis/logs/redis_6379.log
Data dir       : /opt/redis/data/6379
Executable     : /usr/local/redis/bin/redis-server
Cli Executable : //usr/local/redis/bin/redis-cli
Is this ok? Then press ENTER to go on or Ctrl-C to abort.
Copied /tmp/6379.conf => /etc/init.d/redis_6379
Installing service...
Successfully added to chkconfig!
Successfully added to runlevels 345!
Starting Redis server...
Installation successful!

```
现在可以通过service进行启动和关闭：
```shell
[root@m161p114 utils]# service redis_6379 start
Starting Redis server...
[root@m161p114 utils]# service redis_6379 stop
Stopping ...
Redis stopped
[root@m161p114 utils]# 

```

# 5.修改配置文件

修改redis的配置文件：
```shell
vim /etc/redis/6379.conf

修改bind为：
bind 0.0.0.0  -::1
```
这样外部服务才能连接到redis服务中。
启动redis,通过客户端连接测试：
```shell
[root@m161p114 redis]# redis-cli
127.0.0.1:6379> 
[root@m161p114 redis]# redis-cli -h 192.168.161.114 -p 6379
192.168.161.114:6379> 
```
说明redis安装成功。