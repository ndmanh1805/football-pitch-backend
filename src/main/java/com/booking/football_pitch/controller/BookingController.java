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

    // TÍNH NĂNG MỚI: API Hủy đặt sân
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId,
            @RequestBody com.booking.football_pitch.dto.request.CancelBookingRequest request) {
        
        BookingResponse response = bookingService.cancelBooking(bookingId, request);
        return ResponseEntity.ok(response);
    }
    // TÍNH NĂNG MỚI: Tra cứu các khung giờ đã bị đặt
    @GetMapping("/pitch/{pitchId}/booked-slots")
    public ResponseEntity<List<com.booking.football_pitch.dto.response.BookedSlotResponse>> getBookedSlots(
            @PathVariable Long pitchId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date) {
        
        List<com.booking.football_pitch.dto.response.BookedSlotResponse> response = bookingService.getBookedSlots(pitchId, date);
        return ResponseEntity.ok(response);
    }
}
