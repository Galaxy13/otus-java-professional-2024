package com.galaxy13.asm;

import com.galaxy13.autologger.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoLogger {
    private final Logger logger = LoggerFactory.getLogger(AutoLogger.class);

    @Log
    public int sum(int a, int b) {
        return a + b;
    }

    @Log
    public void sum(int a, int b, String comp) {
        int c = a + b;
        logger.info("{}: {}", comp, c);
    }
}
