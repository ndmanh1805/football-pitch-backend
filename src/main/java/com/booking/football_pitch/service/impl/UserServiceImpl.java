package com.booking.football_pitch.service.impl;

import com.booking.football_pitch.entity.User;
import com.booking.football_pitch.repository.UserRepository;
import com.booking.football_pitch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // <--- Import cỗ máy
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // <--- Nhúng cỗ máy vào đây

    // ============ TÍNH NĂNG MỚI: ĐĂNG KÝ ============
    @Override
    public com.booking.football_pitch.dto.response.UserResponse registerUser(com.booking.football_pitch.dto.request.UserRegisterRequest request) {
        
        // 1. Kiểm tra xem Email hoặc Số điện thoại đã bị ai đăng ký chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng!");
        }

        // 2. BĂM MẬT KHẨU: Đưa password raw (vd: 123456) qua máy xay BCrypt
        String hashedPass = passwordEncoder.encode(request.getPassword());

        // 3. Khởi tạo User mới
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(hashedPass); // Lưu chuỗi loằng ngoằng vào DB, tuyệt đối không lưu raw
        user.setStatus(User.Status.ACTIVE);

        User savedUser = userRepository.save(user);

        // 4. Trả về thông tin (KHÔNG BAO GIỜ TRẢ VỀ PASSWORD)
        return com.booking.football_pitch.dto.response.UserResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
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

    // ============ TÍNH NĂNG MỚI: ĐĂNG NHẬP ============
    @Override
    public com.booking.football_pitch.dto.response.UserResponse login(com.booking.football_pitch.dto.request.UserLoginRequest request) {
        // 1. Tìm User dưới Database bằng Email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại!"));

        // 2. Kiểm tra mật khẩu (Dùng phép thuật .matches() của BCrypt)
        boolean isMatch = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        if (!isMatch) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }

        // 3. Mật khẩu đúng -> Trả về thông tin User (tuyệt đối không trả về mật khẩu)
        return com.booking.football_pitch.dto.response.UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }
}
