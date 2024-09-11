package com.galaxy13.optimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Main {
    private static final String[] GARBAGE_COLLECTORS = {"G1GC", "ParallelGC",};
    private static final int MAX_MEMORY = 2048;
    private static final long THRESHOLD = 300;
    private static final int MIN_STEP = 32;

    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws IOException, InterruptedException {
        int currentMemory = MAX_MEMORY, bestMemory = MAX_MEMORY;
        int lastAfterMemoryDecrease = MAX_MEMORY;
        String optimalGC = findFastestGC();
        long currentTime = measureVM(currentMemory, optimalGC);
        long bestTime = currentTime;
        while (true) {
            if (currentTime < bestTime - THRESHOLD) {
                lastAfterMemoryDecrease = currentMemory;
                if (currentMemory <= MIN_STEP * 2) {
                    break;
                }
                currentMemory /= 2;
                bestMemory = currentMemory;
                bestTime = currentTime;
                currentTime = measureVM(currentMemory, optimalGC);
            } else {
                int diff = (lastAfterMemoryDecrease - currentMemory) / 2;
                if (diff < MIN_STEP) {
                    break;
                }
                currentMemory += diff;
                currentTime = measureVM(currentMemory, optimalGC);
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Optimal heap size {}", bestMemory);
            logger.info("Best time {}", bestTime);
        }
    }

    private static ProcessBuilder commandBuilder(int memory, String garbageCollector) {
        String initMemory = "-Xms" + memory + "m";
        String maxMemory = "-Xmx" + memory + "m";
        String gc = "-XX:+Use" + garbageCollector;
        return new ProcessBuilder("java",
                "-cp",
                "otus-java-2024-pro.L08-gc.homework.main",
                "ru."
                "-Dorg.gradle.jvmargs=",
                initMemory,
                maxMemory,
                gc);
    }

    private static long measureVM(int memory, String garbageCollector) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = commandBuilder(memory, garbageCollector);
        long startTime = System.currentTimeMillis();
        Process p = processBuilder.start();
        InputStream stdout = p.getErrorStream();
        if (p.waitFor() != 0) {
            logger.warn(new String(stdout.readAllBytes(), StandardCharsets.UTF_8));
            throw new InterruptedException("Exit code " + p.exitValue());
        }
        return System.currentTimeMillis() - startTime;
    }

    private static String findFastestGC() throws IOException, InterruptedException {
        int fastestGC = 0;
        long bestTime = Long.MAX_VALUE;
        for (int i = 0; i < GARBAGE_COLLECTORS.length; i++) {
            long delta = measureVM(MAX_MEMORY, GARBAGE_COLLECTORS[i]);
            if (delta < bestTime) {
                bestTime = delta;
                fastestGC = i;
            }
        }
        return GARBAGE_COLLECTORS[fastestGC];
    }
}