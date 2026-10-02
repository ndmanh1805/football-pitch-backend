package com.booking.football_pitch.service;

import java.util.List;

import com.booking.football_pitch.dto.request.BookingRequest;
import com.booking.football_pitch.dto.response.BookingResponse;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
    // Tính năng mới: Lấy lịch sử đặt sân
    List<BookingResponse> getBookingHistory(Long userId);

    // Tính năng mới: Hủy đơn đặt sân
    BookingResponse cancelBooking(Long bookingId, com.booking.football_pitch.dto.request.CancelBookingRequest request);
}
