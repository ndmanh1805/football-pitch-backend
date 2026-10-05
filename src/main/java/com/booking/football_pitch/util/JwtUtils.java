package com.booking.football_pitch.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    // 1. Chữ ký bí mật (Tuyệt đối không để lộ). Phải dài ít nhất 256-bit (32 ký tự).
    private static final String SECRET_STRING = "DayLaChuyenMatMaTuyetMatCuaDuAnBookingSanBongCuaManh2026";
    private final Key key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // 2. Thời hạn của Thẻ từ (Ví dụ: 86400000 millisecond = 24 giờ)
    private final long EXPIRATION_TIME = 86400000L;

    // 3. Hàm in Thẻ từ (Ghi tên email của khách vào thẻ)
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // Ghi nhận chủ nhân thẻ
                .setIssuedAt(new Date()) // Ngày giờ phát hành
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Ngày giờ hết hạn
                .signWith(key, SignatureAlgorithm.HS256) // Đóng mộc đỏ bằng chữ ký bí mật
                .compact(); // Đóng gói thành chuỗi
    }
}