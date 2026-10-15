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
    // Lấy chi tiết một sân bóng cụ thể để hiển thị trên trang Đặt Lịch
    @GetMapping("/{id}")
    public ResponseEntity<PitchResponse> getPitchById(@PathVariable Long id) {
        return ResponseEntity.ok(pitchService.getPitchById(id));
    }
    // API cho Admin lấy toàn bộ sân bóng
    @GetMapping("/all")
    public ResponseEntity<List<PitchResponse>> getAllPitches() {
        return ResponseEntity.ok(pitchService.getAllPitches());
    }
    // API cho Admin thêm sân bóng mới
    @org.springframework.web.bind.annotation.PostMapping
    public org.springframework.http.ResponseEntity<com.booking.football_pitch.dto.response.PitchResponse> createPitch(
            @org.springframework.web.bind.annotation.RequestBody com.booking.football_pitch.dto.request.PitchRequest request) {
        return org.springframework.http.ResponseEntity.ok(pitchService.createPitch(request));
    }

    // API cho Admin cập nhật thông tin sân bóng
    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    public org.springframework.http.ResponseEntity<com.booking.football_pitch.dto.response.PitchResponse> updatePitch(
            @org.springframework.web.bind.annotation.PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestBody com.booking.football_pitch.dto.request.PitchRequest request) {
        return org.springframework.http.ResponseEntity.ok(pitchService.updatePitch(id, request));
    }

    // API cho Admin xóa sân bóng
    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<String> deletePitch(
            @org.springframework.web.bind.annotation.PathVariable Long id) {
        pitchService.deletePitch(id);
        return org.springframework.http.ResponseEntity.ok("Đã xóa sân bóng thành công!");
    }
}
