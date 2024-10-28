package com.galaxy13.server.exception;

public class ServerJoinException extends RuntimeException {
    public ServerJoinException(Exception e) {
        super("Jetty server join exception", e);
    }
}
