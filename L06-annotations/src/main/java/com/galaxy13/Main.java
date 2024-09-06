package com.galaxy13;

import com.galaxy13.unittest.TestWorker;

public class Main {
    public static void main(String[] args) throws Exception {
        for (String testClass : args) {
            new TestWorker().executeTestWork(testClass);
        }
    }
}