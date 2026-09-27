package com.booking.football_pitch.service;

import com.booking.football_pitch.dto.request.SearchPitchRequest;
import com.booking.football_pitch.dto.response.PitchResponse;
import java.util.List;

public interface PitchService {

	List<PitchResponse> findAvailablePitches(SearchPitchRequest request);
}
