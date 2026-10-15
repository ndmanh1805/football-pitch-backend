package com.booking.football_pitch.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PitchRequest {
    private Long venueId;          // ID cụm sân (nếu không truyền sẽ tự lấy cụm sân đầu tiên)
    private String name;           // Tên sân (VD: Sân Mỹ Đình 5)
    private String pitchType;      // Loại sân: FIVE, SEVEN, ELEVEN
    private String surface;        // Mặt cỏ (VD: Cỏ nhân tạo)
    private BigDecimal hourlyPrice;// Giá thuê / giờ
    private String status;         // Trạng thái: ACTIVE, MAINTENANCE, INACTIVE
}