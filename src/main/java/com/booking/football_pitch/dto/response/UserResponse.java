package com.booking.football_pitch.dto.response;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
    // Thêm dòng này vào danh sách các biến
    private String token;
}
