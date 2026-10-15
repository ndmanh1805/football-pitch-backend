package com.booking.football_pitch.service;

import com.booking.football_pitch.dto.request.SearchPitchRequest;
import com.booking.football_pitch.dto.response.PitchResponse;
import java.util.List;

public interface PitchService {

	List<PitchResponse> findAvailablePitches(SearchPitchRequest request);

	java.util.List<com.booking.football_pitch.dto.response.PitchResponse> getPitchesByVenue(Long venueId);

	PitchResponse getPitchById(Long id);
	// Admin: Lấy toàn bộ danh sách sân bóng
    List<PitchResponse> getAllPitches();

	// Admin: Thêm sân bóng mới
    com.booking.football_pitch.dto.response.PitchResponse createPitch(com.booking.football_pitch.dto.request.PitchRequest request);

    // Admin: Cập nhật thông tin sân bóng
    com.booking.football_pitch.dto.response.PitchResponse updatePitch(Long id, com.booking.football_pitch.dto.request.PitchRequest request);

    // Admin: Xóa sân bóng
    void deletePitch(Long id);
}
