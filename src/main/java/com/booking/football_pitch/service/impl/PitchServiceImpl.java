package com.booking.football_pitch.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.booking.football_pitch.entity.Pitch;
import com.booking.football_pitch.repository.PitchRepository;
import com.booking.football_pitch.service.PitchService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PitchServiceImpl implements PitchService {

	private final PitchRepository pitchRepository;

	@Override
	public List<Pitch> getActivePitchesByVenue(Long venueId) {
		return pitchRepository.findByVenueIdAndStatus(venueId, Pitch.Status.ACTIVE);
	}
}
