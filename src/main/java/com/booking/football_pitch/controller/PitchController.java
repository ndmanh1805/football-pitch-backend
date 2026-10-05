package com.booking.football_pitch.controller;

import com.booking.football_pitch.dto.response.PitchResponse;
import com.booking.football_pitch.service.PitchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pitches")
@CrossOrigin
@RequiredArgsConstructor
public class PitchController {

    private final PitchService pitchService;

    // Lấy danh sách sân bóng thuộc về 1 Cụm sân (Venue) cụ thể
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<PitchResponse>> getPitchesByVenue(@PathVariable Long venueId) {
        return ResponseEntity.ok(pitchService.getPitchesByVenue(venueId));
    }
}