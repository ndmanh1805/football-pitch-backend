package com.booking.football_pitch.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalTime;

@Data
@Builder
public class BookedSlotResponse {
    private LocalTime startTime;
    private LocalTime endTime;
}