shrink方法是DestroyTask线程中回收连接的具体执行方法。

首先获得锁：
```
try {
    lock.lockInterruptibly();
} catch (InterruptedException e) {
    return;
}
```
之后，要判断初始化状态是否完成，如果采用异步初始化，可能DestoryTask线程已经启动，但是连接池还没有初始化完成。
```
if (!inited) {
    return;
}
```
之后对连接池中的连接进行遍历，connections中，可连接的连接数记在poolingCount变量。
此时要记录一个checkCount，这个变量为  checkCount = poolingCount - minIdle;也就是checkCount为连接池中连接的数量减去最小空闲连接数设置minIdle。

此后进入checkTime逻辑，checkTime是调用shrink传入的参数，通常DestroyTask的调用这个参数都为true。
此后check的参数有：
- 判断物理连接是否超时：phyConnectTimeMillis > phyTimeoutMillis。如果超时，则将当前连接标记到evictConnections数组并退出当前循环。
- 判断空闲时间是否超时：
如果空闲时间小于最小于配置的minEvictableIdleTimeMillis时间且同时小于配置的keepAliveBetweenTimeMillis(idleMillis < minEvictableIdleTimeMillis && idleMillis < keepAliveBetweenTimeMillis) 则结束循环。
反之，当idleMillis大于minEvictableIdleTimeMillis或者大于maxEvictableIdleTimeMillis都被标记到evictConnections数组。
- 判断keepAlive是否超时：如果idleMillis >= keepAliveBetweenTimeMillis，则标记到keepAliveConnections数组。

如果checkTime为false,则将小于checkCount的全部连接都标记到evictConnections数组。
```
if (i < checkCount) {
    evictConnections[evictCount++] = connection;
} else {
    break;
}
```

这之后进行removeCount的处理，removeCount = evictCount + keepAliveCount;
处理逻辑如下:
```
if (removeCount > 0) {
    //将connections从removeCount到poolingCount的连接向前移动poolingCount - removeCount。
    System.arraycopy(connections, removeCount, connections, 0, poolingCount - removeCount);
    //将poolingCount - removeCount后续部分都置为空。
    Arrays.fill(connections, poolingCount - removeCount, poolingCount, null);
    poolingCount -= removeCount;
}
```
这个逻辑实质上是将connections中计算出来的前N项都移除。
之前一直不理解这个逻辑，实际上需要详细看一下for循环中的逻辑。for循环中，如果checkTime为false,则直接将前面checkCount个连接都移除。
反之，由于connections中，通过recycle方法，将放回的连接都放在connections数组的最后面。get的连接也是从connections的尾部获取，那么可以确保connections的连接，index小的连接最少被使用。
那么在这里确定了需要移除的连接数之后，直接就可以将connetions的前面checkCount个连接都移除。

移除之后，可以解锁。之后对移除的连接进行处理。
```
} finally {
    lock.unlock();
}
```
对于evict的连接：
```
if (evictCount > 0) {
    for (int i = 0; i < evictCount; ++i) {
        DruidConnectionHolder item = evictConnections[i];
        Connection connection = item.getConnection();
        //关闭连接
        JdbcUtils.close(connection);
        //更新计数器
        destroyCountUpdater.incrementAndGet(this);
    }
    //将evictConnections清空
    Arrays.fill(evictConnections, null);
}
```
关闭连接并清空evictConnections。

对于keepAliveCount连接，则需要分几种情况进行讨论：
```
if (keepAliveCount > 0) {
    // keep order
    for (int i = keepAliveCount - 1; i >= 0; --i) {
        DruidConnectionHolder holer = keepAliveConnections[i];
        Connection connection = holer.getConnection();
        holer.incrementKeepAliveCheckCount();

        boolean validate = false;
        //校验连接是否还可用
        try {
            this.validateConnection(connection);
            validate = true;
        } catch (Throwable error) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("keepAliveErr", error);
            }
            // skip
        }

        boolean discard = !validate;
        //如果可用，则直接put到connections中，放置到尾部。
        if (validate) {
            holer.lastKeepTimeMillis = System.currentTimeMillis();
            boolean putOk = put(holer, 0L, true);
            if (!putOk) {
                discard = true;
            }
        }
		
		//如果不可用，则关闭连接
        if (discard) {
            try {
                connection.close();
            } catch (Exception e) {
                // skip
            }

            lock.lock();
            //加锁更新计数器
            try {
                discardCount++;

                if (activeCount + poolingCount <= minIdle) {
                    emptySignal();
                }
            } finally {
                lock.unlock();
            }
        }
    }
    this.getDataSourceStat().addKeepAliveCheckCount(keepAliveCount);
    Arrays.fill(keepAliveConnections, null);
}
```
对于keepalive状态的连接，为了更好的复用该连接，则首先判断该连接是否可用，如果可用，则调用put方法，将该连接的状态更新之后，放置到连接池的尾部。
可见，shrink中，并非所有的连接都会关闭，对于keepalive状态的连接，需要判断是否可用。可用的连接还可再次复用。

此时还有一种情况需要考虑，就是此时可用的连接仍然不够minIdle，那么连接池不满，需要继续创建连接。这个状态为needFill:
```
//keepAlive状态，且连接池中的连接加上被使用的连接仍然小于minIdle
if (keepAlive && poolingCount + activeCount < minIdle) {
    needFill = true;
}
```
处理逻辑：
```
if (needFill) {
//加锁
    lock.lock();
    try {
        //如果minIdle 减去activeCount + poolingCount + createTaskCount 仍然不满，则通知创建线程创建连接
        int fillCount = minIdle - (activeCount + poolingCount + createTaskCount);
        for (int i = 0; i < fillCount; ++i) {
            emptySignal();
        }
        //解锁
    } finally {
        lock.unlock();
    }
} else if (onFatalError || fatalErrorIncrement > 0) {
    lock.lock();
    try {
        emptySignal();
    } finally {
        lock.unlock();
    }
}
```
needFill和onFatalError 都需要通知生产者继续创建连接。

至此，shrink方法分析完毕。