package com.booking.football_pitch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booking.football_pitch.entity.Pitch;

@Repository
public interface PitchRepository extends JpaRepository<Pitch, Long> {

	List<Pitch> findByVenueIdAndStatus(Long venueId, Pitch.Status status);
}
