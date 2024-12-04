package com.galaxy13.homework.service;

import com.galaxy13.homework.RemoteSequenceServiceGrpc;
import com.galaxy13.homework.SequenceRequestMessage;
import com.galaxy13.homework.SequenceValueResponse;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;


@SuppressWarnings({"squid:S2142"})
public class RemoteSequenceServiceImpl extends RemoteSequenceServiceGrpc.RemoteSequenceServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(RemoteSequenceServiceImpl.class);

    @Override
    public void generateSequence(SequenceRequestMessage message, StreamObserver<SequenceValueResponse> responseObserver) {
        int start = message.getStart();
        int finish = message.getFinish();
        IntStream.range(start, finish).forEach(value -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.error("Int stream generation interrupted", e);
            }
            var response = SequenceValueResponse.newBuilder().setValue(value).build();
            responseObserver.onNext(response);
        });
        responseObserver.onCompleted();
    }
}
