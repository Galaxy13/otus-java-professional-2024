package com.galaxy13;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntFunction;

public class BinaryThreadPrinter {
    private static final Logger logger = LoggerFactory.getLogger(BinaryThreadPrinter.class);

    private final ExecutorService threadPool;
    private final Lock lock;
    private final Condition condition;
    private int last = 2;

    public BinaryThreadPrinter(int nThreads) {
        threadPool = Executors.newFixedThreadPool(nThreads);
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void start() {
        try (threadPool) {
            threadPool.submit(() -> task(1));
            threadPool.submit(() -> task(2));
        } finally {
            threadPool.shutdown();
        }
    }

    private void task(int threadOrder) {
        lock.lock();
        try {
            countApplyLoop(
                    count -> count < 10,
                    1,
                    count -> ++count,
                    threadOrder);
            countApplyLoop(
                    count -> count > 0,
                    10,
                    count -> --count,
                    threadOrder
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("java:S5411")
    private void countApplyLoop(IntFunction<Boolean> loopCondition,
                                int count,
                                IntFunction<Integer> action,
                                int threadOrder) throws InterruptedException {
        while (loopCondition.apply(count)) {
            while (last == threadOrder) {
                condition.await();
            }
            logger.info("{}", count);
            count = action.apply(count);
            sleep(1000);
            last = threadOrder;
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
