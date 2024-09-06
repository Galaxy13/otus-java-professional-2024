package com.galaxy13;

import com.galaxy13.unittest.TestWorker;

public class Main {
    public static void main(String[] args) throws Exception {
        boolean isFailed = true;
        for (String testClass : args) {
            isFailed = new TestWorker().executeTestWork(testClass);
        }
        if (isFailed) {
            System.exit(1);
        }
    }
}