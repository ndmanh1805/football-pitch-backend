package com.booking.football_pitch.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
@Data
public class BookingRequest {
    private Long pitchId; // ID của sân muốn đặt
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
}
