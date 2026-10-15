package com.booking.football_pitch.service;

import java.util.List;

import com.booking.football_pitch.dto.request.UserRegisterRequest;
import com.booking.football_pitch.dto.response.UserResponse;

public interface UserService {

	UserResponse registerUser(UserRegisterRequest request);

	// Lấy thông tin cá nhân
    com.booking.football_pitch.dto.response.UserResponse getUserProfile(Long id);

    // Cập nhật thông tin cá nhân
    com.booking.football_pitch.dto.response.UserResponse updateUserProfile(Long id, com.booking.football_pitch.dto.request.UserUpdateRequest request);

	com.booking.football_pitch.dto.response.UserResponse login(com.booking.football_pitch.dto.request.UserLoginRequest request);
    // Admin: Lấy danh sách toàn bộ người dùng
    java.util.List<com.booking.football_pitch.dto.response.UserResponse> getAllUsers();

    // Admin: Đổi quyền người dùng (USER <-> ADMIN)
    com.booking.football_pitch.dto.response.UserResponse toggleUserRole(Long id);

    // Admin: Khóa / Mở khóa tài khoản (ACTIVE <-> LOCKED)
    com.booking.football_pitch.dto.response.UserResponse toggleUserStatus(Long id);
}
