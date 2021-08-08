将测试环境的ES节点的GC方式改为G1：
# 1.环境信息
```
[elastic@m162p201 node201]$ free -g
              total        used        free      shared  buff/cache   available
Mem:              7           6           0           0           0           1
Swap:             3           1           2
```
内存为8g,由于该测试服务器为虚拟机，且部署了较多的java应用，因此内存相对比较紧张。

# 2.java内置工具分析
## 2.1  jps
```
[elastic@m162p201 node201]$ jps -mlv 
14321 org.elasticsearch.bootstrap.Elasticsearch -d -Xshare:auto -Des.networkaddress.cache.ttl=60 -Des.networkaddress.cache.negative.ttl=10 -XX:+AlwaysPreTouch -Xss1m -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djna.nosys=true -XX:-OmitStackTraceInFastThrow -Dio.netty.noUnsafe=true -Dio.netty.noKeySetOptimization=true -Dio.netty.recycler.maxCapacityPerThread=0 -Dio.netty.allocator.numDirectArenas=0 -Dlog4j.shutdownHookEnabled=false -Dlog4j2.disable.jmx=true -Djava.locale.providers=SPI,JRE -Djava.io.tmpdir=/tmp/elasticsearch-7287742518305094940 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=data -XX:ErrorFile=logs/hs_err_pid%p.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -XX:+PrintGCApplicationStoppedTime -Xloggc:logs/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=32 -XX:GCLogFileSize=64m -Xms2g -Xmx2g -XX:+UseG1GC -XX:+PrintGCDetails -XX:MaxDirectMemorySize=1073741824 -Des.path.home=/home/elastic/node201 -Des.path.conf=/home/elastic/node201/config -Des.distribution.flavor=default -Des.distribution.type=tar -Des.b
```
通过jps信息，可以知道如下信息：
- java堆内存为2G(-Xms2g -Xmx2g)
- java线程栈为1M(-Xss1m)
- 最大的直接内存大小：-XX:MaxDirectMemorySize=1073741824 = 1G,当超过1G则触发FullGC
- GC方式：G1(-XX:+UseG1GC -XX:+PrintGCDetails)
其他GC参数未指定，将采用默认值

## 2.2 jstat
```
[elastic@m162p201 node201]$ jstat -gc 14321 1000
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT   
 0.0   18432.0  0.0   18432.0 1302528.0  7168.0   776192.0   81277.5   128664.0 112788.6 19968.0 15611.1     19    0.957   0      0.000    0.957
 0.0   18432.0  0.0   18432.0 1302528.0  7168.0   776192.0   81277.5   128664.0 112788.6 19968.0 15611.1     19    0.957   0      0.000    0.957
 0.0   18432.0  0.0   18432.0 1302528.0 33792.0   776192.0   81277.5   128664.0 112788.6 19968.0 15611.1     19    0.957   0      0.000    0.957
 0.0   18432.0  0.0   18432.0 1302528.0 33792.0   776192.0   81277.5   128664.0 112788.6 19968.0 15611.1     19    0.957   0      0.000    0.957
 0.0   18432.0  0.0   18432.0 1302528.0 33792.0   776192.0   81277.5   128664.0 112788.6 19968.0 15611.1     19    0.957   0      0.000    0.957
```
通过jstat查看：

| 指标 | 取值        | 说明          |
|:-----|:-----------|:-------------|
| S0C  | 0.0   | 幸存区0大小    |
| S1C  | 18432.0   | 幸存区1大小    |
| S0U  | 0          | 幸存区0使用    |
| S1U  | 18432.3    | 幸存区1使用    |
| EC   | 1302528.0  | 年轻代大小     |
| EU   | 7168.0   | 年轻代使用     |
| OC   | 776192.0 | 老年代大小     |
| OU   | 81277.5  | 老年代使用     |
| MC   | 128664.0    | MetaSpace大小 |
| MU   | 112788.6    | MetaSpace使用 |
| CCSC | 19968.0    | 压缩类空间大小 |
| CCSU | 15611.1     | 压缩类空间使用 |
| YGC  | 19     | 年轻代GC次数   |
| YGCT | 0.957   | 年轻代GC总耗时 |
| FGC  | 0        | FullGC次数    |
| FGCT | 0     | FullGC耗时    |
| GCT  | 0.957   | GC总耗时      |

通过上表可以得到：
- 老年代约758M。年轻代约1272M Metaspace 约20M CompressedClassSpaceSize 约1G
- 老年代使用率 约10.5% 
- Metaspace使用率 87%
- 年轻代GC平均时间 约50ms
- 老年代GC平均时间,暂时未发生，未知

## 2.3 jstack

通过jstack命令分析：
```
Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.231-b11 mixed mode):

"Attach Listener" #148 daemon prio=9 os_prio=0 tid=0x00007fb42c13a000 nid=0x4713 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"elasticsearch[node201][fetch_shard_store][T#7]" #132 daemon prio=5 os_prio=0 tid=0x00007fb4308da800 nid=0x4493 waiting on condition [0x00007fb420441000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x0000000080ffd3b0> (a org.elasticsearch.common.util.concurrent.EsExecutors$ExecutorScalingQueue)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.LinkedTransferQueue.awaitMatch(LinkedTransferQueue.java:737)
	at java.util.concurrent.LinkedTransferQueue.xfer(LinkedTransferQueue.java:647)
	at java.util.concurrent.LinkedTransferQueue.take(LinkedTransferQueue.java:1269)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)

"elasticsearch[node201][search][T#4]" #107 daemon prio=5 os_prio=0 tid=0x00007fb45c21d800 nid=0x414f waiting on condition [0x00007fb41ff3b000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x0000000080ffdaa0> (a java.util.concurrent.LinkedTransferQueue)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.LinkedTransferQueue.awaitMatch(LinkedTransferQueue.java:737)
	at java.util.concurrent.LinkedTransferQueue.xfer(LinkedTransferQueue.java:647)
	at java.util.concurrent.LinkedTransferQueue.take(LinkedTransferQueue.java:1269)
	at org.elasticsearch.common.util.concurrent.SizeBlockingQueue.take(SizeBlockingQueue.java:154)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
```
| 线程状态       | 数量 |
|:--------------|:----|
| TIMED_WAITING       | 20  |
| WAITING     | 16 |
| RUNNABLE | 11  |
暂未发现死锁。

## 2.4 jmap
jmap 查看 heap
```
[elastic@m162p201 ~]$ jmap -heap 14321
Attaching to process ID 14321, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.231-b11

using thread-local object allocation.
Garbage-First (G1) GC with 2 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 2147483648 (2048.0MB)
   NewSize                  = 1363144 (1.2999954223632812MB)
   MaxNewSize               = 1287651328 (1228.0MB)
   OldSize                  = 5452592 (5.1999969482421875MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 1048576 (1.0MB)

Heap Usage:
G1 Heap:
   regions  = 2048
   capacity = 2147483648 (2048.0MB)
   used     = 362673688 (345.8725814819336MB)
   free     = 1784809960 (1702.1274185180664MB)
   16.88830964267254% used
G1 Young Generation:
Eden Space:
   regions  = 249
   capacity = 1333788672 (1272.0MB)
   used     = 261095424 (249.0MB)
   free     = 1072693248 (1023.0MB)
   19.57547169811321% used
Survivor Space:
   regions  = 18
   capacity = 18874368 (18.0MB)
   used     = 18874368 (18.0MB)
   free     = 0 (0.0MB)
   100.0% used
G1 Old Generation:
   regions  = 82
   capacity = 794820608 (758.0MB)
   used     = 82703896 (78.8725814819336MB)
   free     = 712116712 (679.1274185180664MB)
   10.405353757511028% used

43799 interned Strings occupying 5226392 bytes.
```
G1的堆参数与CMS的堆不太一样：
- 空闲堆空间的最大和最小百分比（MinHeapFreeRatio=40，MaxHeapFreeRatio=70）当低于百分之四十则会扩容，当高于百分之70则会缩容，这是默认值。
- 最大的堆大小2048M,其中，新生代最大为1228M,Metaspace大小20M,压缩类空间大小约1G。G1的Region大小1M。
- G1 Heap中共2048个region,使用率为23.9%
- G1的年轻代为403个Region,使用率31%
- 幸存区使用率为0
- 老年代目前为90个Region,使用率为11%


jmap  -histo:live
```
[elastic@m162p201 ~]$ jmap -histo:live 14321


 num     #instances         #bytes  class name
----------------------------------------------
   1:        205568       20776488  [C
   2:        571060       18273920  java.util.HashMap$Node
   3:         49649        5531376  [Ljava.util.HashMap$Node;
   4:          9267        4896992  [B
   5:        192867        4628808  java.lang.String
   6:         69192        3321216  java.util.HashMap
   7:         90287        2889184  java.util.concurrent.ConcurrentHashMap$Node
   8:         25175        2738504  java.lang.Class
   9:         60426        1933632  java.util.concurrent.atomic.LongAdder
  10:         16412        1909496  [I
  11:         28154        1841632  [Ljava.lang.Object;
  12:         44835        1434720  java.util.Collections$UnmodifiableMap
  13:         39557         949368  org.elasticsearch.common.util.concurrent.ReleasableLock
  14:         19743         947664  java.util.concurrent.locks.ReentrantReadWriteLock$NonfairSync
  15:         16215         908040  java.lang.invoke.MemberName
  16:         54687         874992  java.lang.Object
  17:           298         714936  [Ljava.util.concurrent.ConcurrentHashMap$Node;
  18:         19712         630784  org.elasticsearch.common.cache.Cache$CacheSegment
  19:         12326         591648  org.elasticsearch.painless.lookup.PainlessClass
  20:          6306         554928  java.lang.reflect.Method
  21:          2739         554720  [J
  22:          1730         478224  [Z
  23:         19735         473640  java.util.concurrent.locks.ReentrantReadWriteLock
  24:         19712         473088  org.elasticsearch.common.cache.Cache$CacheSegment$SegmentStats
  25:         11324         452960  java.lang.invoke.MethodType
  26:         15375         403368  [Ljava.lang.Class;
  27:         11344         363008  java.lang.invoke.MethodType$ConcurrentWeakInternSet$WeakEntry
  28:          8558         342320  java.util.LinkedHashMap$Entry
  29:          8508         340320  java.lang.ref.SoftReference
  30:         19746         315936  java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock
  31:         19746         315936  java.util.concurrent.locks.ReentrantReadWriteLock$Sync$ThreadLocalHoldCounter
  32:         19746         315936  java.util.concurrent.locks.ReentrantReadWriteLock$WriteLock
  33:          8465         270880  java.lang.invoke.DirectMethodHandle
  34:         11221         269304  java.util.ArrayList
  35:            44         237352  [S
  36:          5533         221320  java.util.TreeMap$Entry
  37:          5939         190048  org.antlr.v4.runtime.atn.ATNConfig
  38:          5453         174496  java.lang.invoke.BoundMethodHandle$Species_L
  39:          2068         165440  java.lang.reflect.Constructor
  40:          5583         165000  [Ljava.lang.String;
  41:          5891         141384  java.util.Collections$UnmodifiableRandomAccessList
```
