package com.example.baitapsession09.exception;

import com.example.baitapsession09.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("FAIL", exception.getMessage(), null));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(DuplicateResourceException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>("FAIL", exception.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>("FAIL", "Dữ liệu không hợp lệ", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidJson(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>("FAIL", "Dữ liệu JSON không hợp lệ", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOtherErrors(Exception exception) {
        if (exception instanceof org.springframework.web.ErrorResponse error) {
            return ResponseEntity.status(error.getStatusCode()).headers(error.getHeaders())
                    .body(new ApiResponse<>("FAIL", "Yêu cầu không hợp lệ", null));
        }
        return ResponseEntity.internalServerError()
                .body(new ApiResponse<>("FAIL", "Đã xảy ra lỗi hệ thống", null));
    }
}
