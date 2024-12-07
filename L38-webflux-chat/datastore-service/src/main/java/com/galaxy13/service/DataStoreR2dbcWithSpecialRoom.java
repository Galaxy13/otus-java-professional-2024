package com.galaxy13.service;

import com.galaxy13.domain.Message;
import com.galaxy13.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class DataStoreR2dbcWithSpecialRoom implements DataStore {

    private final DataStore wrappedDataStore;
    private final MessageRepository messageRepository;
    private final Scheduler workerPool;

    public DataStoreR2dbcWithSpecialRoom(@Qualifier("dataStoreR2dbc") DataStore dataStore,
                                         Scheduler workerPool,
                                         MessageRepository messageRepository) {
        this.wrappedDataStore = dataStore;
        this.messageRepository = messageRepository;
        this.workerPool = workerPool;
    }

    @Override
    public Mono<Message> saveMessage(Message message) {
        return wrappedDataStore.saveMessage(message);
    }

    @Override
    public Flux<Message> loadMessages(String roomId) {
        if (Integer.parseInt(roomId) == 1408) {
            return messageRepository.findAll().delayElements(Duration.of(1, SECONDS), workerPool);
        }
        return wrappedDataStore.loadMessages(roomId);
    }
}
