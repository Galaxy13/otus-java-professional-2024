package com.galaxy13.optimal;

import java.io.IOException;

public class Main {
    private static final String[] GARBAGE_COLLECTORS = {"G1GC", "ParallelGC",};
    private static final int MAX_MEMORY = 2048;
    private static final long THRESHOLD = 300;


    public static void main(String[] args) throws IOException, InterruptedException {
        int currentMemory = MAX_MEMORY / 2;
        String optimalGC = findFastestGC();
        long currentTime = measureVM(currentMemory, optimalGC);
        int left = currentMemory, right = currentMemory;
        while (left > 0 && right < MAX_MEMORY) {
            left = currentMemory - (currentMemory / 2);
            right = currentMemory + ((MAX_MEMORY - currentMemory) / 2);
            long lessMemoryTime = measureVM(left, optimalGC);
            long moreMemoryTime = measureVM(right, optimalGC);
            if (lessMemoryTime < moreMemoryTime - THRESHOLD && lessMemoryTime < currentTime - THRESHOLD) {
                currentTime = lessMemoryTime;
                currentMemory = left;
            } else if (lessMemoryTime > moreMemoryTime - THRESHOLD && moreMemoryTime < currentTime - THRESHOLD) {
                currentTime = moreMemoryTime;
                currentMemory = right;
            } else {
                break;
            }
        }

    }

    private static ProcessBuilder commandBuilder(int memory, String garbageCollector) {
        String initMemory = "-Xms" + memory + "m";
        String maxMemory = "-Xmx" + memory + "m";
        String gc = "-XX:+Use" + garbageCollector;
        return new ProcessBuilder("java",
                initMemory,
                maxMemory,
                gc);
    }

    private static long measureVM(int memory, String garbageCollector) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = commandBuilder(memory, garbageCollector);
        long startTime = System.currentTimeMillis();
        Process p = processBuilder.start();
        if (p.waitFor() != 0) {
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