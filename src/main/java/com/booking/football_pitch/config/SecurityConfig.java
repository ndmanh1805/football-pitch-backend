package com.booking.football_pitch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Tạo "Cỗ máy" băm mật khẩu BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Cấu hình phân quyền (Tạm thời mở khóa tất cả để không hỏng code cũ)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF bảo vệ mặc định vì chúng ta viết REST API
            .cors(cors -> cors.disable()) // Tạm tắt CORS
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Lệnh "Đặc xá": Mở cửa cho toàn bộ API đi qua
            );
            
        return http.build();
    }
}