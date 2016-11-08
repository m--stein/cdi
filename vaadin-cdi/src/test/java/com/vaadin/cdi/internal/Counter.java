package com.vaadin.cdi.internal;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kumm on 2016.11.07..
 */
@ApplicationScoped
public class Counter {
    ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<String, AtomicInteger>();

    public void increment(String key) {
        map.putIfAbsent(key, new AtomicInteger(0));
        map.get(key).incrementAndGet();
    }

    public void decrement(String key) {
        map.putIfAbsent(key, new AtomicInteger(0));
        map.get(key).decrementAndGet();
    }

    public int get(String key) {
        map.putIfAbsent(key, new AtomicInteger(0));
        return map.get(key).get();
    }

    public void reset() {
        map.clear();
    }
}
