package com.booking.football_pitch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // <--- Cần có chữ này để gọi Chú bảo vệ
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter; // <--- Gọi chú bảo vệ đến

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF (vì dùng API)
            .cors(cors -> cors.disable()) // Tạm tắt CORS (sau làm Frontend sẽ bật lại)
            .authorizeHttpRequests(auth -> auth
                // 1. Cửa xanh (Không cần thẻ): Ai cũng vào được
                .requestMatchers("/api/v1/users/register", "/api/v1/users/login").permitAll()
                .requestMatchers("/api/v1/venues/**", "/api/v1/pitches/**").permitAll()
                
                // 2. Cửa đỏ (Bắt buộc xét thẻ): Tất cả các đường link còn lại (như Đặt sân, Cập nhật Profile...)
                .anyRequest().authenticated()
            )
            // 3. Bố trí Chú bảo vệ đứng trước cánh cửa mặc định của Spring Boot
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
}