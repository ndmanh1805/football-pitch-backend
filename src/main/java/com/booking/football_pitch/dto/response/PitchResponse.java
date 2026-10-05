package com.booking.football_pitch.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PitchResponse {
    private Long id;
    private String name;
    private String pitchType;
    private String surface;
    private BigDecimal hourlyPrice; // Sửa thành BigDecimal và đúng tên
    private String status;
}
