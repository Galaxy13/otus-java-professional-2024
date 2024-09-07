package com.galaxy13.galaxytest.exceptions;

@SuppressWarnings("java:S2166")
public class AssertionFailedException extends AssertionError {
    public AssertionFailedException(String expected, String actual) {
        super(String.format("%n \t Expected -> %s%n \t Actual -> %s", expected, actual));
    }
}
