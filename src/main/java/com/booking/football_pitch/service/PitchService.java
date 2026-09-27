package com.booking.football_pitch.service;

import java.util.List;

import com.booking.football_pitch.entity.Pitch;

public interface PitchService {

	List<Pitch> getActivePitchesByVenue(Long venueId);
}
