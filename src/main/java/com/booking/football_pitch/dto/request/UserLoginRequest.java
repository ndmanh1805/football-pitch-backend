package com.booking.football_pitch.dto.request;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String email;
    private String password;
}