package com.galaxy13;

import com.galaxy13.galaxytest.TestWorker;


public class Main {
    public static void main(String[] args) throws Exception {
        boolean isFail = false;
        for (String testClass : args) {
            isFail = new TestWorker().executeTestWork(testClass) || isFail;
        }
        if (isFail) {
            System.exit(1);
        }
    }
}