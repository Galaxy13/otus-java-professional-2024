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
        var oldObjectFromMessage = message.getField13();
        var newObjectForMessage = new ObjectForMessage();
        if (oldObjectFromMessage != null) {
            if (oldObjectFromMessage.getData() != null) {
                newObjectForMessage.setData(new ArrayList<>(oldObjectFromMessage.getData()));
                history.put(message.getId(),
                        message.toBuilder().field13(newObjectForMessage).build());
            }
        } else {
            history.put(message.getId(), message);
        }
    }
}
