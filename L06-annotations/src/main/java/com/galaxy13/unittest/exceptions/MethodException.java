package com.galaxy13.unittest.exceptions;

import java.lang.reflect.Method;

public class MethodException extends Exception{
    public MethodException(Method method) {
        super("Test method" + method.getName() + "must have no parameters");
    }
}
