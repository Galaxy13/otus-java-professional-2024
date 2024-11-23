package com.galaxy13;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntFunction;

public class BinaryThreadPrinter {
    private static final Logger logger = LoggerFactory.getLogger(BinaryThreadPrinter.class);

    private final Thread thread1;
    private final Thread thread2;
    private final Lock lock;
    private final Condition condition;
    private String lastThread = "t2";

    public BinaryThreadPrinter() {
        thread1 = new Thread(this::task, "t1");
        thread2 = new Thread(this::task, "t2");
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void start() {
        thread1.start();
        thread2.start();
    }

    private void task() {
        lock.lock();
        try {
            countApplyLoop(
                    count -> count < 10,
                    1,
                    count -> ++count);
            countApplyLoop(
                    count -> count > 0,
                    10,
                    count -> --count);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("java:S5411")
    private void countApplyLoop(IntFunction<Boolean> loopCondition,
                                int count,
                                IntFunction<Integer> action) throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        while (loopCondition.apply(count)) {
            while (threadName.equals(lastThread)) {
                condition.await();
            }
            logger.info("{}", count);
            count = action.apply(count);
            sleep(1000);
            lastThread = threadName;
            condition.signalAll();
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
