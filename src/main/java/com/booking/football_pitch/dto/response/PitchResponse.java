package com.booking.football_pitch.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PitchResponse {
    private Long id;
    private String name;
    private String pitchType;
    private String status;
}
