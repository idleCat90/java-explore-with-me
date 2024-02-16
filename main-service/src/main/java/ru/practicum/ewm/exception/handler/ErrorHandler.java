package ru.practicum.ewm.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.IncorrectParameterException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.utility.Util;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Util.DATE_TIME_FORMAT);

    @ExceptionHandler({MissingRequestHeaderException.class,
            MethodArgumentNotValidException.class,
            IncorrectParameterException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class})
    public ResponseEntity<ApiError> handleBadRequest(final Exception e) {
        log.error("Status: {}, Description: {}, Timestamp: {}",
                HttpStatus.BAD_REQUEST, e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(new ApiError(HttpStatus.BAD_REQUEST.name(), e.getMessage(),
                "Bad Request", LocalDateTime.now().format(DATE_TIME_FORMATTER)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleOther(final Throwable e) {
        log.error("Status: {}, Description: {}, Timestamp: {}, Stacktrace: {}",
                HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), LocalDateTime.now(), e.getStackTrace());

        return new ResponseEntity<>(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage(),
                "Internal Server Error", LocalDateTime.now().format(DATE_TIME_FORMATTER)),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler({ConflictException.class,
            DataIntegrityViolationException.class})
    public ResponseEntity<ApiError> handleConflict(final Exception e) {
        log.error("Status: {}, Description: {}, Timestamp: {}",
                HttpStatus.CONFLICT, e.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(new ApiError(HttpStatus.CONFLICT.name(), e.getMessage(),
                "Data Integrity constraint violation", LocalDateTime.now().format(DATE_TIME_FORMATTER)),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(final Exception e) {
        log.error("Status: {}, Description: {}, Timestamp: {}",
                HttpStatus.NOT_FOUND, e.getMessage(), LocalDateTime.now());

        return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND.name(), e.getMessage(),
                "Not Found", LocalDateTime.now().format(DATE_TIME_FORMATTER)), HttpStatus.NOT_FOUND);
    }
}
