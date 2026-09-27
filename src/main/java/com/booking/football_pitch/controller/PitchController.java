package com.booking.football_pitch.controller;

import com.booking.football_pitch.dto.request.SearchPitchRequest;
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

	@PostMapping("/search")
	public ResponseEntity<List<PitchResponse>> searchAvailablePitches(@RequestBody SearchPitchRequest request) {
        List<PitchResponse> pitches = pitchService.findAvailablePitches(request);
        return ResponseEntity.ok(pitches);
	}
}
