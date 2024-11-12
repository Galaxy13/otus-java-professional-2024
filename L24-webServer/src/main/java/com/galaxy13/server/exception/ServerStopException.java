package com.galaxy13.server.exception;

public class ServerStopException extends RuntimeException {
    public ServerStopException(Exception e) {
        super("Server stop exception", e);
    }
}
