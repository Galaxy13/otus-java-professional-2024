package com.galaxy13.service;

import com.galaxy13.domain.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DataStore {

    Mono<Message> saveMessage(Message message);

    Flux<Message> loadMessages(String roomId);
}
