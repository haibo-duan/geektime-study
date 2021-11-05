package com.dhb.kmq.v2.core;


import java.util.concurrent.atomic.AtomicInteger;

public class KmqConsumer<T> {

    private final KmqBroker broker;

    private Kmq kmq;

    private AtomicInteger index = new AtomicInteger(0);
    
    public KmqConsumer(KmqBroker broker) {
        this.broker = broker;
    }

    public void subscribe(String topic) {
        this.kmq = this.broker.findKmq(topic);
        if (null == kmq) {
            throw new RuntimeException("Topic[" + topic + "] doesn't exist.");
        }
    }

    public KmqMessage<T> poll(long timeout) {
//        System.out.println("consumer index is :"+index.get());
        KmqMessage message =  kmq.poll(index.get(),timeout);
        if(null != message){
            index.getAndIncrement();
        }
        return message;
    }

}
