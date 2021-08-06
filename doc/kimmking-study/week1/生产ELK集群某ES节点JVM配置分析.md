
# 1.硬件配置
分析了公司在线某ELK日志集群的某个ES节点的jvm信息：
操作系统；
```
LSB Version:	:base-4.0-amd64:base-4.0-noarch:core-4.0-amd64:core-4.0-noarch:graphics-4.0-amd64:graphics-4.0-noarch:printing-4.0-amd64:printing-4.0-noarch
Distributor ID:	CentOS
Description:	CentOS release 6.5 (Final)
Release:	6.5
Codename:	Final
```
内存：
```
[haibo.duan@m21p89 ~]$ free -g
             total       used       free     shared    buffers     cached
Mem:           141        119         22          0          0         20
-/+ buffers/cache:         98         42
Swap:            0          0          0
```
内存总计。
CPU:
```
[haibo.duan@m21p89 ~]$ cat /proc/cpuinfo | grep name | cut -f2 -d: | uniq -c
     24  Intel(R) Xeon(R) CPU E5-2620 v2 @ 2.10GHz
```
该服务器有24个逻辑核心。
磁盘信息：
```
[haibo.duan@m21p89 ~]$ df -lh
Filesystem      Size  Used Avail Use% Mounted on
/dev/sda2       286G  7.2G  264G   3% /
tmpfs            79G     0   79G   0% /dev/shm
/dev/sda1       291M   34M  243M  13% /boot
/dev/sdb1       7.0T  359G  6.7T   6% /opt

[root@m21p120 ~]# fdisk -lu

Disk /dev/sda: 322.1 GB, 322122547200 bytes
255 heads, 63 sectors/track, 39162 cylinders, total 629145600 sectors
Units = sectors of 1 * 512 = 512 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disk identifier: 0x00044020

   Device Boot      Start         End      Blocks   Id  System
/dev/sda1   *        2048      616447      307200   83  Linux
Partition 1 does not end on cylinder boundary.
/dev/sda2          616448   608174079   303778816   83  Linux
/dev/sda3       608174080   629145599    10485760   82  Linux swap / Solaris

WARNING: GPT (GUID Partition Table) detected on '/dev/sdb'! The util fdisk doesn't support GPT. Use GNU Parted.


Disk /dev/sdb: 7678.3 GB, 7678327783424 bytes
255 heads, 63 sectors/track, 933503 cylinders, total 14996733952 sectors
Units = sectors of 1 * 512 = 512 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disk identifier: 0x00000000

   Device Boot      Start         End      Blocks   Id  System
/dev/sdb1               1  4294967295  2147483647+  ee  GPT

```
scsi信息
```
[haibo.duan@m21p89 ~]$ cat /proc/scsi/scsi
Attached devices:
Host: scsi0 Channel: 02 Id: 00 Lun: 00
  Vendor: DELL     Model: PERC H710        Rev: 3.13
  Type:   Direct-Access                    ANSI  SCSI revision: 05
Host: scsi0 Channel: 02 Id: 01 Lun: 00
  Vendor: DELL     Model: PERC H710        Rev: 3.13
  Type:   Direct-Access                    ANSI  SCSI revision: 05

```
由于该服务器没有进一步的权限，无法确认其具体的raid和磁盘信息。
根据磁盘大小以及询问相关运维同事，可以大致可以推测该服务器配置应该是通用的大数据数据存储节点。数据磁盘为普通的机械硬盘。

该节点是生产环境elk的一个数据节点。

# 2.jvm相关工具进行分析
## 2.1 jps
jps查看进程：
```
[elastic@m21p89 ~]$ jps -mlv
15184 sun.tools.jps.Jps -mlv -Denv.class.path=:/opt/java/lib -Dapplication.home=/usr/java/jdk1.8.0_101 -Xms8m
49091 org.elasticsearch.bootstrap.Elasticsearch -d -Xms30g -Xmx30g -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:+AlwaysPreTouch -XX:+DisableExplicitGC -Xss1m -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djna.nosys=true -Djdk.io.permissionsUseCanonicalPath=true -Dio.netty.noUnsafe=true -Dio.netty.noKeySetOptimization=true -Dio.netty.recycler.maxCapacityPerThread=0 -Dlog4j.shutdownHookEnabled=false -Dlog4j2.disable.jmx=true -Dlog4j.skipJansi=true -XX:+HeapDumpOnOutOfMemoryError -Des.path.home=/opt/elasticsearch/node4-1
42167 org.elasticsearch.bootstrap.Elasticsearch -d -Xms30g -Xmx30g -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:+AlwaysPreTouch -XX:+DisableExplicitGC -Xss1m -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djna.nosys=true -Djdk.io.permissionsUseCanonicalPath=true -Dio.netty.noUnsafe=true -Dio.netty.noKeySetOptimization=true -Dio.netty.recycler.maxCapacityPerThread=0 -Dlog4j.shutdownHookEnabled=false -Dlog4j2.disable.jmx=true -Dlog4j.skipJansi=true -XX:+HeapDumpOnOutOfMemoryError -Des.path.home=/opt/elasticsearch/node4-3
43772 org.elasticsearch.bootstrap.Elasticsearch -d -Xms30g -Xmx30g -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:+AlwaysPreTouch -XX:+DisableExplicitGC -Xss1m -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djna.nosys=true -Djdk.io.permissionsUseCanonicalPath=true -Dio.netty.noUnsafe=true -Dio.netty.noKeySetOptimization=true -Dio.netty.recycler.maxCapacityPerThread=0 -Dlog4j.shutdownHookEnabled=false -Dlog4j2.disable.jmx=true -Dlog4j.skipJansi=true -XX:+HeapDumpOnOutOfMemoryError -Des.path.home=/opt/elasticsearch/elasticsearch-node4-2
```
可以得到如下信息：
- 该节点部署了3个ES节点，堆内存都为30G，且大小一致。stack大小为1M。（-Xms30g -Xmx30g  -Xss1m ）
- 该节点老年代采用了cms收集器，（-XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly）内存占用达到堆内存的75%开始GC,这个比例仅仅适用回收阈值。
- -XX:+DisableExplicitGC 关闭了system.gc 由于是es集群而不是自定义的代码，这个配置有无风险暂时未知。
- -XX:AlwaysPreTouch 内存直接分配给物理内存。

## 2.2 jstat
jstat
```
[elastic@m21p89 ~]$ jstat -gc 49091 1000
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT   
153344.0 153344.0  0.0   70928.3 1226816.0 633150.1 29923776.0 2179671.4  74816.0 68376.3 10584.0 8684.2 110540 5519.943  143    57.995 5577.937
153344.0 153344.0  0.0   70928.3 1226816.0 739859.7 29923776.0 2179671.4  74816.0 68376.3 10584.0 8684.2 110540 5519.943  143    57.995 5577.937
153344.0 153344.0  0.0   70928.3 1226816.0 743371.3 29923776.0 2179671.4  74816.0 68376.3 10584.0 8684.2 110540 5519.943  143    57.995 5577.937
153344.0 153344.0  0.0   70928.3 1226816.0 750174.8 29923776.0 2179671.4  74816.0 68376.3 10584.0 8684.2 110540 5519.943  143    57.995 5577.937
153344.0 153344.0  0.0   70928.3 1226816.0 833028.2 29923776.0 2179671.4  74816.0 68376.3 10584.0 8684.2 110540 5519.943  143    57.995 5577.937
```
通过jstat查看：

| 指标 | 取值        | 说明          |
|:-----|:-----------|:-------------|
| S0C  | 153344.0   | 幸存区0大小    |
| S1C  | 153344.0   | 幸存区1大小    |
| S0U  | 0          | 幸存区0使用    |
| S1U  | 70928.3    | 幸存区1使用    |
| EC   | 1226816.0  | 年轻代大小     |
| EU   | 633150.1   | 年轻代使用     |
| OC   | 29923776.0 | 老年代大小     |
| OU   | 2179671.4  | 老年代使用     |
| MC   | 74816.0    | MetaSpace大小 |
| MU   | 68376.3    | MetaSpace使用 |
| CCSC | 10584.0    | 压缩类空间大小 |
| CCSU | 8684.2     | 压缩类空间使用 |
| YGC  | 110540     | 年轻代GC次数   |
| YGCT | 5519.943   | 年轻代GC总耗时 |
| FGC  | 143        | FullGC次数    |
| FGCT | 57.995     | FullGC耗时    |
| GCT  | 5577.937   | GC总耗时      |

通过上表可以得到：
- 老年代约28.5G。年轻代约1.5G Metaspace 约70M CompressedClassSpaceSize 约1G
- 老年代使用率 约6.10% 
- Metaspace使用率 91.39%
- 年轻代GC平均时间 约49ms
- 老年代GC平均时间 约398ms

## 2.3 jstack

通过jstack命令分析：
```
elastic@m21p89 ~]$ /opt/java/bin/jstack   49091
2021-08-06 20:27:19
Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.191-b12 mixed mode):

"Attach Listener" #331 daemon prio=9 os_prio=0 tid=0x00007f2c8c001000 nid=0x8842 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"elasticsearch[node4-1][generic][T#64]" #321 daemon prio=5 os_prio=0 tid=0x00007f2c04074800 nid=0x11e0 waiting on condition [0x00007f29c72f1000]
   java.lang.Thread.State: TIMED_WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x00000000e199e5f8> (a org.elasticsearch.common.util.concurrent.EsExecutors$ExecutorScalingQueue)
	at java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)
	at java.util.concurrent.LinkedTransferQueue.awaitMatch(LinkedTransferQueue.java:734)
	at java.util.concurrent.LinkedTransferQueue.xfer(LinkedTransferQueue.java:647)
	at java.util.concurrent.LinkedTransferQueue.poll(LinkedTransferQueue.java:1277)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1073)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)

"elasticsearch[node4-1][generic][T#48]" #305 daemon prio=5 os_prio=0 tid=0x00007f2c04055800 nid=0x11b1 waiting on condition [0x00007f29cc3c4000]
   java.lang.Thread.State: TIMED_WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x00000000e199e5f8> (a org.elasticsearch.common.util.concurrent.EsExecutors$ExecutorScalingQueue)
	at java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)
	at java.util.concurrent.LinkedTransferQueue.awaitMatch(LinkedTransferQueue.java:734)
	at java.util.concurrent.LinkedTransferQueue.xfer(LinkedTransferQueue.java:647)
	at java.util.concurrent.LinkedTransferQueue.poll(LinkedTransferQueue.java:1277)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1073)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)

"elasticsearch[node4-1][generic][T#27]" #284 daemon prio=5 os_prio=0 tid=0x00007f2c04030000 nid=0x119c waiting on condition [0x00007f29cd8d9000]
   java.lang.Thread.State: TIMED_WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x00000000e199e5f8> (a org.elasticsearch.common.util.concurrent.EsExecutors$ExecutorScalingQueue)
	at java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)
	at java.util.concurrent.LinkedTransferQueue.awaitMatch(LinkedTransferQueue.java:734)
	at java.util.concurrent.LinkedTransferQueue.xfer(LinkedTransferQueue.java:647)
	at java.util.concurrent.LinkedTransferQueue.poll(LinkedTransferQueue.java:1277)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1073)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)

"elasticsearch[node4-1][generic][T#13]" #270 daemon prio=5 os_prio=0 tid=0x00007f2c04017000 nid=0x118e waiting on condition [0x00007f29cedee000]
```
死锁检测，系统不存在死锁，由于线程比较多，没有复制全部打印信息。

对各线程统计如下表：

| 线程状态       | 数量 |
|:--------------|:----|
| TIMED_WAITING       | 15  |
| WAITING     | 159 |
| RUNNABLE | 163  |

通过-L参数可以检测死锁：
```
[elastic@m21p89 ~]$ /opt/java/bin/jstack  -F  49091
Attaching to process ID 49091, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.191-b12
Deadlock Detection:

No deadlocks found.

Thread 34882: (state = BLOCKED)


Thread 4576: (state = BLOCKED)
 - sun.misc.Unsafe.park(boolean, long) @bci=0 (Compiled frame; information may be imprecise)
 - java.util.concurrent.locks.LockSupport.parkNanos(java.lang.Object, long) @bci=20, line=215 (Compiled frame)
 - java.util.concurrent.LinkedTransferQueue.awaitMatch(java.util.concurrent.LinkedTransferQueue$Node, java.util.concurrent.LinkedTransferQueue$Node, java.lang.Object, boolean, long) @bci=177, line=734 (Compiled frame)
 - java.util.concurrent.LinkedTransferQueue.xfer(java.lang.Object, boolean, int, long) @bci=286, line=647 (Compiled frame)
 - java.util.concurrent.LinkedTransferQueue.poll(long, java.util.concurrent.TimeUnit) @bci=9, line=1277 (Compiled frame)
 - java.util.concurrent.ThreadPoolExecutor.getTask() @bci=134, line=1073 (Compiled frame)
 - java.util.concurrent.ThreadPoolExecutor.runWorker(java.util.concurrent.ThreadPoolExecutor$Worker) @bci=26, line=1134 (Compiled frame)
 - java.util.concurrent.ThreadPoolExecutor$Worker.run() @bci=5, line=624 (Interpreted frame)
 - java.lang.Thread.run() @bci=11, line=748 (Interpreted frame)

```
可见，没有死锁发生。

## 2.4 jmap
jmap 查看 heap
```
[elastic@m21p89 ~]$ /opt/java/bin/jmap -heap  49091
Attaching to process ID 49091, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.191-b12

using parallel threads in the new generation.
using thread-local object allocation.
Concurrent Mark-Sweep GC

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 32212254720 (30720.0MB)
   NewSize                  = 1570308096 (1497.5625MB)
   MaxNewSize               = 1570308096 (1497.5625MB)
   OldSize                  = 30641946624 (29222.4375MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
New Generation (Eden + 1 Survivor Space):
   capacity = 1413283840 (1347.8125MB)
   used     = 1230480168 (1173.4773330688477MB)
   free     = 182803672 (174.33516693115234MB)
   87.06532496685168% used
Eden Space:
   capacity = 1256259584 (1198.0625MB)
   used     = 1185957056 (1131.0167846679688MB)
   free     = 70302528 (67.04571533203125MB)
   94.40382155922323% used
From Space:
   capacity = 157024256 (149.75MB)
   used     = 44523112 (42.460548400878906MB)
   free     = 112501144 (107.2894515991211MB)
   28.354289416279737% used
To Space:
   capacity = 157024256 (149.75MB)
   used     = 0 (0.0MB)
   free     = 157024256 (149.75MB)
   0.0% used
concurrent mark-sweep generation:
   capacity = 30641946624 (29222.4375MB)
   used     = 2242263936 (2138.3895263671875MB)
   free     = 28399682688 (27084.047973632812MB)
   7.317628881462019% used

38198 interned Strings occupying 4587120 bytes.

```

jmap查看
```
[elastic@m21p89 ~]$ /opt/java/bin/jmap -histo:live   49091

 num     #instances         #bytes  class name
----------------------------------------------
   1:         67308     1740483792  [B
   2:        157507       28853632  [Ljava.lang.Object;
   3:        313695       26050552  [C
   4:        306215        7349160  java.lang.String
   5:        126319        3031656  java.util.ArrayList
   6:        125503        3012072  java.util.Collections$UnmodifiableRandomAccessList
   7:         84834        2714688  java.util.HashMap$Node
   8:         14761        2543136  [I
   9:         39479        2526656  org.elasticsearch.cluster.routing.ShardRouting
  10:         23867        2416512  [Ljava.util.HashMap$Node;
  11:         46088        1843520  java.util.LinkedHashMap$Entry
  12:         14053        1541376  java.lang.Class
  13:         37672        1506880  java.util.TreeMap$Entry
  14:         59217        1421208  java.util.Collections$SingletonList
  15:         27785        1333680  java.util.HashMap
  16:         19738        1263232  org.elasticsearch.cluster.routing.IndexShardRoutingTable
  17:           461        1220960  [Ljava.nio.ByteBuffer;
  18:         34567        1106144  java.util.concurrent.ConcurrentHashMap$Node
  19:         39479         947496  org.elasticsearch.cluster.routing.AllocationId
  20:           288         804864  [Lio.netty.buffer.PoolSubpage;
  21:         47603         761648  java.lang.Object
  22:         10849         607544  com.carrotsearch.hppc.ObjectObjectHashMap
  23:         22501         540024  java.util.concurrent.atomic.AtomicLong
  24:         10725         514800  org.joda.time.format.DateTimeFormatter
  25:         19738         473712  org.elasticsearch.index.shard.ShardId
  26:         11232         449280  io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscAtomicArrayQueue
  27:          7646         428176  java.lang.invoke.MemberName
  28:            48         407608  [D
  29:          7152         400512  com.carrotsearch.hppc.IntObjectHashMap
  30:          5689         389800  [J
  31:         16052         387440  [Lorg.joda.time.format.InternalParser;
  32:         23772         380352  java.util.Collections$UnmodifiableSet
  33:         23612         377792  java.util.concurrent.atomic.AtomicInteger
  34:         23313         373008  org.elasticsearch.cluster.routing.RotationShardShuffler
  35:          3576         371904  org.elasticsearch.cluster.metadata.IndexMetaData
  36:         10721         343072  org.joda.time.format.DateTimeFormatterBuilder$Composite
  37:         10368         331776  io.netty.buffer.PoolThreadCache$SubPageMemoryRegionCache
  38:         20596         329536  java.util.HashSet
  39:          5147         329408  io.netty.buffer.PoolSubpage
  40:         20104         321664  java.util.HashMap$KeySet
  41:           138         277408  [Ljava.util.concurrent.ConcurrentHashMap$Node;
  42:          1392         272088  [Ljava.lang.String;
  43:         10683         258376  [Lorg.joda.time.format.InternalPrinter;
  44:          3931         251584  java.util.concurrent.ConcurrentHashMap
  45:          5794         231760  java.lang.invoke.MethodType
  46:          6954         222528  java.util.concurrent.atomic.LongAdder
  47:          6199         198368  java.util.Hashtable$Entry
  48:          5795         185440  java.lang.invoke.MethodType$ConcurrentWeakInternSet$WeakEntry
  49:          3851         184848  java.util.TreeMap
  50:          7651         183624  org.elasticsearch.index.Index
  51:          7613         182712  org.apache.lucene.util.SetOnce
  52:         11233         179728  java.util.concurrent.atomic.AtomicReferenceArray
  53:         10849         173584  org.elasticsearch.common.collect.ImmutableOpenMap
  54:          4288         171520  java.lang.ref.SoftReference
  55:         10634         170144  org.joda.time.format.InternalParserDateTimeParser
  56:          5295         169440  org.elasticsearch.common.joda.FormatDateTimeFormatter
  57:          5288         169216  org.elasticsearch.cluster.metadata.MappingMetaData$Timestamp
  58:          5287         169184  org.elasticsearch.cluster.metadata.MappingMetaData
  59:          5023         160736  java.lang.StackTraceElement
  60:          5016         140480  [Ljava.lang.Class;
  61:          2894         138912  org.elasticsearch.painless.Definition$Method
  62:          4057         129824  java.lang.invoke.DirectMethodHandle
  63:          5331         127944  org.joda.time.format.DateTimeFormatterBuilder$MatchingParser
  64:          7991         127856  java.util.concurrent.atomic.AtomicBoolean
  65:          5327         127848  org.elasticsearch.common.compress.CompressedXContent
  66:          5030         120720  org.apache.logging.log4j.core.impl.ExtendedClassInfo
  67:          5030         120720  org.apache.logging.log4j.core.impl.ExtendedStackTraceElement
  68:          1648         118656  io.netty.channel.DefaultChannelHandlerContext
  69:          3633         116256  java.util.Collections$UnmodifiableSortedMap
  70:          3628         116096  org.elasticsearch.common.settings.Settings
  71:          7152         114432  org.elasticsearch.common.collect.ImmutableOpenIntMap
  72:          7150         114400  org.elasticsearch.cluster.metadata.AliasOrIndex$Index
```
可以查看堆对象中的统计信息。



# 3.总结
通过jvm的内置工具的分析，可以发现的问题有：
- JVM堆内存设置过大，这会导致严重的资源浪费，结合当前系统的情况可以发现该节点负载不饱和，还很空闲。可以回收资源给其他系统使用。如果资源有冗余，那么GC配置过大也会导致在系统负载上来之后的GC时间过长。虽然本案例中的GC世界并不长。
- JVM的年轻代和老年代的比例设置会有问题，最好手动指定，当前的比例会造成年轻代过小从而年轻代的GC频繁。
- MetaSpace空间不足，使用率达到90%，应该适当增加MetaSpace的空间。
