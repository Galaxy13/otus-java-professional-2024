package com.galaxy13.galaxytest;

import com.galaxy13.galaxytest.exceptions.AssertionFailedException;

import java.util.Arrays;

public class Assert {
    private Assert() {
        // default implementation prohibited
        throw new UnsupportedOperationException("Utility class");
    }

    public static void assertTrue() {
        // always assert successfully
    }

    public static void assertFalse() {
        throw new AssertionFailedException("true", "false");
    }

    public static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionFailedException(String.valueOf(expected), String.valueOf(actual));
        }
    }

    public static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionFailedException(expected, actual);
        }
    }

    public static void assertArraysEquals(int[] expected, int[] actual) {
        if (expected.length != actual.length) {
            throw new AssertionFailedException(Arrays.toString(expected), Arrays.toString(actual));
        }
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != actual[i]) {
                throw new AssertionFailedException(Arrays.toString(expected), Arrays.toString(actual));
            }
        }
    }
}
