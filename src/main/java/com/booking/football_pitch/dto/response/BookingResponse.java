package com.booking.football_pitch.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class BookingResponse {
    private Long id; // Mã đơn đặt sân
    private String customerName; // Tên khách
    private String pitchName; // Tên sân
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double totalPrice; // Tổng tiền tính toán được
    private String status; // Trạng thái đơn (PENDING)
}
