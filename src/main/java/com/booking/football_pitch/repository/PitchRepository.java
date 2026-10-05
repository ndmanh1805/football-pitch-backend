package com.booking.football_pitch.repository;

import com.booking.football_pitch.entity.Pitch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PitchRepository extends JpaRepository<Pitch, Long> {

	// Câu lệnh SQL "Triệu đô" xử lý tìm sân trống cực kỳ tối ưu
    @Query("SELECT p FROM Pitch p WHERE p.venue.id = :venueId " +
           "AND p.status = 'ACTIVE' " +
           "AND p.id NOT IN (" +
           "    SELECT b.pitch.id FROM Booking b " +
           "    WHERE b.bookingDate = :bookingDate " +
           "    AND b.status IN ('PENDING', 'CONFIRMED') " +
           "    AND b.startTime < :endTime " +
           "    AND b.endTime > :startTime" +
           ")")
    List<Pitch> findAvailablePitches(
            @Param("venueId") Long venueId,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // Tìm danh sách sân theo ID của Cụm sân (Venue)
    java.util.List<com.booking.football_pitch.entity.Pitch> findByVenueId(Long venueId);
}
