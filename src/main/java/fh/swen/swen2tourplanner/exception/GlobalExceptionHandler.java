package fh.swen.swen2tourplanner.exception;

import fh.swen.swen2tourplanner.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, e.getMessage()));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ResourceAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, e.getMessage()));
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthFailed(AuthenticationFailedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(401, e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, e.getMessage()));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException e) {
        log.error("I/O error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "Internal server error"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "Internal server error"));
    }
}
