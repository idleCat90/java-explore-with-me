package ru.practicum.ewm.exception;

public class DateNotValidException extends RuntimeException {
    public DateNotValidException(String message) {
        super(message);
    }
}
