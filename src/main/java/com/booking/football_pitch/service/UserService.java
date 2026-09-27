package com.booking.football_pitch.service;

import com.booking.football_pitch.dto.request.UserRegisterRequest;
import com.booking.football_pitch.dto.response.UserResponse;

public interface UserService {

	UserResponse registerUser(UserRegisterRequest request);
}
