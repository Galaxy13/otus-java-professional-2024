package com.galaxy13.galaxytest;

import java.util.*;


public class TestStatistics {
    private final Class<?> testClass;

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

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("\r\nTest class " + testClass.getName() + " stats:");
        for (String test : tests) {
            joiner.add("\t" + test + "::" + testMap.get(test));
        }
        joiner.add("Number of tests: " + tests.size());
        joiner.add("Success: " + okCounter);
        joiner.add("Fail: " + failCounter);
        return joiner.toString();
    }

    public boolean isFailed() {
        return failCounter > 0;
    }
}
