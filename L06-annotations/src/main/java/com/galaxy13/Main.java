package com.galaxy13;

import com.galaxy13.galaxytest.TestWorker;
import com.galaxy13.galaxytest.exceptions.NotTestClass;


public class Main {
    public static void main(String[] args) throws Exception {
        boolean isFail = false;
        for (String testClass : args) {
            try {
                isFail = new TestWorker().executeTestWork(testClass);
            } catch (NotTestClass ignored) {
                // stops test class handling and continues test work
            }
        }
        if (isFail) {
            System.exit(1);
        }
    }
}