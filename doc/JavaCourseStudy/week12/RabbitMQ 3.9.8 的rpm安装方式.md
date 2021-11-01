
之前采用源码的方式安装erlang,之后再来安装rabbitMQ，这种方式有一些复杂。现在参考官网，可以直接用rpm安装。本文对安装过程进行描述。
官方参考[Installing on RPM-based Linux (RedHat Enterprise Linux, CentOS, Fedora, openSUSE)](https://www.rabbitmq.com/install-rpm.html#downloads)

# 1.rpm包下载

需要安装的操作系统信息：
```shell
[root@m161p114 ~]# lsb_release -a
LSB Version:	:core-4.1-amd64:core-4.1-noarch:cxx-4.1-amd64:cxx-4.1-noarch:desktop-4.1-amd64:desktop-4.1-noarch:languages-4.1-amd64:languages-4.1-noarch:printing-4.1-amd64:printing-4.1-noarch
Distributor ID:	CentOS
Description:	CentOS Linux release 7.9.2009 (Core)
Release:	7.9.2009
Codename:	Core
[root@m161p114 ~]# 
```
本文选择最新版本的RabbitMQ 3.9.8。现在rabbitMQ的下载文件都放在了github。

[rabbitmq-server](https://github.com/rabbitmq/rabbitmq-server/releases)

由于操作系统是centos7,那么选择下载的版本为 [rabbitmq-server-3.9.8-1.el7.noarch.rpm](https://github-releases.githubusercontent.com/924551/6d354c7b-9afb-46ee-9fe7-bf1473c2c8ac?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIWNJYAX4CSVEH53A%2F20211101%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20211101T062119Z&X-Amz-Expires=300&X-Amz-Signature=5fd90bcdcfe96b5c4c9e5eb2dc2a6d640438d9ea4728044e1c65c19be492ff0f&X-Amz-SignedHeaders=host&actor_id=7974845&key_id=0&repo_id=924551&response-content-disposition=attachment%3B%20filename%3Drabbitmq-server-3.9.8-1.el7.noarch.rpm&response-content-type=application%2Foctet-stream).

另外需要下载erlang,参考rabbitMQ和rabbitMQ的兼容关系：
[RabbitMQ Erlang Version Requirements](https://www.rabbitmq.com/which-erlang.html)

erlang最低版本为23.2，在此选择了23.3.4.8版本。因为这个版本有支持el7的rpm包可下载。
[erlang-rpm](https://github.com/rabbitmq/erlang-rpm/releases)

下载的版本为：[erlang-23.3.4.8-1.el7.x86_64.rpm](https://github-releases.githubusercontent.com/47679505/0da549a5-6a9e-4863-9a78-d2176436f886?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIWNJYAX4CSVEH53A%2F20211101%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20211101T063548Z&X-Amz-Expires=300&X-Amz-Signature=d48e33c4a0faa72249d41e5d1cc33ecf853fdd71c2e5fd4a5c9ce94934407ec6&X-Amz-SignedHeaders=host&actor_id=7974845&key_id=0&repo_id=47679505&response-content-disposition=attachment%3B%20filename%3Derlang-23.3.4.8-1.el7.x86_64.rpm&response-content-type=application%2Foctet-stream)

现在将这两个rpm上传到需要安装的服务的/opt/software目录。

# 2.rpm安装
首先要安装依赖包：
```shell
[root@m161p114 ~]# yum install -y socat
Loaded plugins: fastestmirror, langpacks
Determining fastest mirrors
 * base: mirrors.aliyun.com
 * centos-sclo-rh: mirrors.huaweicloud.com
 * centos-sclo-sclo: mirrors.huaweicloud.com
 * extras: mirrors.aliyun.com
 * updates: mirrors.aliyun.com
base                                                                                                                                                                                      | 3.6 kB  00:00:00     
centos-sclo-rh                                                                                                                                                                            | 3.0 kB  00:00:00     
centos-sclo-sclo                                                                                                                                                                          | 3.0 kB  00:00:00     
epel                                                                                                                                                                                      | 4.7 kB  00:00:00     
extras                                                                                                                                                                                    | 2.9 kB  00:00:00     
updates                                                                                                                                                                                   | 2.9 kB  00:00:00     
(1/3): epel/x86_64/updateinfo                                                                                                                                                             | 1.0 MB  00:00:01     
(2/3): epel/x86_64/primary_db                                                                                                                                                             | 7.0 MB  00:00:06     
(3/3): updates/7/x86_64/primary_db                                                                                                                                                        |  12 MB  00:00:08     
Resolving Dependencies
--> Running transaction check
---> Package socat.x86_64 0:1.7.3.2-2.el7 will be installed
--> Finished Dependency Resolution

Dependencies Resolved

=================================================================================================================================================================================================================
 Package                                         Arch                                             Version                                                   Repository                                      Size
=================================================================================================================================================================================================================
Installing:
 socat                                           x86_64                                           1.7.3.2-2.el7                                             base                                           290 k

Transaction Summary
=================================================================================================================================================================================================================
Install  1 Package

Total download size: 290 k
Installed size: 1.1 M
Downloading packages:
socat-1.7.3.2-2.el7.x86_64.rpm                                                                                                                                                            | 290 kB  00:00:00     
Running transaction check
Running transaction test
Transaction test succeeded
Running transaction
Warning: RPMDB altered outside of yum.
  Installing : socat-1.7.3.2-2.el7.x86_64                                                                                                                                                                    1/1 
  Verifying  : socat-1.7.3.2-2.el7.x86_64                                                                                                                                                                    1/1 

Installed:
  socat.x86_64 0:1.7.3.2-2.el7                                                                                                                                                                                   

Complete!
```

erlang安装：
```shell
[root@m161p114 software]# cd /opt/software

[root@m161p114 software]# rpm -ivh erlang-23.3.4.8-1.el7.x86_64.rpm 
warning: erlang-23.3.4.8-1.el7.x86_64.rpm: Header V4 RSA/SHA256 Signature, key ID cc4bbe5b: NOKEY
Preparing...                          ################################# [100%]
Updating / installing...
   1:erlang-23.3.4.8-1.el7            ################################# [100%]
[root@m161p114 software]# 
```
安装之后查看版本：
```shell
[root@m161p114 software]# erl
Erlang/OTP 23 [erts-11.2.2.7] [source] [64-bit] [smp:4:4] [ds:4:4:10] [async-threads:1] [hipe]

Eshell V11.2.2.7  (abort with ^G)

```
erlang23安装成功。

rabbitMQ安装：
```shell
[root@m161p114 software]# rpm -ivh rabbitmq-server-3.9.8-1.el7.noarch.rpm 
warning: rabbitmq-server-3.9.8-1.el7.noarch.rpm: Header V4 RSA/SHA512 Signature, key ID 6026dfca: NOKEY
Preparing...                          ################################# [100%]
Updating / installing...
   1:rabbitmq-server-3.9.8-1.el7      ################################# [100%]
[root@m161p114 software]# 

```

# 3.配置及启动rabbitmq

首先要检查host文件，在hosts文件中确保存在hostname对应的ip解析。
在本文中，增加：
```shell
192.168.161.114 m161p114
```

配置管理后台：
```shell
[root@m161p114 ~]# rabbitmq-plugins enable rabbitmq_management
Enabling plugins on node rabbit@m161p114:
rabbitmq_management
The following plugins have been configured:
  rabbitmq_management
  rabbitmq_management_agent
  rabbitmq_web_dispatch
Applying plugin configuration to rabbit@m161p114...
The following plugins have been enabled:
  rabbitmq_management
  rabbitmq_management_agent
  rabbitmq_web_dispatch

set 3 plugins.
Offline change; changes will take effect at broker restart.

```

之后，启动rabbitmq:
```shell
[root@m161p114 opt]# service rabbitmq-server start
Redirecting to /bin/systemctl start rabbitmq-server.service
```
rabbitmq启动完成。



增加用户：
```shell
[root@m161p114 opt]# rabbitmqctl add_user root 123456
Adding user "root" ...
Done. Don't forget to grant the user permissions to some virtual hosts! See 'rabbitmqctl help set_permissions' to learn more.
[root@m161p114 opt]# rabbitmqctl set_user_tags root administrator
Setting tags for user "root" to [administrator] ...
[root@m161p114 opt]# 

```

至此，rabbitmq安装完成，可以用新增的用户，访问rabbitMQ:

http://192.168.161.114:15672 

![RabbitMQ 登陆](../../images/RabbitMQ%20登陆.png)

这样RabbitMQ就安装完毕，通过service rabbitmq-server 可以对rabbitmq进行启动和关闭。

# 4.遇到的一些常见的错误

## 4.1 端口被占用错误
启动报错:
```shell

[root@m161p114 ~]# service rabbitmq-server restart
Redirecting to /bin/systemctl restart rabbitmq-server.service
Job for rabbitmq-server.service failed because the control process exited with error code. See "systemctl status rabbitmq-server.service" and "journalctl -xe" for details.
[root@m161p114 ~]# systemctl start rabbitmq-server
Job for rabbitmq-server.service failed because the control process exited with error code. See "systemctl status rabbitmq-server.service" and "journalctl -xe" for details.
[root@m161p114 ~]# systemctl status rabbitmq-server.service
● rabbitmq-server.service - RabbitMQ broker
   Loaded: loaded (/usr/lib/systemd/system/rabbitmq-server.service; disabled; vendor preset: disabled)
   Active: activating (auto-restart) (Result: exit-code) since Mon 2021-11-01 14:54:05 CST; 9s ago
  Process: 25478 ExecStart=/usr/sbin/rabbitmq-server (code=exited, status=1/FAILURE)
 Main PID: 25478 (code=exited, status=1/FAILURE)
   Status: "Standing by"

Nov 01 14:54:05 m161p114 systemd[1]: Failed to start RabbitMQ broker.
Nov 01 14:54:05 m161p114 systemd[1]: Unit rabbitmq-server.service entered failed state.
Nov 01 14:54:05 m161p114 systemd[1]: rabbitmq-server.service failed.


[root@m161p114 ~]# systemctl status rabbitmq-server.service
● rabbitmq-server.service - RabbitMQ broker
   Loaded: loaded (/usr/lib/systemd/system/rabbitmq-server.service; disabled; vendor preset: disabled)
   Active: activating (auto-restart) (Result: exit-code) since Mon 2021-11-01 14:56:45 CST; 1s ago
  Process: 26366 ExecStart=/usr/sbin/rabbitmq-server (code=exited, status=1/FAILURE)
 Main PID: 26366 (code=exited, status=1/FAILURE)
   Status: "Standing by"

Nov 01 14:56:45 m161p114 systemd[1]: Failed to start RabbitMQ broker.
Nov 01 14:56:45 m161p114 systemd[1]: Unit rabbitmq-server.service entered failed state.
Nov 01 14:56:45 m161p114 systemd[1]: rabbitmq-server.service failed.
[root@m161p114 ~]# systemctl start rabbitmq-server
Job for rabbitmq-server.service failed because the control process exited with error code. See "systemctl status rabbitmq-server.service" and "journalctl -xe" for details.
[root@m161p114 ~]# journalctl -xe
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {failed_to_start_child,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {ranch_embedded_sup,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {acceptor,{0,0,0,0,0,0,0,0},5672}},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {shutdown,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {failed_to_start_child,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {ranch_listener_sup,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {acceptor,{0,0,0,0,0,0,0,0},5672}},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {shutdown,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {failed_to_start_child,ranch_acceptors_sup,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {listen_error,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {acceptor,{0,0,0,0,0,0,0,0},5672},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: eaddrinuse}}}}}}},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {child,undefined,'rabbit_tcp_listener_sup_:::5672',
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {tcp_listener_sup,start_link,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: [{0,0,0,0,0,0,0,0},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: 5672,ranch_tcp,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: [inet6,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {backlog,128},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {nodelay,true},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {linger,{true,0}},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {exit_on_close,false}],
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: rabbit_connection_sup,[],
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {rabbit_networking,tcp_listener_started,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: [amqp,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: [{backlog,128},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {nodelay,true},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {linger,{true,0}},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {exit_on_close,false}]]},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {rabbit_networking,tcp_listener_stopped,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: [amqp,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: [{backlog,128},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {nodelay,true},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {linger,{true,0}},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: {exit_on_close,false}]]},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: 10,1,"TCP listener"]},
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: transient,infinity,supervisor,
Nov 01 14:57:16 m161p114 rabbitmq-server[26561]: [tcp_listener_sup]}}}}
Nov 01 14:57:17 m161p114 rabbitmq-server[26561]: {"init terminating in do_boot",{error,{could_not_start_listener,"::",5672,{{shutdown,{failed_to_start_child,{ranch_embedded_sup,{acceptor,{0,0,0,0,0,0,0,0},5672
Nov 01 14:57:17 m161p114 rabbitmq-server[26561]: init terminating in do_boot ({error,{could_not_start_listener,::,5672,{{shutdown,{_}},{child,undefined,rabbit_tcp_listener_sup_:::5672,{_},transient,infinity,su

```
启动rabbitmq,出现上述错误。
实际上这是端口被占用所致。rabbitmq使用的是5672端口。而这个服务器上由于安装过activeMQ,导致5672端口被占用。
```shell
[root@m161p114 usr]# netstat -an |grep 5672
tcp        0      0 0.0.0.0:5672            0.0.0.0:*               LISTEN     
[root@m161p114 usr]# lsof -i:5672
COMMAND   PID USER   FD   TYPE DEVICE SIZE/OFF NODE NAME
java    15085 root  139u  IPv4 142285      0t0  TCP *:amqp (LISTEN)
[root@m161p114 usr]# 
[root@m161p114 etc]# ps -aux |grep activemq
root     15085  0.1  5.6 4749504 450096 ?      Sl   Oct28   6:45 /bin/java -Xms64M -Xmx1G -Djava.util.logging.config.file=logging.properties -Djava.security.auth.login.config=/opt/apache-activemq-5.16.3//conf/login.config -Dcom.sun.management.jmxremote -Djava.awt.headless=true -Djava.io.tmpdir=/opt/apache-activemq-5.16.3//tmp -Dactivemq.classpath=/opt/apache-activemq-5.16.3//conf:/opt/apache-activemq-5.16.3//../lib/: -Dactivemq.home=/opt/apache-activemq-5.16.3/ -Dactivemq.base=/opt/apache-activemq-5.16.3/ -Dactivemq.conf=/opt/apache-activemq-5.16.3//conf -Dactivemq.data=/opt/apache-activemq-5.16.3//data -jar /opt/apache-activemq-5.16.3//bin/activemq.jar start
root     32118  0.0  0.0 112820   944 pts/0    S+   15:14   0:00 grep --color=auto activemq
[root@m161p114 etc]# 

```
可以看到，5672被activeMQ占用。虽然activeMQ使用的是61616端口，但是amqp服务还是会占用5672端口。
我们只需要关闭activeMQ,然后重启rabbitMQ即可。

## 4.2 增加用户报错
增加rabbitMQ 用户的过程中，出现如下错误：
```shell
[root@m161p114 etc]# rabbitmqctl add_user root 123456
Error: unable to perform an operation on node 'rabbit@m161p114'. Please see diagnostics information and suggestions below.

Most common reasons for this are:

 * Target node is unreachable (e.g. due to hostname resolution, TCP connection or firewall issues)
 * CLI tool fails to authenticate with the server (e.g. due to CLI tool's Erlang cookie not matching that of the server)
 * Target node is not running

In addition to the diagnostics info below:

 * See the CLI, clustering and networking guides on https://rabbitmq.com/documentation.html to learn more
 * Consult server logs on node rabbit@m161p114
 * If target node is configured to use long node names, don't forget to use --longnames with CLI tools

DIAGNOSTICS
===========

attempted to contact: [rabbit@m161p114]

rabbit@m161p114:
  * connected to epmd (port 4369) on m161p114
  * epmd reports: node 'rabbit' not running at all
                  no other nodes on m161p114
  * suggestion: start the node

Current node details:
 * node name: 'rabbitmqcli-242-rabbit@m161p114'
 * effective user's home directory: /var/lib/rabbitmq
 * Erlang cookie hash: sLsnKdrzrUtqBTBdswbG6g==

[root@m161p114 etc]# 

```
这需要将rabbitMQ server启动。否则将会出现上述错误。
