package com.booking.football_pitch.dto.request;


import lombok.Data;

@Data
public class CancelBookingRequest {
    private String reason;   // Tùy chọn: Lý do hủy (VD: "Trời mưa to", "Bận đột xuất")
}
