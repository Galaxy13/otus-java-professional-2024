package com.galaxy13.galaxytest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestStatistics {
    private final Class<?> testClass;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<String> tests = new ArrayList<>();
    private final Map<String, String> testMap = new HashMap<>();
    private int okCounter = 0;
    private int failCounter = 0;

    public TestStatistics(Class<?> testClass) {
        this.testClass = testClass;
    }

    public void addOkTest(String test) {
        tests.add(test);
        okCounter++;
        testMap.put(test, "SUCCESS");
    }

    public void addFailTest(String test, Exception e) {
        tests.add(test);
        failCounter++;
        testMap.put(test, String.format("FAIL -> %s", e.getMessage()));
    }

    public void out() {
        logger.info("\r\nTest class {} stats:", testClass.getName());
        for (String test : tests) {
            if (logger.isInfoEnabled()) {
                String testResult = testMap.get(test);
                if (testResult.equals("SUCCESS")) {
                    logger.info("\t{}::{}", test, testResult);
                } else {
                    logger.warn("\t{}::{}", test, testResult);
                }
            }
        }
        logger.info("Number of tests: {}", tests.size());
        logger.info("Success: {}", okCounter);
        logger.info("Fail: {}", failCounter);
    }

    public boolean isFailed() {
        return failCounter > 0;
    }
}
