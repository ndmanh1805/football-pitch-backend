package com.booking.football_pitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booking.football_pitch.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
