package ru.otus.dataprocessor;

public class FileProcessException extends RuntimeException {
    public FileProcessException(String msg, Exception ex) {
        super(msg, ex);
    }
}
