
本文将利用redis的sentinel，实现redis集群的高可用。
# 1.服务器规划
服务器资源规划如下表：

|机器名|ip|redis port|redis status|sentinel port|
|:----|:----|:----|:----|:----|
|m161p114|192.168.161.114|6379|master|26379|
|m161p115|192.168.161.115|6379|slave|26379|
|m162p203|192.168.162.203|6379|slave|26379|

将搭建一个redis一主两从的主从复制集群，然后通过sentinel实现故障转移。

# 2.redis主从配置
redis主从配置详见[Redis主从复制的配置过程](./Redis主从复制的配置过程.md).
在本文中，节点m161p114作为master节点，只需要在其他slave节点上，执行主从同步的命令即可。
```shell
127.0.0.1:6379> slaveof 192.168.161.114 6379
OK
127.0.0.1:6379> 
```
只需要在m161p115、m162p203两个节点执行上述的slaveof命令，就能组建一个以m161p114为master的redis集群。
之后通过info可以确认集群状态：
m161p115:
```shell
127.0.0.1:6379> info replication
# Replication
role:slave
master_host:192.168.161.114
master_port:6379
master_link_status:up
master_last_io_seconds_ago:0
master_sync_in_progress:0
slave_read_repl_offset:279743
slave_repl_offset:279743
slave_priority:100
slave_read_only:1
replica_announced:1
connected_slaves:0
master_failover_state:no-failover
master_replid:00bad10aad1b2ddde89253fd5c026c9a3925d7de
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:279743
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:123059
repl_backlog_histlen:156685
```
m162p203:
```shell
127.0.0.1:6379> info replication
# Replication
role:slave
master_host:192.168.161.114
master_port:6379
master_link_status:up
master_last_io_seconds_ago:1
master_sync_in_progress:0
slave_read_repl_offset:290875
slave_repl_offset:290875
slave_priority:100
slave_read_only:1
replica_announced:1
connected_slaves:0
master_failover_state:no-failover
master_replid:00bad10aad1b2ddde89253fd5c026c9a3925d7de
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:290875
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:87690
repl_backlog_histlen:203186
127.0.0.1:6379> 

```

# 3.sentinel配置
现在对3个节点的sentinel进行配置。sentinel的配置文件在redis的安装目录中已经存在，只需要复制到制定的位置即可。
在/opt/redis-6.2.6目录中：
```shell
[root@m162p203 redis-6.2.6]# ll
total 244
-rw-rw-r--  1 root root 33624 Oct  4 18:59 00-RELEASENOTES
-rw-rw-r--  1 root root    51 Oct  4 18:59 BUGS
-rw-rw-r--  1 root root  5026 Oct  4 18:59 CONDUCT
-rw-rw-r--  1 root root  3384 Oct  4 18:59 CONTRIBUTING
-rw-rw-r--  1 root root  1487 Oct  4 18:59 COPYING
drwxrwxr-x  7 root root   213 Oct 26 11:18 deps
-rw-rw-r--  1 root root    11 Oct  4 18:59 INSTALL
-rw-rw-r--  1 root root   151 Oct  4 18:59 Makefile
-rw-rw-r--  1 root root  6888 Oct  4 18:59 MANIFESTO
-rw-rw-r--  1 root root 21567 Oct  4 18:59 README.md
-rw-rw-r--  1 root root 93724 Oct  4 18:59 redis.conf
-rwxrwxr-x  1 root root   275 Oct  4 18:59 runtest
-rwxrwxr-x  1 root root   279 Oct  4 18:59 runtest-cluster
-rwxrwxr-x  1 root root  1079 Oct  4 18:59 runtest-moduleapi
-rwxrwxr-x  1 root root   281 Oct  4 18:59 runtest-sentinel
-rw-rw-r--  1 root root 13768 Oct  4 18:59 sentinel.conf
drwxrwxr-x  3 root root 12288 Oct 26 11:20 src
drwxrwxr-x 11 root root   182 Oct  4 18:59 tests
-rw-rw-r--  1 root root  3055 Oct  4 18:59 TLS.md
drwxrwxr-x  9 root root  4096 Oct 26 11:22 utils
```
现在将sentinel.conf copy到/etc/redis目录：
```shell
cp /opt/redis-6.2.6/sentinel.conf  /etc/redis/sentinel_26379.conf
```
由于sentinel与redis实例是一一对应的关系，那么sentinel将使用26379端口。我们将配置文件也做如此命名，便于后续在同一个服务器配置多个redis和sentinel。

在三个节点中的sentinel配置如下：
```shell
#监听端口
port 26379
#设置daemonize为yes,否则不会采用守护进程启动，启动命令会阻塞
daemonize yes
# pidfile 加上端口
pidfile "/var/run/redis-sentinel_26379.pid"
#自定义logfile文件的位置
logfile "/opt/redis/logs/sentinel_26379.log"
dir "/tmp"
#sentinel都指向redis集群的主节点
sentinel monitor mymaster 192.168.161.144 6379 2

acllog-max-len 128
sentinel deny-scripts-reconfig yes
sentinel resolve-hostnames no

protected-mode no
user default on nopass ~* &* +@all
sentinel myid 231bd2b57f330458efb37dd895b0b9cdac98c145
sentinel config-epoch mymaster 0
sentinel leader-epoch mymaster 0
sentinel current-epoch 0
```
在三个节点m161p114、m161p115、m162p203三个服务中，sentinel的配置都相同。
在sentinel启动的时候，都指向reids的master节点。

之后启动sentinel。
m161p114:
```shell
[root@m161p114 redis]# cd /etc/redis
[root@m161p114 redis]# redis-sentinel sentinel_26379.conf 
```
m161p115:
```shell
[root@m161p115 redis]# cd /etc/redis
[root@m161p115 redis]# redis-sentinel sentinel_26379.conf 
```
m162p203:
```shell
[root@m162p203 redis]# cd /etc/redis
[root@m162p203 redis]# redis-sentinel sentinel_26379.conf 
```

sentinel也支持redis的协议，可以通过redis-cli连接，并查看setinel的信息：
m161p114:
```shell
[root@m161p114 ~]# redis-cli -h 192.168.161.114 -p 26379
192.168.161.114:26379> info sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=mymaster,status=ok,address=192.168.161.114:6379,slaves=2,sentinels=3
192.168.161.114:26379> 
```
m161p115:
```shell
[root@m161p115 redis]# redis-cli -h 192.168.161.115 -p 26379
192.168.161.115:26379> info sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=mymaster,status=ok,address=192.168.161.114:6379,slaves=2,sentinels=3
```
m162p203:
```shell
192.168.162.203:26379> info sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=mymaster,status=ok,address=192.168.161.114:6379,slaves=2,sentinels=3
192.168.162.203:26379> 
```
三个节点的sentinel信息都一致，sentinel配置成功。
查看sentinel的日志：
```shell
15545:X 26 Oct 2021 15:21:08.001 # Configuration loaded
15545:X 26 Oct 2021 15:21:08.002 * monotonic clock: POSIX clock_gettime
15545:X 26 Oct 2021 15:21:08.002 * Running mode=sentinel, port=26379.
15545:X 26 Oct 2021 15:21:08.002 # WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128.
15545:X 26 Oct 2021 15:21:08.003 # Sentinel ID is 231bd2b57f330458efb37dd895b0b9cdac98c145
15545:X 26 Oct 2021 15:21:08.003 # +monitor master mymaster 192.168.161.114 6379 quorum 2
15545:X 26 Oct 2021 15:21:08.004 * +slave slave 192.168.162.203:6379 192.168.162.203 6379 @ mymaster 192.168.161.114 6379
15545:X 26 Oct 2021 15:21:08.006 * +slave slave 192.168.161.115:6379 192.168.161.115 6379 @ mymaster 192.168.161.114 6379
15545:X 26 Oct 2021 15:21:08.827 * +sentinel sentinel 703d6ecb6eb8f3af512867fa31c8bd43a5cfce38 192.168.161.114 26379 @ mymaster 192.168.161.114 6379
15545:X 26 Oct 2021 15:21:08.971 * +sentinel sentinel 90848ce683c46822b5c28b3f00c78d8f6c1a23b4 192.168.162.203 26379 @ mymaster 192.168.161.114 6379
```

# 4.故障转移测试
现在kill掉master，测试能否将其他从节点重置为主节点。
m161p114:
```shell
[root@m161p114 ~]# ps -aux|grep redis
root       779  0.0  0.1 171288 10348 ?        Ssl  Oct25   1:46 /usr/local/redis/bin/redis-server 0.0.0.0:6379
root      7148  0.2  0.1 162588  9884 ?        Ssl  14:15   0:12 redis-sentinel *:26379 [sentinel]
root      7478  0.0  0.0 243456  4620 pts/0    S    15:44   0:00 /usr/bin/sudo -E env LD_LIBRARY_PATH=/opt/rh/devtoolset-9/root/usr/lib64:/opt/rh/devtoolset-9/root/usr/lib:/opt/rh/devtoolset-9/root/usr/lib64/dyninst:/opt/rh/devtoolset-9/root/usr/lib/dyninst:/opt/rh/devtoolset-9/root/usr/lib64:/opt/rh/devtoolset-9/root/usr/lib PATH=/opt/rh/devtoolset-9/root/usr/bin:/opt/mysql/bin:/usr/lib64/qt-3.3/bin:/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/root/bin:/usr/local/redis/bin:/home/haibo.duan/.local/bin:/home/haibo.duan/bin scl enable devtoolset-9  'su' '-' 'root'
root      7531  0.0  0.0 112816   944 pts/0    S+   15:44   0:00 grep --color=auto redis
[root@m161p114 ~]# 
[root@m161p114 ~]# kill -9 779
```
然后可以查看其他节点的日志：
m162p203 redis日志：
```shell
19614:S 26 Oct 2021 15:46:39.964 # Connection with master lost.
19614:S 26 Oct 2021 15:46:39.964 * Caching the disconnected master state.
19614:S 26 Oct 2021 15:46:39.964 * Reconnecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:39.964 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:39.964 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:40.389 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:40.389 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:40.389 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:41.393 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:41.393 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:41.393 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:42.397 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:42.397 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:42.397 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:43.400 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:43.401 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:43.401 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:44.405 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:44.405 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:44.405 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:45.410 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:45.410 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:45.410 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:46.414 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:46.414 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:46.415 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:47.419 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:47.419 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:47.419 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:48.423 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:48.423 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:48.424 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:49.429 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:49.429 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:49.429 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:50.433 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:50.434 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:50.434 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:51.440 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:51.440 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:51.440 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:52.446 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:52.447 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:52.447 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:53.452 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:53.452 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:53.452 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:54.458 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:54.458 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:54.458 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:55.465 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:55.465 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:55.465 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:56.472 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:56.472 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:56.472 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:57.477 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:57.477 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:57.477 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:58.483 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:58.484 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:58.484 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:46:59.490 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:46:59.491 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:46:59.491 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:47:00.496 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:47:00.496 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:47:00.497 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:47:01.501 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:47:01.501 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:47:01.501 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:47:02.509 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:47:02.509 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:47:02.509 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:47:03.514 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:47:03.514 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:47:03.514 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:47:04.521 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:47:04.521 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:47:04.521 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:47:05.526 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:47:05.526 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:47:05.526 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:47:06.532 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:47:06.532 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:47:06.533 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:47:07.539 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:47:07.539 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:47:07.539 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:47:08.544 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:47:08.544 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:47:08.545 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:47:09.551 * Connecting to MASTER 192.168.161.114:6379
19614:S 26 Oct 2021 15:47:09.551 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:47:09.552 # Error condition on socket for SYNC: Connection refused
19614:S 26 Oct 2021 15:47:10.444 * Connecting to MASTER 192.168.161.115:6379
19614:S 26 Oct 2021 15:47:10.444 * MASTER <-> REPLICA sync started
19614:S 26 Oct 2021 15:47:10.444 * REPLICAOF 192.168.161.115:6379 enabled (user request from 'id=12 addr=192.168.162.203:10404 laddr=192.168.162.203:6379 fd=7 name=sentinel-90848ce6-cmd age=4679 idle=0 flags=x db=0 sub=0 psub=0 multi=4 qbuf=348 qbuf-free=40606 argv-mem=4 obl=45 oll=0 omem=0 tot-mem=61468 events=r cmd=exec user=default redir=-1')
19614:S 26 Oct 2021 15:47:10.448 # CONFIG REWRITE executed with success.
19614:S 26 Oct 2021 15:47:10.448 * Non blocking connect for SYNC fired the event.
19614:S 26 Oct 2021 15:47:10.448 * Master replied to PING, replication can continue...
19614:S 26 Oct 2021 15:47:10.449 * Trying a partial resynchronization (request 00bad10aad1b2ddde89253fd5c026c9a3925d7de:888237).
19614:S 26 Oct 2021 15:47:10.449 * Successful partial resynchronization with master.
19614:S 26 Oct 2021 15:47:10.449 # Master replication ID changed to a30062f1aa4f842d38eefa2d540933559733f5f3
19614:S 26 Oct 2021 15:47:10.449 * MASTER <-> REPLICA sync: Master accepted a Partial Resynchronization.
```
通过日志可以看到，当slave节点检测到master节点无法连接之后，一直在尝试重连master节点。
在大约10秒之后，m161p115成为了master节点。sentinel起作用了。
我们可以看到sentinel的日志：
```shell
28839:X 26 Oct 2021 15:21:10.110 * +sentinel sentinel 231bd2b57f330458efb37dd895b0b9cdac98c145 192.168.161.115 26379 @ mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:10.043 # +sdown master mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:10.099 # +odown master mymaster 192.168.161.114 6379 #quorum 2/2
28839:X 26 Oct 2021 15:47:10.099 # +new-epoch 1
28839:X 26 Oct 2021 15:47:10.099 # +try-failover master mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:10.102 # +vote-for-leader 90848ce683c46822b5c28b3f00c78d8f6c1a23b4 1
28839:X 26 Oct 2021 15:47:10.176 # 703d6ecb6eb8f3af512867fa31c8bd43a5cfce38 voted for 90848ce683c46822b5c28b3f00c78d8f6c1a23b4 1
28839:X 26 Oct 2021 15:47:10.184 # 231bd2b57f330458efb37dd895b0b9cdac98c145 voted for 90848ce683c46822b5c28b3f00c78d8f6c1a23b4 1
28839:X 26 Oct 2021 15:47:10.186 # +elected-leader master mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:10.186 # +failover-state-select-slave master mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:10.257 # +selected-slave slave 192.168.161.115:6379 192.168.161.115 6379 @ mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:10.257 * +failover-state-send-slaveof-noone slave 192.168.161.115:6379 192.168.161.115 6379 @ mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:10.324 * +failover-state-wait-promotion slave 192.168.161.115:6379 192.168.161.115 6379 @ mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:10.393 # +promoted-slave slave 192.168.161.115:6379 192.168.161.115 6379 @ mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:10.394 # +failover-state-reconf-slaves master mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:10.444 * +slave-reconf-sent slave 192.168.162.203:6379 192.168.162.203 6379 @ mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:11.267 # -odown master mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:11.415 * +slave-reconf-inprog slave 192.168.162.203:6379 192.168.162.203 6379 @ mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:11.415 * +slave-reconf-done slave 192.168.162.203:6379 192.168.162.203 6379 @ mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:11.474 # +failover-end master mymaster 192.168.161.114 6379
28839:X 26 Oct 2021 15:47:11.474 # +switch-master mymaster 192.168.161.114 6379 192.168.161.115 6379
28839:X 26 Oct 2021 15:47:11.474 * +slave slave 192.168.162.203:6379 192.168.162.203 6379 @ mymaster 192.168.161.115 6379
28839:X 26 Oct 2021 15:47:11.474 * +slave slave 192.168.161.114:6379 192.168.161.114 6379 @ mymaster 192.168.161.115 6379
28839:X 26 Oct 2021 15:47:41.516 # +sdown slave 192.168.161.114:6379 192.168.161.114 6379 @ mymaster 192.168.161.115 6379
```
也验证了这一点。首先对之前的master节点进行检测，达到超时时间之后，进行了master切换。
现在master节点为m161p115。


现在启动m161p114节点：
```shell
[root@m161p114 ~]# service redis_6379 start
/var/run/redis_6379.pid exists, process is already running or crashed
[root@m161p114 ~]# 
```
提示存在pid文件，这是由于之前是通过kill直接将进程杀掉，pid文件没回收。
因此需要将pid文件删除。
```shell
[root@m161p114 ~]# rm /var/run/redis_6379.pid
[root@m161p114 ~]# service redis_6379 start
Starting Redis server...
[root@m161p114 ~]# 
```
此时就能正常启动。
sentinel日志：
```shell
15545:X 26 Oct 2021 15:58:40.012 # -sdown slave 192.168.161.114:6379 192.168.161.114 6379 @ mymaster 192.168.161.115 6379
```
将sentinel中m161p114的sdown状态去掉。这样就启动成功了。
我们查看一下sentinel和redis节点的信息：
```shell
192.168.161.114:26379> info sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=mymaster,status=ok,address=192.168.161.115:6379,slaves=2,sentinels=3
192.168.161.114:26379>
[root@m161p114 ~]# redis-cli
127.0.0.1:6379> info replication
# Replication
role:slave
master_host:192.168.161.115
master_port:6379
master_link_status:up
master_last_io_seconds_ago:1
master_sync_in_progress:0
slave_read_repl_offset:1108080
slave_repl_offset:1108080
slave_priority:100
slave_read_only:1
replica_announced:1
connected_slaves:0
master_failover_state:no-failover
master_replid:a30062f1aa4f842d38eefa2d540933559733f5f3
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:1108080
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1038735
repl_backlog_histlen:69346
127.0.0.1:6379> 

```

# 5.springboot中java客户端的配置

采用sentinel之后，在springboot中，需要直接配置sentinel的地址和端口，这样当出现redis节点宕机时，就可以利用redis sentinel很好的实现高可用。
配置文件如下：
```yaml

server:
  port: 8080

#redis配置 
spring:
  redis:
    database: 0
    password: NULL
    timeout: 30000
    jedis:
      pool:
        max-wait: 30000
        max-active: -1
        max-idle: 20
        min-idle: 20
    lettuce:
      pool:
        max-wait: 30000
        max-active: 10
        max-idle: 8
        min-idle: 0
      shutdown-timeout: 100
      cache-null-values: false
    sentinel:
      master: mymaster
      nodes: 192.168.161.114:26379,192.168.161.115:26379,192.168.162.203:26379
```
实现一个rest打印info的接口：
```java
package com.dhb.cache;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SentinelTestController {
	
	@Autowired
	RedisTemplate redisTemplate;

	@RequestMapping(value="/testSentinel")
	@ResponseBody
	public String testSentinel() {
		String info =  redisTemplate.getRequiredConnectionFactory().getConnection().info().toString();
		log.info("jedis info: {}",info);
		return info;
	}
	
}

```