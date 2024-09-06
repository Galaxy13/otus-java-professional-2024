package com.galaxy13.unittest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("java:S106")
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

    public void out() throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        bw.write(String.format("%nTest class %s stats:%n", testClass.getName()));
        for (String test : tests) {
            bw.write(String.format("\t- %s::%s%n", test, testMap.get(test)));
        }
        bw.write(String.format("Number of tests: %s%n", tests.size()));
        bw.write(String.format("Success: %s%n", okCounter));
        bw.write(String.format("Fail: %s%n", failCounter));
        bw.flush();
    }
}
