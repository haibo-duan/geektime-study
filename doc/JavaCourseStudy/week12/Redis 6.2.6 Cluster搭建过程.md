
# 1.环境准备
环境规划如下,准备了6个redis节点：

|hostname|ip|redis port|
|:----|:----|:----|
|m161p114|192.168.161.114|6380|
|m161p114|192.168.161.114|6381|
|m161p115|192.168.161.115|6380|
|m161p115|192.168.161.115|6381|
|m162p203|192.168.162.203|6380|
|m162p203|192.168.162.203|6381|

每个节点的安装过程详见 [Redis6.2.6在Centos7上的安装过程](../week11/Redis6.2.6在Centos7上的安装过程.md)

每个节点都需要修改配置文件的如下参数：
```yaml
#后台启动
daemonize yes 
#端口
port 6380
#集群配置
cluster-enabled yes
cluster-config-file nodes-6380.conf
cluster-node-timeout 15000
appendonly yes
```
规划集群的每个节点的配置文件都按上述格式进行修改。


# 2.ruby环境安装
redis的集群将采用utils中的redis-trib进行安装，这是一个ruby脚本。因此需要首先安装ruby的环境。
centos7 通过yum安装的ruby版本为2.0,这不符合redis-trib脚本的需要。
需要将ruby升级到至少2.4版本。
```shell
yum -y install ruby rubygem
```
安装的ruby版本：
```shell
[root@m161p114 ~]# ruby -v
ruby 2.0.0p648 (2015-12-16) [x86_64-linux]
```
ruby升级过程：
```shell
[root@m161p114 ~]#  yum install -y centos-release-scl-rh
[root@m161p114 ~]#  yum install -y rh-ruby24
[root@m161p114 ~]# scl enable rh-ruby24 bash
[root@m161p114 ~]# ruby -v
ruby 2.4.6p354 (2019-04-01 revision 67394) [x86_64-linux]
```
这样就将ruby版本升级到2.4版本。

此外还需要安装ruby的redis客户端。
```shell
[root@m161p114 ~]# gem install redis
Fetching: redis-4.5.1.gem (100%)
Successfully installed redis-4.5.1
Parsing documentation for redis-4.5.1
Installing ri documentation for redis-4.5.1
Done installing documentation for redis after 2 seconds
1 gem installed
[root@m161p114 ~]# 

```

# 3.redis cluster创建
现在通过redis-trib.rb 来创建集群：
```shell
[root@m161p114 redis-cluster]# mkdir /opt/redis-cluster
[root@m161p114 redis-cluster]# cp /opt/redis-6.2.6/src/redis-trib.rb  /opt/redis-cluster/
```
执行，创建集群：
```shell
[root@m161p114 redis-cluster]# ./redis-trib.rb create --replicas 1 192.168.161.114:6380 192.168.161.114:6381 192.168.161.115.6380 192.168.161.115:6381 192.168.162.203:6380 192.168.162.203:6381
WARNING: redis-trib.rb is not longer available!
You should use redis-cli instead.

All commands and features belonging to redis-trib.rb have been moved
to redis-cli.
In order to use them you should call redis-cli with the --cluster
option followed by the subcommand name, arguments and options.

Use the following syntax:
redis-cli --cluster SUBCOMMAND [ARGUMENTS] [OPTIONS]

Example:
redis-cli --cluster create 192.168.161.114:6380 192.168.161.114:6381 192.168.161.115.6380 192.168.161.115:6381 192.168.162.203:6380 192.168.162.203:6381 --cluster-replicas 1

To get help about all subcommands, type:
redis-cli --cluster help

[root@m161p114 redis-cluster]# 
```
创建过程中遇到了上述错误，仔细一看才发现，redis自从5.0版本之后，redis-cli已经包含了相关ruby脚本的功能，已经不需要通过ruby来创建集群了。

现在再次创建集群：
```shell
[root@m161p114 redis-cluster]# redis-cli --cluster create 192.168.161.114:6380 192.168.161.114:6381 192.168.161.115:6380 192.168.161.115:6381 192.168.162.203:6380 192.168.162.203:6381 --cluster-replicas 1
>>> Performing hash slots allocation on 6 nodes...
Master[0] -> Slots 0 - 5460
Master[1] -> Slots 5461 - 10922
Master[2] -> Slots 10923 - 16383
Adding replica 192.168.161.115:6381 to 192.168.161.114:6380
Adding replica 192.168.162.203:6381 to 192.168.161.115:6380
Adding replica 192.168.161.114:6381 to 192.168.162.203:6380
M: 4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380
   slots:[0-5460] (5461 slots) master
S: 74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381
   replicates 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac
M: 33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380
   slots:[5461-10922] (5462 slots) master
S: e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381
   replicates 4778a781cca83a0ff83737001c771c29669e8b98
M: 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380
   slots:[10923-16383] (5461 slots) master
S: b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381
   replicates 33e5f14bdf16bdea641a9d574fa47ab57fa0867e
Can I set the above configuration? (type 'yes' to accept): yes
>>> Nodes configuration updated
>>> Assign a different config epoch to each node
>>> Sending CLUSTER MEET messages to join the cluster
Waiting for the cluster to join

>>> Performing Cluster Check (using node 192.168.161.114:6380)
M: 4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380
   slots:[0-5460] (5461 slots) master
   1 additional replica(s)
S: e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381
   slots: (0 slots) slave
   replicates 4778a781cca83a0ff83737001c771c29669e8b98
M: 33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380
   slots:[5461-10922] (5462 slots) master
   1 additional replica(s)
S: b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381
   slots: (0 slots) slave
   replicates 33e5f14bdf16bdea641a9d574fa47ab57fa0867e
S: 74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381
   slots: (0 slots) slave
   replicates 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac
M: 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380
   slots:[10923-16383] (5461 slots) master
   1 additional replica(s)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
[root@m161p114 redis-cluster]# 
```

查看集群信息:
通过nodes命令能够查看集群中存在的节点。
```shell
192.168.161.114:6380> cluster nodes
e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381@16381 slave 4778a781cca83a0ff83737001c771c29669e8b98 0 1635318964386 1 connected
4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380@16380 myself,master - 0 1635318964000 1 connected 0-5460
33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380@16380 master - 0 1635318966465 3 connected 5461-10922
b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381@16381 slave 33e5f14bdf16bdea641a9d574fa47ab57fa0867e 0 1635318965425 3 connected
74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381@16381 slave 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 0 1635318967511 5 connected
6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380@16380 master - 0 1635318966000 5 connected 10923-16383
```
通过info可以查看集群的状态信息：
```shell
192.168.161.114:6380> cluster info
cluster_state:ok
cluster_slots_assigned:16384
cluster_slots_ok:16384
cluster_slots_pfail:0
cluster_slots_fail:0
cluster_known_nodes:6
cluster_size:3
cluster_current_epoch:6
cluster_my_epoch:1
cluster_stats_messages_ping_sent:3876
cluster_stats_messages_pong_sent:3788
cluster_stats_messages_sent:7664
cluster_stats_messages_ping_received:3783
cluster_stats_messages_pong_received:3876
cluster_stats_messages_meet_received:5
cluster_stats_messages_received:7664
192.168.161.114:6380> 
```

客户端连接测试,在client连接的时候，要加上参数 -c,这样client将以集群的方式连接redis。
```shell
[root@m161p114 redis-cluster]# redis-cli -h 192.168.161.114 -p 6380 -c
192.168.161.114:6380> set name redis
-> Redirected to slot [5798] located at 192.168.161.115:6380
OK
192.168.161.115:6380> get name
"redis"
192.168.161.115:6380> 
```
set name的时候，计算slot为5798，该槽位在192.168.161.115:6380节点。
client会自动切换到192.168.161.115:6380节点。

# 4.redis集群节点高可用测试
在上面cluster nodes的命令中，各节点的情况如下表：

|id|ip|port|status|slave of|slot|
|:----|:----|:----|:----|:----|:----|
|4778a781cca83a0ff83737001c771c29669e8b98|192.168.161.114|6380|master||0-5460|
|e0542c22b2512a108a763af0c118cae4253b9bdf|192.168.161.115|6381|slave|4778a781cca83a0ff83737001c771c29669e8b98||
|33e5f14bdf16bdea641a9d574fa47ab57fa0867e|192.168.161.115|6380|master||5461-10922|
|b4a6cde88f50bbe6cd1a6183818bdb173e50d92f|192.168.162.203|6381|slave|33e5f14bdf16bdea641a9d574fa47ab57fa0867e||
|74b837bf216c2908a5aaa703b4710fea0a313e13|192.168.161.203|6380|master|10923-16383|
|6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac|192.168.161.114|6381|slave|74b837bf216c2908a5aaa703b4710fea0a313e13||
可以看到，上述三个服务器上的两个redis节点，6380端口为master,而6381端口为slave节点。三个节点交叉备份。

现在测试一下集群的高可用，将节点192.168.161.115:6380 kill掉。看看node的变化：
```shell
[root@m161p115 ~]# ps -aux |grep redis
root     15380  0.1  0.1 137232 10856 ?        Ssl  Oct26   2:37 /usr/local/redis/bin/redis-server 0.0.0.0:6379
root     15545  0.2  0.1 162588  9908 ?        Ssl  Oct26   3:48 redis-sentinel *:26379 [sentinel]
root     17972  0.1  0.1 137232  9932 ?        Ssl  11:15   0:16 /usr/local/redis/bin/redis-server 0.0.0.0:6380 [cluster]
root     17991  0.0  0.1 147476  9900 ?        Ssl  11:15   0:15 /usr/local/redis/bin/redis-server 0.0.0.0:6381 [cluster]
haibo.d+ 18500  0.0  0.0 112816   948 pts/0    S+   15:45   0:00 grep --color=auto redis

[root@m161p115 ~]# kill -9 17972
```
集群日志信息：
```shell
28721:M 27 Oct 2021 14:10:52.788 * Synchronization with replica 192.168.161.114:6381 succeeded
28721:M 27 Oct 2021 14:10:56.704 # Cluster state changed: ok
28721:M 27 Oct 2021 15:47:13.844 * FAIL message received from e0542c22b2512a108a763af0c118cae4253b9bdf about 33e5f14bdf16bdea641a9d574fa47ab57fa0867e
28721:M 27 Oct 2021 15:47:13.844 # Cluster state changed: fail
28721:M 27 Oct 2021 15:47:14.549 # Failover auth granted to b4a6cde88f50bbe6cd1a6183818bdb173e50d92f for epoch 7
28721:M 27 Oct 2021 15:47:14.589 # Cluster state changed: ok
```
现在查看redis集群的node信息：
```shell
192.168.161.114:6380> cluster nodes
e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381@16381 slave 4778a781cca83a0ff83737001c771c29669e8b98 0 1635320942335 1 connected
4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380@16380 myself,master - 0 1635320944000 1 connected 0-5460
33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380@16380 master,fail - 1635320816651 1635320813000 3 disconnected
b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381@16381 master - 0 1635320945489 7 connected 5461-10922
74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381@16381 slave 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 0 1635320944452 5 connected
6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380@16380 master - 0 1635320944000 5 connected 10923-16383
192.168.161.114:6380> 
```
可以看到，节点192.168.162.203:6381升级为了master节点。槽位为5461-10922。
192.168.161.115:6380节点为fail状态。

现在我们启动192.168.161.115:6380节点，看看能否自动加入集群。
```shell
[root@m161p115 ~]# service redis_6380 start
/var/run/redis_6380.pid exists, process is already running or crashed
[root@m161p115 ~]# rm -rf /var/run/redis_6380.pid 
[root@m161p115 ~]# 
[root@m161p115 ~]# service redis_6380 start
```
启动后集群日志：
```shell
28721:M 27 Oct 2021 15:54:35.429 * Clear FAIL state for node 33e5f14bdf16bdea641a9d574fa47ab57fa0867e: master without slots is reachable again.
```
再次查看集群状态：
```shell
192.168.161.114:6380> cluster nodes
e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381@16381 slave 4778a781cca83a0ff83737001c771c29669e8b98 0 1635321291000 1 connected
4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380@16380 myself,master - 0 1635321289000 1 connected 0-5460
33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380@16380 slave b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 0 1635321289835 7 connected
b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381@16381 master - 0 1635321290900 7 connected 5461-10922
74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381@16381 slave 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 0 1635321287000 5 connected
6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380@16380 master - 0 1635321291929 5 connected 10923-16383
192.168.161.114:6380> 
```
可以看到，192.168.161.115:6380已经加入集群，自动成为了192.168.162.203:6381的slave节点。

也可以用check 命令进行检测：
```shell
[root@m161p114 ~]# redis-cli --cluster check 192.168.161.115:6380
192.168.162.203:6381 (b4a6cde8...) -> 1 keys | 5462 slots | 1 slaves.
192.168.162.203:6380 (6fd8fa50...) -> 0 keys | 5461 slots | 1 slaves.
192.168.161.114:6380 (4778a781...) -> 0 keys | 5461 slots | 1 slaves.
[OK] 1 keys in 3 masters.
0.00 keys per slot on average.
>>> Performing Cluster Check (using node 192.168.161.115:6380)
S: 33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380
   slots: (0 slots) slave
   replicates b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
M: b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381
   slots:[5461-10922] (5462 slots) master
   1 additional replica(s)
M: 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380
   slots:[10923-16383] (5461 slots) master
   1 additional replica(s)
S: e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381
   slots: (0 slots) slave
   replicates 4778a781cca83a0ff83737001c771c29669e8b98
S: 74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381
   slots: (0 slots) slave
   replicates 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac
M: 4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380
   slots:[0-5460] (5461 slots) master
   1 additional replica(s)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
```


# 5.redis cluster 增加节点
## 5.1 增加master节点
准备redis节点 192.168.161.114:6382 ，将该节点加入到cluster,并作为master节点。
增加节点操作，redis-cli --cluster add-node <加入node的ip:port> <被加入的集群的任意node的ip:port>
```shell
[root@m161p114 ~]# redis-cli --cluster add-node 192.168.161.114:6382 192.168.161.114:6380
>>> Adding node 192.168.161.114:6382 to cluster 192.168.161.114:6380
>>> Performing Cluster Check (using node 192.168.161.114:6380)
M: 4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380
   slots:[0-5460] (5461 slots) master
   1 additional replica(s)
S: e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381
   slots: (0 slots) slave
   replicates 4778a781cca83a0ff83737001c771c29669e8b98
S: 33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380
   slots: (0 slots) slave
   replicates b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
M: b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381
   slots:[5461-10922] (5462 slots) master
   1 additional replica(s)
S: 74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381
   slots: (0 slots) slave
   replicates 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac
M: 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380
   slots:[10923-16383] (5461 slots) master
   1 additional replica(s)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
>>> Send CLUSTER MEET to node 192.168.161.114:6382 to make it join the cluster.
[OK] New node added correctly.
```
之后通过check命令查看状态：
```shell
[root@m161p114 ~]# redis-cli --cluster check 192.168.161.115:6380
192.168.162.203:6381 (b4a6cde8...) -> 1 keys | 5462 slots | 1 slaves.
192.168.162.203:6380 (6fd8fa50...) -> 0 keys | 5461 slots | 1 slaves.
192.168.161.114:6382 (b01dfefa...) -> 0 keys | 0 slots | 0 slaves.
192.168.161.114:6380 (4778a781...) -> 0 keys | 5461 slots | 1 slaves.
[OK] 1 keys in 4 masters.
0.00 keys per slot on average.
>>> Performing Cluster Check (using node 192.168.161.115:6380)
S: 33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380
   slots: (0 slots) slave
   replicates b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
M: b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381
   slots:[5461-10922] (5462 slots) master
   1 additional replica(s)
M: 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380
   slots:[10923-16383] (5461 slots) master
   1 additional replica(s)
S: e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381
   slots: (0 slots) slave
   replicates 4778a781cca83a0ff83737001c771c29669e8b98
S: 74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381
   slots: (0 slots) slave
   replicates 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac
M: b01dfefa0b75cf9c04582bdebfe095bff5443c9c 192.168.161.114:6382
   slots: (0 slots) master
M: 4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380
   slots:[0-5460] (5461 slots) master
   1 additional replica(s)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
[root@m161p114 ~]# 
```
可以看到 192.168.161.114:6382 节点被加入到集群，作为一个master节点，但是并没有分配slot。
通过nodes也可以看到相关节点的状态：
```shell
[root@m161p114 ~]# redis-cli -h 192.168.161.114 -p 6380
192.168.161.114:6380> cluster nodes
e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381@16381 slave 4778a781cca83a0ff83737001c771c29669e8b98 0 1635323567824 1 connected
4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380@16380 myself,master - 0 1635323564000 1 connected 0-5460
33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380@16380 slave b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 0 1635323568827 7 connected
b01dfefa0b75cf9c04582bdebfe095bff5443c9c 192.168.161.114:6382@16382 master - 0 1635323565000 0 connected
b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381@16381 master - 0 1635323567000 7 connected 5461-10922
74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381@16381 slave 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 0 1635323566822 5 connected
6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380@16380 master - 0 1635323566000 5 connected 10923-16383
192.168.161.114:6380> 
```
如果要分配slot需要手动执行命令reshard。

```shell
redis-cli --cluster reshard <集群任意节点IP:port>
```
执行过程：
```shell
[root@m161p114 ~]# redis-cli --cluster reshard 192.168.161.114:6380
>>> Performing Cluster Check (using node 192.168.161.114:6380)
M: 4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380
   slots:[0-5460] (5461 slots) master
   1 additional replica(s)
S: e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381
   slots: (0 slots) slave
   replicates 4778a781cca83a0ff83737001c771c29669e8b98
S: 33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380
   slots: (0 slots) slave
   replicates b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
M: b01dfefa0b75cf9c04582bdebfe095bff5443c9c 192.168.161.114:6382
   slots: (0 slots) master
M: b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381
   slots:[5461-10922] (5462 slots) master
   1 additional replica(s)
S: 74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381
   slots: (0 slots) slave
   replicates 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac
M: 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380
   slots:[10923-16383] (5461 slots) master
   1 additional replica(s)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
```
第一个问题是，需要移动多少个slot？ 现在有4个节点，如果平均的话，每个节点的slot为 16384/4 = 4096
```shell
How many slots do you want to move (from 1 to 16384)? 4096
```
第二个问题： 输入接收slot的id ,查询得192.168.161.114：6382的slot为b01dfefa0b75cf9c04582bdebfe095bff5443c9c。
```shell
What is the receiving node ID? b01dfefa0b75cf9c04582bdebfe095bff5443c9c
```
第三个问题，是从指定节点移动，还是从全部节点移动。如果平均从全部节点移动，则输入all。
```shell
Please enter all the source node IDs.
  Type 'all' to use all the nodes as source nodes for the hash slots.
  Type 'done' once you entered all the source nodes IDs.
Source node #1: all
```
开始move过程：
```shell
Ready to move 4096 slots.
  Source nodes:
    M: 4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380
       slots:[0-5460] (5461 slots) master
       1 additional replica(s)
    M: b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381
       slots:[5461-10922] (5462 slots) master
       1 additional replica(s)
    M: 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380
       slots:[10923-16383] (5461 slots) master
       1 additional replica(s)
  Destination node:
    M: b01dfefa0b75cf9c04582bdebfe095bff5443c9c 192.168.161.114:6382
       slots: (0 slots) master
  Resharding plan:
    Moving slot 5461 from b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
    Moving slot 5462 from b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
    Moving slot 5463 from b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
    Moving slot 5464 from b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
    Moving slot 5465 from b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
    Moving slot 5466 from b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
    Moving slot 5467 from b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
    Moving slot 5468 from b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
    Moving slot 5469 from b4a6cde88f50bbe6cd1a6183818bdb173e50d92f

... ...
```

迁移完成之后，可以查看节点的状态：
```shell
root@m161p114 ~]# redis-cli --cluster check 192.168.161.115:6380
192.168.162.203:6381 (b4a6cde8...) -> 0 keys | 4096 slots | 1 slaves.
192.168.162.203:6380 (6fd8fa50...) -> 0 keys | 4096 slots | 1 slaves.
192.168.161.114:6382 (b01dfefa...) -> 1 keys | 4096 slots | 0 slaves.
192.168.161.114:6380 (4778a781...) -> 0 keys | 4096 slots | 1 slaves.
[OK] 1 keys in 4 masters.
0.00 keys per slot on average.
>>> Performing Cluster Check (using node 192.168.161.115:6380)
S: 33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380
   slots: (0 slots) slave
   replicates b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
M: b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381
   slots:[6827-10922] (4096 slots) master
   1 additional replica(s)
M: 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380
   slots:[12288-16383] (4096 slots) master
   1 additional replica(s)
S: e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381
   slots: (0 slots) slave
   replicates 4778a781cca83a0ff83737001c771c29669e8b98
S: 74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381
   slots: (0 slots) slave
   replicates 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac
M: b01dfefa0b75cf9c04582bdebfe095bff5443c9c 192.168.161.114:6382
   slots:[0-1364],[5461-6826],[10923-12287] (4096 slots) master
M: 4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380
   slots:[1365-5460] (4096 slots) master
   1 additional replica(s)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
[root@m161p114 ~]# 
```
可以看到，新加入的master节点，slot并不连续，这些slot分别来自其他三个node。
```shell
192.168.161.114:6380> cluster nodes
e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381@16381 slave 4778a781cca83a0ff83737001c771c29669e8b98 0 1635324782000 1 connected
4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380@16380 myself,master - 0 1635324783000 1 connected 1365-5460
33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380@16380 slave b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 0 1635324786738 7 connected
b01dfefa0b75cf9c04582bdebfe095bff5443c9c 192.168.161.114:6382@16382 master - 0 1635324783730 8 connected 0-1364 5461-6826 10923-12287
b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381@16381 master - 0 1635324784000 7 connected 6827-10922
74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381@16381 slave 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 0 1635324786000 5 connected
6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380@16380 master - 0 1635324784734 5 connected 12288-16383
192.168.161.114:6380> 

```
至此，主节点增加成功。

## 5.2 增加slave节点
同样，现在准备如下节点，192.168.161.115:6382节点。加入到cluster中，作为192.168.161.114:6382的slave节点。
```shell
[root@m161p115 utils]# ./install_server.sh 
Welcome to the redis service installer
This script will help you easily set up a running redis server

Please select the redis port for this instance: [6379] 6382
Please select the redis config file name [/etc/redis/6382.conf] 
Selected default - /etc/redis/6382.conf
Please select the redis log file name [/var/log/redis_6382.log] /opt/redis/log/^C
[root@m161p115 utils]# ./install_server.sh 
Welcome to the redis service installer
This script will help you easily set up a running redis server

Please select the redis port for this instance: [6379] 6382
Please select the redis config file name [/etc/redis/6382.conf] 
Selected default - /etc/redis/6382.conf
Please select the redis log file name [/var/log/redis_6382.log] /opt/redis/logs/redis_6382.log
Please select the data directory for this instance [/var/lib/redis/6382] /opt/redis/data/6382
Please select the redis executable path [/usr/local/redis/bin/redis-server] 
Selected config:
Port           : 6382
Config file    : /etc/redis/6382.conf
Log file       : /opt/redis/logs/redis_6382.log
Data dir       : /opt/redis/data/6382
Executable     : /usr/local/redis/bin/redis-server
Cli Executable : /usr/local/redis/bin/redis-cli
Is this ok? Then press ENTER to go on or Ctrl-C to abort.
Copied /tmp/6382.conf => /etc/init.d/redis_6382
Installing service...
Successfully added to chkconfig!
Successfully added to runlevels 345!
Starting Redis server...
Installation successful!
```
该节点配置文件按第一部分内描述进行修改。

加入slave节点的语法：
```shell
redis-cli --cluster add-node <待加入节点的ip:port> <集群中的任意节点node的IP:port>  --cluster-slave --cluster-master-id <主节点的id> 
```
如果--master-id不指定，那么将自动将节点加入后分配为slave最少的哪个节点，如果不存在最少，则自动分配。

```shell
[root@m161p114 ~]# redis-cli --cluster add-node 192.168.161.115:6382 192.168.161.114:6380 --cluster-slave --cluster-master-id b01dfefa0b75cf9c04582bdebfe095bff5443c9c
>>> Adding node 192.168.161.115:6382 to cluster 192.168.161.114:6380
>>> Performing Cluster Check (using node 192.168.161.114:6380)
M: 4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380
   slots:[1365-5460] (4096 slots) master
   1 additional replica(s)
S: e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381
   slots: (0 slots) slave
   replicates 4778a781cca83a0ff83737001c771c29669e8b98
S: 33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380
   slots: (0 slots) slave
   replicates b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
M: b01dfefa0b75cf9c04582bdebfe095bff5443c9c 192.168.161.114:6382
   slots:[0-1364],[5461-6826],[10923-12287] (4096 slots) master
M: b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381
   slots:[6827-10922] (4096 slots) master
   1 additional replica(s)
S: 74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381
   slots: (0 slots) slave
   replicates 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac
M: 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380
   slots:[12288-16383] (4096 slots) master
   1 additional replica(s)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
>>> Send CLUSTER MEET to node 192.168.161.115:6382 to make it join the cluster.
Waiting for the cluster to join

>>> Configure node as replica of 192.168.161.114:6382.
[OK] New node added correctly.
[root@m161p114 ~]# 

```
通过node可以查看，加入节点的状态：
```shell
[root@m161p114 ~]# redis-cli -h 192.168.161.114 -p 6380
192.168.161.114:6380> cluster nodes
e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381@16381 slave 4778a781cca83a0ff83737001c771c29669e8b98 0 1635325737000 1 connected
4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380@16380 myself,master - 0 1635325735000 1 connected 1365-5460
33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380@16380 slave b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 0 1635325737572 7 connected
f98ac1bcc47e90f9b0b9df104750ba07eb3b609c 192.168.161.115:6382@16382 slave b01dfefa0b75cf9c04582bdebfe095bff5443c9c 0 1635325740580 8 connected
b01dfefa0b75cf9c04582bdebfe095bff5443c9c 192.168.161.114:6382@16382 master - 0 1635325739578 8 connected 0-1364 5461-6826 10923-12287
b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381@16381 master - 0 1635325736000 7 connected 6827-10922
74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381@16381 slave 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 0 1635325739000 5 connected
6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380@16380 master - 0 1635325739000 5 connected 12288-16383

```
可以看到，对应节点的slave增加成功。


# 6.移除节点
## 6.1 移除slave节点
现在删除上述步骤添加的slave节点。语法为：
```shell
redis-cli --cluster del-node <集群中的任意node:port> <node ID>
```
移除节点相对比较简单，需要指定node的ID
```shell
[root@m161p115 redis]# redis-cli --cluster del-node 192.168.161.115:6380 f98ac1bcc47e90f9b0b9df104750ba07eb3b609c
>>> Removing node f98ac1bcc47e90f9b0b9df104750ba07eb3b609c from cluster 192.168.161.115:6380
>>> Sending CLUSTER FORGET messages to the cluster...
>>> Sending CLUSTER RESET SOFT to the deleted node.
[root@m161p115 redis]# 
```
现在可以发现，slave节点已经被移除：
```shell
192.168.161.114:6380> cluster nodes
e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381@16381 slave 4778a781cca83a0ff83737001c771c29669e8b98 0 1635326294000 1 connected
4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380@16380 myself,master - 0 1635326293000 1 connected 1365-5460
33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380@16380 slave b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 0 1635326294215 7 connected
b01dfefa0b75cf9c04582bdebfe095bff5443c9c 192.168.161.114:6382@16382 master - 0 1635326292000 8 connected 0-1364 5461-6826 10923-12287
b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381@16381 master - 0 1635326291000 7 connected 6827-10922
74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381@16381 slave 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 0 1635326293212 5 connected
6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380@16380 master - 0 1635326292000 5 connected 12288-16383
192.168.161.114:6380> 
```

## 6.2 移除master节点
移除主节点的语法如下：
```shell
redis-cli --cluster del-node <集群中的任意node:port> <node ID>
```
现在将192.168.161.114:6382移除：
```shell
[root@m161p115 redis]# redis-cli --cluster del-node 192.168.161.115:6380 b01dfefa0b75cf9c04582bdebfe095bff5443c9c
>>> Removing node b01dfefa0b75cf9c04582bdebfe095bff5443c9c from cluster 192.168.161.115:6380
[ERR] Node 192.168.161.114:6382 is not empty! Reshard data away and try again.
[root@m161p115 redis]# 
```
提示不为空不能移除。
这与增加主节点类似，只不过移除节点之前是将对应节点的slot全部转移到其他节点。
操作如下：
```shell
[root@m161p115 redis]# redis-cli --cluster reshard 192.168.161.114:6380
>>> Performing Cluster Check (using node 192.168.161.114:6380)
M: 4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380
   slots:[1365-5460] (4096 slots) master
   1 additional replica(s)
S: e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381
   slots: (0 slots) slave
   replicates 4778a781cca83a0ff83737001c771c29669e8b98
S: 33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380
   slots: (0 slots) slave
   replicates b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
M: b01dfefa0b75cf9c04582bdebfe095bff5443c9c 192.168.161.114:6382
   slots:[0-1364],[5461-6826],[10923-12287] (4096 slots) master
M: b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381
   slots:[6827-10922] (4096 slots) master
   1 additional replica(s)
S: 74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381
   slots: (0 slots) slave
   replicates 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac
M: 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380
   slots:[12288-16383] (4096 slots) master
   1 additional replica(s)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
```
第一个问题，需要移动多少个slot,192.168.161.114:6382节点中有4096个slot,因此这里输入4096
```shell
How many slots do you want to move (from 1 to 16384)? 4096
```
第二个问题，接收slot的node ID ? 这里没办法让全部节点都接收，只能指定一个节点。我们将4096个slot都移动到192.168.162.203:6381
```shell
What is the receiving node ID? b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
```
第三个问题，移动的slot来源，输入all则所有的节点都是source. 输入done,则表示挨个输入sourceID之后完成。在此我们输入192.168.161.114:6382的ID即可。
```shell
Please enter all the source node IDs.
  Type 'all' to use all the nodes as source nodes for the hash slots.
  Type 'done' once you entered all the source nodes IDs.
Source node #1: b01dfefa0b75cf9c04582bdebfe095bff5443c9c
Source node #2: done
```
开始移动：
```shell
Ready to move 4096 slots.
  Source nodes:
    M: b01dfefa0b75cf9c04582bdebfe095bff5443c9c 192.168.161.114:6382
       slots:[0-1364],[5461-6826],[10923-12287] (4096 slots) master
  Destination node:
    M: b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381
       slots:[6827-10922] (4096 slots) master
       1 additional replica(s)
  Resharding plan:
    Moving slot 0 from b01dfefa0b75cf9c04582bdebfe095bff5443c9c
    Moving slot 1 from b01dfefa0b75cf9c04582bdebfe095bff5443c9c
    Moving slot 2 from b01dfefa0b75cf9c04582bdebfe095bff5443c9c
    Moving slot 3 from b01dfefa0b75cf9c04582bdebfe095bff5443c9c
    Moving slot 4 from b01dfefa0b75cf9c04582bdebfe095bff5443c9c
    
    ... ...
```
现在可以查看，各节点的slot情况：
```shell
192.168.161.114:6380> cluster nodes
e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381@16381 slave 4778a781cca83a0ff83737001c771c29669e8b98 0 1635327440779 1 connected
4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380@16380 myself,master - 0 1635327438000 1 connected 1365-5460
33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380@16380 slave b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 0 1635327439000 9 connected
b01dfefa0b75cf9c04582bdebfe095bff5443c9c 192.168.161.114:6382@16382 master - 0 1635327442785 8 connected
b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381@16381 master - 0 1635327439000 9 connected 0-1364 5461-12287
74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381@16381 slave 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 0 1635327441782 5 connected
6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380@16380 master - 0 1635327440000 5 connected 12288-16383
192.168.161.114:6380> 
```
可以看到192.168.161.114:6382节点的slot全部被移除。现在移除该节点即可。
```shell
[root@m161p115 redis]# redis-cli --cluster del-node 192.168.161.115:6380 b01dfefa0b75cf9c04582bdebfe095bff5443c9c
>>> Removing node b01dfefa0b75cf9c04582bdebfe095bff5443c9c from cluster 192.168.161.115:6380
>>> Sending CLUSTER FORGET messages to the cluster...
>>> Sending CLUSTER RESET SOFT to the deleted node.
[root@m161p115 redis]# 

[root@m161p115 redis]# redis-cli --cluster check 192.168.161.115:6380
192.168.162.203:6381 (b4a6cde8...) -> 1 keys | 8192 slots | 1 slaves.
192.168.162.203:6380 (6fd8fa50...) -> 0 keys | 4096 slots | 1 slaves.
192.168.161.114:6380 (4778a781...) -> 0 keys | 4096 slots | 1 slaves.
[OK] 1 keys in 3 masters.
0.00 keys per slot on average.
>>> Performing Cluster Check (using node 192.168.161.115:6380)
S: 33e5f14bdf16bdea641a9d574fa47ab57fa0867e 192.168.161.115:6380
   slots: (0 slots) slave
   replicates b4a6cde88f50bbe6cd1a6183818bdb173e50d92f
M: b4a6cde88f50bbe6cd1a6183818bdb173e50d92f 192.168.162.203:6381
   slots:[0-1364],[5461-12287] (8192 slots) master
   1 additional replica(s)
M: 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac 192.168.162.203:6380
   slots:[12288-16383] (4096 slots) master
   1 additional replica(s)
S: e0542c22b2512a108a763af0c118cae4253b9bdf 192.168.161.115:6381
   slots: (0 slots) slave
   replicates 4778a781cca83a0ff83737001c771c29669e8b98
S: 74b837bf216c2908a5aaa703b4710fea0a313e13 192.168.161.114:6381
   slots: (0 slots) slave
   replicates 6fd8fa505dc11d4e0be1e72a9fd4edd7f86554ac
M: 4778a781cca83a0ff83737001c771c29669e8b98 192.168.161.114:6380
   slots:[1365-5460] (4096 slots) master
   1 additional replica(s)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
```
这样主节点移除完毕。

# 7.springboot 中客户端的配置
application.yml
```yaml

server:
  port: 8080

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
#    sentinel:
#      master: mymaster
#      nodes: 192.168.161.114:26379,192.168.161.115:26379,192.168.162.203:26379
    cluster:
      nodes: 192.168.161.114:6380,192.168.161.115:6380,192.168.162.203:6380,192.168.161.114:6381,192.168.161.115:6381,192.168.162.203:6381
      max-redirects: 3
```

restController:
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
public class ClusterTestController {
	
	@Autowired
	RedisTemplate redisTemplate;

	@RequestMapping(value="/testCluster")
	@ResponseBody
	public String testCluster() {
		String info =  redisTemplate.getRequiredConnectionFactory().getConnection().info().toString();
		log.info("cluster info: {}",info);
		return info;
	}
	
}
```
