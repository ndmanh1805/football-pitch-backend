package com.booking.football_pitch.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Bắt toàn bộ các lỗi RuntimeException (bao gồm cả lỗi tìm user, tìm sân, lỗi đè lịch...)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        
        // Lấy câu thông báo lỗi tiếng Việt mà chúng ta đã viết ở Service
        errorResponse.put("error", ex.getMessage());
        
        // Trả về mã 400 Bad Request thay vì 500
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
