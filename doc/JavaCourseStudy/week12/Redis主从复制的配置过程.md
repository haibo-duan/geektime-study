# 1.服务器信息
有两个已经启动的redis节点：

|机器名|IP|port|
|:----|:----|:----|
|m161p114|192.168.161.114|6379|
|m161p115|192.168.161.115|6379|

现在需要将上述redis节点配置为主从复制。

# 2. 主从复制的建立
在redis的配置文件中加上 slaveof <host> <port> 即可实现。
配置前，查看m161p114中的内容如下：
```shell
[root@m161p114 ~]$ redis-cli
127.0.0.1:6379> keys *
1) "\xac\xed\x00\x05t\x00\ateacher"
2) "uptime"
3) "teacher"
4) "\xac\xed\x00\x05t\x00\x06uptime"
5) "stock_key"
127.0.0.1:6379> 
```

如下，在服务器192.168.161.115节点的redis的配置文件中增加如下配置：
```lombok.config
slaveof 192.168.161.114 6379
```
之后重启redis服务：
```shell
[root@m161p115 redis]# service redis_6379 restart
Stopping ...
Waiting for Redis to shutdown ...
Redis stopped
Starting Redis server...
[root@m161p115 redis]# 
```
此时查看m161p115中的key:
```shell
[root@m161p115 redis]# redis-cli
127.0.0.1:6379> keys *
1) "\xac\xed\x00\x05t\x00\ateacher"
2) "stock_key"
3) "\xac\xed\x00\x05t\x00\x06uptime"
4) "uptime"
5) "teacher"
127.0.0.1:6379> 
```
这与m161p114中的内容一致。这说明配置生效，启动从库数据会直接同步。
此后，从库m161p115将变为只读状态，无法再set内容：
```shell
127.0.0.1:6379> set testkey testvalue
(error) READONLY You can't write against a read only replica.
127.0.0.1:6379> 
```
# 3. 命令行的方式实现主从复制
将配置文件中新增的slaveof 注释掉，再重启redis,则主从复制就会关闭，不过从库中的数据不会清除。
当然，主从复制也可以不在配置文件中配置，而直接在命令行中执行命令：
```shell
[root@m161p115 redis]# redis-cli 
127.0.0.1:6379> slaveof 192.168.161.114 6379
OK
127.0.0.1:6379> keys *
1) "teacher"
2) "uptime"
3) "\xac\xed\x00\x05t\x00\x06uptime"
4) "\xac\xed\x00\x05t\x00\ateacher"
5) "stock_key"
127.0.0.1:6379> 

```
这样数据就会同步过来。
通过info可以看到主从建立成功：
```properties
# Replication
role:slave
master_host:192.168.161.114
master_port:6379
master_link_status:up
master_last_io_seconds_ago:9
master_sync_in_progress:0
slave_read_repl_offset:84098
slave_repl_offset:84098
slave_priority:100
slave_read_only:1
replica_announced:1
connected_slaves:0
master_failover_state:no-failover
master_replid:d53ec7326b4359d8820a99246918dbb5aa5a2145
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:84098
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:84043
repl_backlog_histlen:56
```

从节点的断开
在从节点执行，slaveof no one 即可。
```shell
127.0.0.1:6379> slaveof no one
OK
```
之后从节点就会变成master状态，但是数据不会清除。如果要清除数据，需要执行flashall
从建立主从复制到断开过程的日制：
```
14608:S 26 Oct 2021 10:38:57.278 * Before turning into a replica, using my own master parameters to synthesize a cached master: I may be able to synchronize with the new master with just a partial transfer.
14608:S 26 Oct 2021 10:38:57.278 * Connecting to MASTER 192.168.161.114:6379
14608:S 26 Oct 2021 10:38:57.278 * MASTER <-> REPLICA sync started
14608:S 26 Oct 2021 10:38:57.278 * REPLICAOF 192.168.161.114:6379 enabled (user request from 'id=7 addr=127.0.0.1:5780 laddr=127.0.0.1:6379 fd=7 name= age=34 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=49 qbuf-free=40905 argv-mem=26 obl=0 oll=0 omem=0 tot-mem=61490 events=r cmd=slaveof user=default redir=-1')
14608:S 26 Oct 2021 10:38:57.278 * Non blocking connect for SYNC fired the event.
14608:S 26 Oct 2021 10:38:57.279 * Master replied to PING, replication can continue...
14608:S 26 Oct 2021 10:38:57.279 * Trying a partial resynchronization (request e36775cb68d63e65da9676d165b2bae931a04d9e:84393).
14608:S 26 Oct 2021 10:38:57.283 * Full resync from master: d53ec7326b4359d8820a99246918dbb5aa5a2145:84392
14608:S 26 Oct 2021 10:38:57.283 * Discarding previously cached master state.
14608:S 26 Oct 2021 10:38:57.646 * MASTER <-> REPLICA sync: receiving 307 bytes from master to disk
14608:S 26 Oct 2021 10:38:57.646 * MASTER <-> REPLICA sync: Flushing old data
14608:S 26 Oct 2021 10:38:57.646 * MASTER <-> REPLICA sync: Loading DB in memory
14608:S 26 Oct 2021 10:38:57.647 * Loading RDB produced by version 6.2.6
14608:S 26 Oct 2021 10:38:57.647 * RDB age 0 seconds
14608:S 26 Oct 2021 10:38:57.647 * RDB memory usage when created 1.83 Mb
14608:S 26 Oct 2021 10:38:57.647 # Done loading RDB, keys loaded: 5, keys expired: 0.
14608:S 26 Oct 2021 10:38:57.648 * MASTER <-> REPLICA sync: Finished with success
14608:M 26 Oct 2021 10:39:11.264 # Connection with master lost.
14608:M 26 Oct 2021 10:39:11.264 * Caching the disconnected master state.
14608:M 26 Oct 2021 10:39:11.264 * Discarding previously cached master state.
14608:M 26 Oct 2021 10:39:11.264 # Setting secondary replication ID to d53ec7326b4359d8820a99246918dbb5aa5a2145, valid up to offset: 84421. New replication ID is d5c02b0e97aedfcccd0d3ef9ff6c2d6e454e0f29
14608:M 26 Oct 2021 10:39:11.264 * MASTER MODE enabled (user request from 'id=7 addr=127.0.0.1:5780 laddr=127.0.0.1:6379 fd=7 name= age=48 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=34 qbuf-free=40920 argv-mem=12 obl=0 oll=0 omem=0 tot-mem=61476 events=r cmd=slaveof user=default redir=-1')
```
