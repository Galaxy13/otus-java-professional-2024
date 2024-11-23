package ru.otus.api.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SensorData {
    private final LocalDateTime measurementTime;
    private final String room;
    private final Double value;

    public SensorData(LocalDateTime measurementTime, String room, Double value) {
        this.measurementTime = measurementTime;
        this.room = room;
        this.value = value;
    }

    @Override
    public String toString() {
        return "SensorData{" + "measurementTime="
                + measurementTime + ", room='"
                + room + '\'' + ", value="
                + value + '}';
    }
}
