package com.booking.football_pitch.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SearchPitchRequest {
    private Long venueId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
