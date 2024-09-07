package com.galaxy13.galaxytest.exceptions.annotations;


import java.lang.annotation.Annotation;

public class MultipleAnnotationsException extends RuntimeException {
    public MultipleAnnotationsException(Class<? extends Annotation> annotation) {
        super("Using annotation " + annotation + " with other annotations is prohibited");
    }
}
