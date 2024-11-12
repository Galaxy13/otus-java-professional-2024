package com.galaxy13.server;

public interface ClientWebServer {
    void start();

    void stop();

    void join() throws InterruptedException;
}
