package com.booking.football_pitch.service.impl;

import com.booking.football_pitch.dto.request.SearchPitchRequest;
import com.booking.football_pitch.dto.response.PitchResponse;
import com.booking.football_pitch.entity.Pitch;
import com.booking.football_pitch.repository.PitchRepository;
import com.booking.football_pitch.service.PitchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PitchServiceImpl implements PitchService {

	private final PitchRepository pitchRepository;

	@Override
	public List<PitchResponse> findAvailablePitches(SearchPitchRequest request) {
        // Gọi câu lệnh SQL tối ưu từ Repository
        List<Pitch> availablePitches = pitchRepository.findAvailablePitches(
			request.getVenueId(),
			request.getBookingDate(),
			request.getStartTime(),
			request.getEndTime()
	);
        // Biến đổi Entity sang DTO và lấy đúng giá tiền
        return availablePitches.stream().map(pitch -> PitchResponse.builder()
                .id(pitch.getId())
                .name(pitch.getName())
                .pitchType(pitch.getPitchType().name())
                .hourlyPrice(pitch.getHourlyPrice()) // Lấy đúng tên hàm getHourlyPrice()
                .status(pitch.getStatus().name())
                .build()).collect(java.util.stream.Collectors.toList());
	}
}
