package com.booking.football_pitch.dto.request;
import lombok.Data;

@Data
public class UserRegisterRequest {
    private String fullName;
    private String email;
    private String phone;
    private String password;
}
