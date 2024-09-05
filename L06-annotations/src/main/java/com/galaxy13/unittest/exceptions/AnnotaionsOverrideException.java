package com.galaxy13.unittest.exceptions;

import java.lang.annotation.Annotation;

public class AnnotaionsOverrideException extends Exception {
    public AnnotaionsOverrideException(Class<? extends Annotation> annotation) {
        super("Annotation " + annotation.getName() + " has multiple occurrences. This behaviour is prohibited");
    }
}
