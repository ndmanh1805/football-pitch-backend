package com.booking.football_pitch.controller;


import com.booking.football_pitch.dto.request.BookingRequest;
import com.booking.football_pitch.dto.response.BookingResponse;
import com.booking.football_pitch.service.BookingService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@CrossOrigin
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.ok(response);
    }

    // TÍNH NĂNG MỚI: API Lấy lịch sử đặt sân của 1 người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingHistory(@PathVariable Long userId) {
        List<BookingResponse> history = bookingService.getBookingHistory(userId);
        return ResponseEntity.ok(history);
    }
}
