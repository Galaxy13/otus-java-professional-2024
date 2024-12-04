package com.galaxy13.service;

public class MessageForbiddenException extends RuntimeException {
    public MessageForbiddenException(int specialRoomId) {
        super("Message sending forbidden for room " + specialRoomId);
    }
}
