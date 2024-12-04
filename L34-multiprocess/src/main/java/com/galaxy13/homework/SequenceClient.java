package com.galaxy13.homework;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class SequenceClient {
    private static final Logger logger = LoggerFactory.getLogger(SequenceClient.class);

    private final RemoteSequenceServiceGrpc.RemoteSequenceServiceStub stub;
    private final AtomicInteger counter = new AtomicInteger(0);

    public SequenceClient(ManagedChannel channel) {
        stub = RemoteSequenceServiceGrpc.newStub(channel);
    }

    public void start() {
        logger.info("Starting sequence client...");

        var sequenceRequest = SequenceRequestMessage
                .newBuilder()
                .setStart(0)
                .setFinish(30)
                .build();

        startSequenceAccept(sequenceRequest);
        loop();
        logger.info("Sequence generation done");
    }

    private void loop() {
        try {
            while (counter.get() < 50) {
                Thread.sleep(1000);
                logger.info("Current value: {}", counter.get() + 1);
                counter.incrementAndGet();
            }
        } catch (InterruptedException e) {
            logger.error("Client loop Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    private void startSequenceAccept(SequenceRequestMessage sequenceRequest) {
        stub.generateSequence(sequenceRequest, new StreamObserver<>() {

            @Override
            public void onNext(SequenceValueResponse sequenceValueResponse) {
                int receivedValue = sequenceValueResponse.getValue();
                logger.info("New value: {}", receivedValue);
                counter.addAndGet(receivedValue);
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("Error while accepting stream", throwable);
            }

            @Override
            public void onCompleted() {
                logger.info("Sequence receiving completed");
            }
        });
    }
}
