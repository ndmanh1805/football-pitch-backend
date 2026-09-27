package com.booking.football_pitch.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SearchPitchRequest {
    private Long venueId;
    private LocalDate bookingDate; // Thêm ngày đặt sân riêng
    private LocalTime startTime;   // Chỉ lưu giờ
    private LocalTime endTime;     // Chỉ lưu giờ
}
