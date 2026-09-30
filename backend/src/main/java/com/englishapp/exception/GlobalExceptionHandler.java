package com.englishapp.exception;

import com.englishapp.common.Result;
import com.englishapp.common.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleInvalidArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new Result<>(400, ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleInvalidRequest(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(Result.error(ResultCode.BAD_REQUEST));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Result<Void>> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Result<>(ResultCode.NOT_FOUND.getCode(), ex.getMessage(), null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "message", ex.getMessage()
                ));
    }
}
