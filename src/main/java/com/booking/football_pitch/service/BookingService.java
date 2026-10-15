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

    // Tính năng mới: Lấy các khung giờ đã được đặt của 1 sân trong 1 ngày
    java.util.List<com.booking.football_pitch.dto.response.BookedSlotResponse> getBookedSlots(Long pitchId, java.time.LocalDate date);

    // Lấy toàn bộ danh sách đặt sân (Dành cho Admin)
    List<BookingResponse> getAllBookings();
    // Admin cập nhật trạng thái đơn hàng (Duyệt/Hủy)
    BookingResponse updateBookingStatus(Long bookingId, String status);
}
