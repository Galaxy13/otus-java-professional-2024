package com.galaxy13.unittest.annotaions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Unique
@Retention(RetentionPolicy.RUNTIME)
public @interface Before {
}
