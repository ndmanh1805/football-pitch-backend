package com.booking.football_pitch.controller;

import com.booking.football_pitch.dto.request.UserRegisterRequest;
import com.booking.football_pitch.dto.response.UserResponse;
import com.booking.football_pitch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@RequestBody UserRegisterRequest request) {
        UserResponse createdUser = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // Xem thông tin cá nhân
    @org.springframework.web.bind.annotation.GetMapping("/{id}")
    public org.springframework.http.ResponseEntity<com.booking.football_pitch.dto.response.UserResponse> getUserProfile(
            @org.springframework.web.bind.annotation.PathVariable Long id) {
        return org.springframework.http.ResponseEntity.ok(userService.getUserProfile(id));
    }

    // Cập nhật thông tin cá nhân
    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    public org.springframework.http.ResponseEntity<com.booking.football_pitch.dto.response.UserResponse> updateUserProfile(
            @org.springframework.web.bind.annotation.PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestBody com.booking.football_pitch.dto.request.UserUpdateRequest request) {
        return org.springframework.http.ResponseEntity.ok(userService.updateUserProfile(id, request));
    }
}
