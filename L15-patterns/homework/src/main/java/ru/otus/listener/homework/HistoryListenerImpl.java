package ru.otus.listener.homework;

import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HistoryListenerImpl extends HistoryListener {
    private final Map<Long, Message> history = new HashMap<>();

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.of(history.get(id));
    }

    @Override
    public void onUpdated(Message message) {
        var msgCloneBuilder = message.toBuilder();
        if (message.getField13() != null) {
            var newObjectForMessage = new ObjectForMessage();
            if (message.getField13().getData() != null) {
                newObjectForMessage.setData(new ArrayList<>(message.getField13().getData()));
            }
            msgCloneBuilder.field13(newObjectForMessage);
        }
        history.put(message.getId(), msgCloneBuilder.build());
    }
}
