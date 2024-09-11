package com.galaxy13.galaxytest.exceptions.annotations;

import java.lang.annotation.Annotation;

public class AnnotaionsOverrideException extends RuntimeException {
    public AnnotaionsOverrideException(Class<? extends Annotation> annotation) {
        super("Annotation " + annotation.getName() + " has multiple occurrences. This behaviour is prohibited");
    }
}
