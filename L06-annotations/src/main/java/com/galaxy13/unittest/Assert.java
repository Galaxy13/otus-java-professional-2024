package com.galaxy13.unittest;

import com.galaxy13.unittest.exceptions.AssertionException;

import java.util.Arrays;

public class Assert {
    private Assert() {
        // default implementation prohibited
        throw new UnsupportedOperationException("Utility class");
    }

    public static void assertTrue() {
        // everytime assert successfully
    }

    public static void assertFalse() throws AssertionException {
        throw new AssertionException("true", "false");
    }

    public static void assertEquals(int expected, int actual) throws AssertionException {
        if (expected != actual) {
            throw new AssertionException(String.valueOf(expected), String.valueOf(actual));
        }
    }

    public static void assertEquals(String expected, String actual) throws AssertionException {
        if (!expected.equals(actual)) {
            throw new AssertionException(expected, actual);
        }
    }

    public static void assertArraysEquals(int[] expected, int[] actual) throws AssertionException {
        if (expected.length != actual.length) {
            throw new AssertionException(Arrays.toString(expected), Arrays.toString(actual));
        }
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != actual[i]) {
                throw new AssertionException(Arrays.toString(expected), Arrays.toString(actual));
            }
        }
    }
}
