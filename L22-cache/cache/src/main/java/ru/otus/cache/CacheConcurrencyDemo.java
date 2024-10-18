package ru.otus.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// -Xmx256m -Xms256m
class CacheConcurrencyDemo {
    private static final Logger logger = LoggerFactory.getLogger(CacheConcurrencyDemo.class);
    private static final Random rand = new Random();

    public static void main(String[] args) {
        testCacheConcurrency();
    }

    public static void testCacheConcurrency() {
        HwCache<Integer, Integer> cache = MyCache.create(10_000_000);
        cache.addListener((key, value, operation) -> {
            if (operation.equals("gc")) {
                logger.info("key:{} value:{} ops:{}", key, value, operation);
            }
        });

        ExecutorService threadPoolPut = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            threadPoolPut.submit(() -> {
                while (true) {
                    cache.put(rand.nextInt(0, 10_000_001), rand.nextInt(0, 10_000_001));
                }
            });
        }

        ExecutorService threadPoolGet = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            threadPoolGet.submit(() -> {
                while (true) {
                    cache.get(rand.nextInt(0, 10_000_001));
                }
            });
        }
    }
}
