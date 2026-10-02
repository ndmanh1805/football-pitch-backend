package com.booking.football_pitch.service;

import com.booking.football_pitch.dto.request.BookingRequest;
import com.booking.football_pitch.dto.response.BookingResponse;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
}
