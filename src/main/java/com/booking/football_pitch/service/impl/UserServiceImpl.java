package com.booking.football_pitch.service.impl;

import org.springframework.stereotype.Service;

import com.booking.football_pitch.entity.User;
import com.booking.football_pitch.repository.UserRepository;
import com.booking.football_pitch.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public User registerUser(User user) {
		if (userRepository.findByPhone(user.getPhone()).isPresent()) {
			throw new RuntimeException("Số điện thoại đã được sử dụng");
		}
		return userRepository.save(user);
	}
}
