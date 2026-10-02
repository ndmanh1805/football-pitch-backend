package com.booking.football_pitch.dto.request;


import lombok.Data;

@Data
public class CancelBookingRequest {
    private Long userId;     // Bắt buộc: Để xác thực đúng người đang hủy
    private String reason;   // Tùy chọn: Lý do hủy (VD: "Trời mưa to", "Bận đột xuất")
}
