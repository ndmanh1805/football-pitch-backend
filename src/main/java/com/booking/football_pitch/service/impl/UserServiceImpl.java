package com.booking.football_pitch.service.impl;

import com.booking.football_pitch.entity.Role;
import com.booking.football_pitch.entity.User;
import com.booking.football_pitch.repository.RoleRepository;
import com.booking.football_pitch.repository.UserRepository;
import com.booking.football_pitch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository; // <--- Bổ sung Repository quản lý Quyền
    private final PasswordEncoder passwordEncoder;
    private final com.booking.football_pitch.util.JwtUtils jwtUtils;
    
    // ============ ĐĂNG KÝ ============
    @Override
    public com.booking.football_pitch.dto.response.UserResponse registerUser(com.booking.football_pitch.dto.request.UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng!");
        }

        String hashedPass = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(hashedPass);
        user.setStatus(User.Status.ACTIVE);

        User savedUser = userRepository.save(user);
        String jwtToken = jwtUtils.generateToken(user.getEmail());
        
        return com.booking.football_pitch.dto.response.UserResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .status(savedUser.getStatus().name())
                .token(jwtToken)
                .build();
    }
    
    @Override
    public com.booking.football_pitch.dto.response.UserResponse getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng."));

        return com.booking.football_pitch.dto.response.UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .build();
    }

    @Override
    @Transactional
    public com.booking.football_pitch.dto.response.UserResponse updateUserProfile(Long id, com.booking.football_pitch.dto.request.UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng."));

        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhone(request.getPhone());
        }

        User savedUser = userRepository.save(user);

        return com.booking.football_pitch.dto.response.UserResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .phone(savedUser.getPhone())
                .email(savedUser.getEmail())
                .status(savedUser.getStatus().name())
                .build();
    }

    // ============ ĐĂNG NHẬP ============
    @Override
    @Transactional(readOnly = true)
    public com.booking.football_pitch.dto.response.UserResponse login(com.booking.football_pitch.dto.request.UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại!"));

        // Chặn đăng nhập nếu tài khoản đã bị Admin khóa
        if (user.getStatus() == User.Status.LOCKED) {
            throw new RuntimeException("Tài khoản của bạn đã bị khóa! Vui lòng liên hệ Admin.");
        }

        boolean isMatch = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        if (!isMatch) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }

        String jwtToken = jwtUtils.generateToken(user.getEmail()); 

        String roleName = "USER";
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            roleName = user.getRoles().iterator().next().getCode();
        }

        return com.booking.football_pitch.dto.response.UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .role(roleName)
                .token(jwtToken)
                .build();
    }

    // ============ ADMIN: LẤY DANH SÁCH NGƯỜI DÙNG ============
    @Override
    @Transactional(readOnly = true)
    public java.util.List<com.booking.football_pitch.dto.response.UserResponse> getAllUsers() {
        java.util.List<User> users = userRepository.findAll();
        
        return users.stream().map(user -> {
            String roleName = "USER"; 
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                roleName = user.getRoles().iterator().next().getCode(); 
            }

            return com.booking.football_pitch.dto.response.UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus() != null ? user.getStatus().name() : "ACTIVE")
                .role(roleName) 
                .build();
                
        }).collect(java.util.stream.Collectors.toList());
    }

    // ============ ADMIN: ĐỔI QUYỀN (USER <-> ADMIN) ============
    @Override
    @Transactional
    public com.booking.football_pitch.dto.response.UserResponse toggleUserRole(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng có ID: " + id));

        // Kiểm tra quyền hiện tại
        String currentRole = "USER";
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            currentRole = user.getRoles().iterator().next().getCode();
        }

        // Đảo quyền: Nếu đang là ADMIN thì xuống USER, ngược lại lên ADMIN
        String targetRoleCode = "ADMIN".equalsIgnoreCase(currentRole) ? "USER" : "ADMIN";

        // Tìm Role trong DB, nếu chưa có thì tự động tạo mới luôn để không bị lỗi
        Role targetRole = roleRepository.findByCode(targetRoleCode)
                .orElseGet(() -> roleRepository.save(Role.builder().code(targetRoleCode).build()));

        user.getRoles().clear();
        user.getRoles().add(targetRole);
        User savedUser = userRepository.save(user);

        return com.booking.football_pitch.dto.response.UserResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .status(savedUser.getStatus().name())
                .role(targetRole.getCode())
                .build();
    }

    // ============ ADMIN: KHÓA / MỞ KHÓA TÀI KHOẢN ============
    @Override
    @Transactional
    public com.booking.football_pitch.dto.response.UserResponse toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng có ID: " + id));

        // Đảo trạng thái: Đang ACTIVE thì thành LOCKED, đang LOCKED thì mở lại ACTIVE
        if (user.getStatus() == User.Status.ACTIVE) {
            user.setStatus(User.Status.LOCKED);
        } else {
            user.setStatus(User.Status.ACTIVE);
        }

        User savedUser = userRepository.save(user);

        String roleName = "USER";
        if (savedUser.getRoles() != null && !savedUser.getRoles().isEmpty()) {
            roleName = savedUser.getRoles().iterator().next().getCode();
        }

        return com.booking.football_pitch.dto.response.UserResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .status(savedUser.getStatus().name())
                .role(roleName)
                .build();
    }
}