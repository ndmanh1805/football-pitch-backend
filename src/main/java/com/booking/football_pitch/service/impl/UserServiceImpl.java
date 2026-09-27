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
}
