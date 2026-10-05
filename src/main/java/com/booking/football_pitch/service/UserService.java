package com.booking.football_pitch.service;

import com.booking.football_pitch.dto.request.UserRegisterRequest;
import com.booking.football_pitch.dto.response.UserResponse;

public interface UserService {

	UserResponse registerUser(UserRegisterRequest request);

	// Lấy thông tin cá nhân
    com.booking.football_pitch.dto.response.UserResponse getUserProfile(Long id);

    // Cập nhật thông tin cá nhân
    com.booking.football_pitch.dto.response.UserResponse updateUserProfile(Long id, com.booking.football_pitch.dto.request.UserUpdateRequest request);
}
