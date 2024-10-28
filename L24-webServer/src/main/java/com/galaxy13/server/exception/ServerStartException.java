package com.galaxy13.server.exception;

public class ServerStartException extends RuntimeException {
    public ServerStartException(Exception e) {
        super("Jetty server start exception", e);
    }
}
