package com.galaxy13.unittest.exceptions;

public class AssertionException extends Exception {
    public AssertionException(String expected, String actual) {
        super(String.format("%n \t Expected -> %s%n \t Actual -> %s", expected, actual));
    }
}
