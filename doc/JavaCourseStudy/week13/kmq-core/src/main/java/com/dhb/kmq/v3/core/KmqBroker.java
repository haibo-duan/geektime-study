package com.dhb.kmq.v3.core;


import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public final class KmqBroker { // Broker+Connection

    public static final int CAPACITY = 10000;

    private final Map<String, Kmq> kmqMap = new ConcurrentHashMap<>(64);

    public Kmq createTopic(String name){
        Kmq kmq = new Kmq(name,CAPACITY);
        kmqMap.putIfAbsent(name,kmq);
        return kmq;
    }

    public Kmq findKmq(String topic) {
        return this.kmqMap.get(topic);
    }
    
}
