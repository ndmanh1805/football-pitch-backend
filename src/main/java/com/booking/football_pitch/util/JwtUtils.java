package com.booking.football_pitch.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    // 1. Chữ ký bí mật (Tuyệt đối không để lộ)
    private static final String SECRET_STRING = "DayLaChuyenMatMaTuyetMatCuaDuAnBookingSanBongCuaManh2026";
    private final Key key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // 2. Thời hạn của Thẻ từ (24 giờ)
    private final long EXPIRATION_TIME = 86400000L;

    // 3. Hàm in Thẻ từ (Ghi tên email của khách vào thẻ)
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ============ TÍNH NĂNG MỚI THÊM HÔM NAY ============

    // 4. Máy quét đọc tên khách (Lấy email từ Token)
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Lấy chữ ký bí mật ra để soi
                .build()
                .parseClaimsJws(token) // Bóc vỏ Token
                .getBody()
                .getSubject(); // Lấy chữ Email bên trong
    }

    // 5. Kiểm tra thẻ thật/giả hoặc hết hạn chưa
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true; // Thẻ hợp lệ!
        } catch (Exception e) {
            System.out.println("Thẻ giả hoặc đã hết hạn: " + e.getMessage());
            return false; // Bắt được thẻ vớ vẩn!
        }
    }
}