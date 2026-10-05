package com.booking.football_pitch.service.impl;

import com.booking.football_pitch.dto.request.UserRegisterRequest;
import com.booking.football_pitch.dto.response.UserResponse;
import com.booking.football_pitch.entity.User;
import com.booking.football_pitch.repository.UserRepository;
import com.booking.football_pitch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public UserResponse registerUser(UserRegisterRequest request) {
        // Kiểm tra trùng số điện thoại
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        }

        // Map từ Request sang Entity để lưu Database
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(request.getPasswordHash());
        // FIX LỖI TẠI ĐÂY: Ép kiểu từ String (DTO) sang Enum (Entity) một cách an toàn
        if (request.getStatus() != null) {
            user.setStatus(User.Status.valueOf(request.getStatus().toUpperCase()));
        } else {
            user.setStatus(User.Status.ACTIVE); // Mặc định là ACTIVE nếu không truyền
        }
        User savedUser = userRepository.save(user);
        // Map từ Entity sang Response để trả về cho Client
        return UserResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                // FIX LỖI TẠI ĐÂY: Chuyển từ Enum (Entity) về lại String để hiển thị ra JSON
                .status(savedUser.getStatus().name())
                .createdAt(savedUser.getCreatedAt())
                .build();
	}
    @Override
    public com.booking.football_pitch.dto.response.UserResponse getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng."));

        // Trả về DTO an toàn (đã xóa dòng gọi getUsername)
        return com.booking.football_pitch.dto.response.UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public com.booking.football_pitch.dto.response.UserResponse updateUserProfile(Long id, com.booking.football_pitch.dto.request.UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng."));

        // Cập nhật thông tin nếu có truyền lên
        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhone(request.getPhone());
        }

        User savedUser = userRepository.save(user);

        // Đã xóa dòng gọi getUsername
        return com.booking.football_pitch.dto.response.UserResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .phone(savedUser.getPhone())
                .email(savedUser.getEmail())
                .build();
    }
}
