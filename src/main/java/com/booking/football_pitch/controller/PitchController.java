package com.booking.football_pitch.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.football_pitch.entity.Pitch;
import com.booking.football_pitch.service.PitchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pitches")
@CrossOrigin
@RequiredArgsConstructor
public class PitchController {

	private final PitchService pitchService;

	@GetMapping("/venue/{venueId}")
	public ResponseEntity<List<Pitch>> getActivePitchesByVenue(@PathVariable Long venueId) {
		return ResponseEntity.ok(pitchService.getActivePitchesByVenue(venueId));
	}
}
