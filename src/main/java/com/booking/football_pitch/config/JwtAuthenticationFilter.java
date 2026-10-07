package com.booking.football_pitch.config;

import com.booking.football_pitch.entity.User;
import com.booking.football_pitch.repository.UserRepository;
import com.booking.football_pitch.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils; // Máy quét thẻ
    private final UserRepository userRepository; // Cuốn sổ hộ khẩu (Database) để tra tên

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Khách đưa thẻ: Lấy thẻ từ "Header" của Request (tên quy chuẩn quốc tế là "Authorization")
        String authHeader = request.getHeader("Authorization");

        // 2. Kiểm tra xem thẻ có chuẩn định dạng không (Thẻ chuẩn phải bắt đầu bằng chữ "Bearer ")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Cắt bỏ chữ "Bearer " (7 ký tự) để lấy đúng phần lõi thẻ

            // 3. Đưa thẻ vào máy quét kiểm tra thật/giả và xem đã hết hạn chưa
            if (jwtUtils.validateToken(token)) {
                // 4. Thẻ hợp lệ -> Đọc tên khách (email)
                String email = jwtUtils.extractEmail(token);

                // 5. Kiểm tra xem khách có tồn tại trong "Sổ hộ khẩu" không
                User user = userRepository.findByEmail(email).orElse(null);

                if (user != null) {
                    // 6. Cho phép đi qua: Lưu thông tin khách vào "Bộ nhớ của hệ thống" (SecurityContext)
                    // Kể từ giây phút này, API Đặt sân sẽ tự động biết người đang thao tác là ai!
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user, null, new ArrayList<>() // Tạm thời danh sách quyền hạn để rỗng
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // 7. Kiểm tra xong xuôi, cho khách đi tiếp tới cửa tiếp theo
        filterChain.doFilter(request, response);
    }
}