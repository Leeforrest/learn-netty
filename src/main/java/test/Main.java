package test;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.ReentrantLock;

/**
 * author: Forrest
 * date: 2017/7/29
 */
public class Main {
    public static void main(String[] args) {
        AtomicLong atomicLong = new AtomicLong();
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        lock.unlock();
//        AtomicReferenceFieldUpdater update = AtomicReferenceFieldUpdater.newUpdater();
    }
}
