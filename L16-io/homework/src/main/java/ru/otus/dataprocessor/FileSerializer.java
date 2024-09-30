package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class FileSerializer implements Serializer {
    private final ObjectWriter writer = new ObjectMapper().writer();
    private final String fileName;

    public FileSerializer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) {
        // формирует результирующий json и сохраняет его в файл
        try (var outStream = new BufferedOutputStream(new FileOutputStream(fileName))) {
            writer.writeValue(outStream, data);
        } catch (IOException e) {
            throw new FileProcessException("Error while creating json file: " + fileName, e);
        }
    }
}
