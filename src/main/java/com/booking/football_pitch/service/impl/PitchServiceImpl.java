package com.booking.football_pitch.service.impl;

import com.booking.football_pitch.dto.request.PitchRequest;
import com.booking.football_pitch.dto.request.SearchPitchRequest;
import com.booking.football_pitch.dto.response.PitchResponse;
import com.booking.football_pitch.entity.Pitch;
import com.booking.football_pitch.entity.Venue;
import com.booking.football_pitch.repository.PitchRepository;
import com.booking.football_pitch.repository.VenueRepository;
import com.booking.football_pitch.service.PitchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PitchServiceImpl implements PitchService {

    private final PitchRepository pitchRepository;
    private final VenueRepository venueRepository; // Tiêm thêm VenueRepository để lấy Cụm sân

    @Override
    public List<PitchResponse> findAvailablePitches(SearchPitchRequest request) {
        List<Pitch> availablePitches = pitchRepository.findAvailablePitches(
                request.getVenueId(),
                request.getBookingDate(),
                request.getStartTime(),
                request.getEndTime()
        );
        return availablePitches.stream().map(pitch -> PitchResponse.builder()
                .id(pitch.getId())
                .name(pitch.getName())
                .pitchType(pitch.getPitchType().name())
                .hourlyPrice(pitch.getHourlyPrice())
                .status(pitch.getStatus().name())
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<PitchResponse> getPitchesByVenue(Long venueId) {
        List<Pitch> pitches = pitchRepository.findByVenueId(venueId);

        return pitches.stream().map(pitch -> PitchResponse.builder()
                .id(pitch.getId())
                .name(pitch.getName())
                .pitchType(pitch.getPitchType().name())
                .surface(pitch.getSurface())
                .hourlyPrice(pitch.getHourlyPrice())
                .status(pitch.getStatus().name())
                .build()).collect(Collectors.toList());
    }

    @Override
    public PitchResponse getPitchById(Long id) {
        Pitch pitch = pitchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sân bóng với ID: " + id));

        return PitchResponse.builder()
                .id(pitch.getId())
                .name(pitch.getName())
                .pitchType(pitch.getPitchType().name())
                .surface(pitch.getSurface())
                .hourlyPrice(pitch.getHourlyPrice())
                .status(pitch.getStatus().name())
                .build();
    }

    @Override
    public List<PitchResponse> getAllPitches() {
        List<Pitch> pitches = pitchRepository.findAll();

        return pitches.stream().map(pitch -> PitchResponse.builder()
                .id(pitch.getId())
                .name(pitch.getName())
                .pitchType(pitch.getPitchType().name())
                .surface(pitch.getSurface())
                .hourlyPrice(pitch.getHourlyPrice())
                .status(pitch.getStatus().name())
                .build()
        ).collect(Collectors.toList());
    }

    // ============ ADMIN: THÊM SÂN BÓNG MỚI ============
    @Override
    @Transactional
    public PitchResponse createPitch(PitchRequest request) {
        // Nếu có truyền venueId thì tìm theo venueId, nếu không thì lấy Cụm sân đầu tiên trong DB
        Venue venue;
        if (request.getVenueId() != null) {
            venue = venueRepository.findById(request.getVenueId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy cụm sân với ID: " + request.getVenueId()));
        } else {
            venue = venueRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Chưa có Cụm sân (Venue) nào trong Database!"));
        }

        Pitch pitch = Pitch.builder()
                .venue(venue)
                .name(request.getName())
                .pitchType(Pitch.PitchType.valueOf(request.getPitchType()))
                .surface(request.getSurface())
                .hourlyPrice(request.getHourlyPrice())
                .slotMinutes(60)
                .status(request.getStatus() != null
                        ? Pitch.Status.valueOf(request.getStatus())
                        : Pitch.Status.ACTIVE)
                .build();

        Pitch savedPitch = pitchRepository.save(pitch);

        return PitchResponse.builder()
                .id(savedPitch.getId())
                .name(savedPitch.getName())
                .pitchType(savedPitch.getPitchType().name())
                .surface(savedPitch.getSurface())
                .hourlyPrice(savedPitch.getHourlyPrice())
                .status(savedPitch.getStatus().name())
                .build();
    }

    // ============ ADMIN: CẬP NHẬT SÂN BÓNG ============
    @Override
    @Transactional
    public PitchResponse updatePitch(Long id, PitchRequest request) {
        Pitch pitch = pitchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sân bóng với ID: " + id));

        if (request.getName() != null && !request.getName().isEmpty()) {
            pitch.setName(request.getName());
        }
        if (request.getPitchType() != null && !request.getPitchType().isEmpty()) {
            pitch.setPitchType(Pitch.PitchType.valueOf(request.getPitchType()));
        }
        if (request.getSurface() != null) {
            pitch.setSurface(request.getSurface());
        }
        if (request.getHourlyPrice() != null) {
            pitch.setHourlyPrice(request.getHourlyPrice());
        }
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            pitch.setStatus(Pitch.Status.valueOf(request.getStatus()));
        }

        Pitch updatedPitch = pitchRepository.save(pitch);

        return PitchResponse.builder()
                .id(updatedPitch.getId())
                .name(updatedPitch.getName())
                .pitchType(updatedPitch.getPitchType().name())
                .surface(updatedPitch.getSurface())
                .hourlyPrice(updatedPitch.getHourlyPrice())
                .status(updatedPitch.getStatus().name())
                .build();
    }

    // ============ ADMIN: XÓA SÂN BÓNG ============
    @Override
    @Transactional
    public void deletePitch(Long id) {
        Pitch pitch = pitchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sân bóng với ID: " + id));
        pitchRepository.delete(pitch);
    }
}