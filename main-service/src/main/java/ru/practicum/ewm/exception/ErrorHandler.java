package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException e) {
        log.warn("404 Not Found, {}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .message(e.getMessage())
                .reason("Object not found")
                .build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,
            IncorrectParameterException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectParameters(Exception e) {
        log.warn("400 Bad Request, {}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .message(e.getMessage())
                .reason("Incorrect parameter")
                .build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class, ConflictException.class, PSQLException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(Exception e) {
        log.warn("409 Conflict, {}", e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.toString())
                .message(e.getMessage())
                .reason("Conflicting request")
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleOtherException(Throwable e) {
        log.error("500 Internal Server Error, {}, {}", e.getMessage(), e.getStackTrace(), e);
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .message(e.getMessage())
                .reason("Internal server error")
                .build();
    }
}
