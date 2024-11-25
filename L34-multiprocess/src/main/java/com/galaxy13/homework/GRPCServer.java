package com.galaxy13.homework;

import com.galaxy13.homework.service.RemoteSequenceServiceImpl;
import io.grpc.ServerBuilder;

import java.io.IOException;

@SuppressWarnings({"squid:S106"})
public class GRPCServer {

    public static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws IOException, InterruptedException {

        var remoteSeqService = new RemoteSequenceServiceImpl();

        var server =
                ServerBuilder.forPort(SERVER_PORT).addService(remoteSeqService).build();
        server.start();
        System.out.println("server waiting for client connections...");
        server.awaitTermination();
    }
}
