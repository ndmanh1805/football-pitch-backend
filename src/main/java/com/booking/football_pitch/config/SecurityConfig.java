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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor 
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter; 

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            // 1. BẬT CORS VÀ TRỎ TỚI CẤU HÌNH PHÍA DƯỚI
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/users/register", "/api/v1/users/login").permitAll()
                .requestMatchers("/api/v1/venues/**", "/api/v1/pitches/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    // 2. THÊM BỘ CẤU HÌNH MỞ CỬA CHO FRONTEND (CORS)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Cho phép Frontend ở cổng 5173 và 5174 truy cập
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174")); 
        
        // Cho phép các phương thức HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Cho phép gửi tất cả các Headers (bao gồm cả thẻ Authorization chứa JWT)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Bắt buộc phải có dòng này để Frontend có thể đính kèm thẻ Token vào request
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Áp dụng luật mở cửa này cho toàn bộ đường dẫn API
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }
}