package com.booking.football_pitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.booking.football_pitch.entity.Booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Hàm kiểm tra xem sân đã bị đặt trong khoảng thời gian khách chọn chưa
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.pitch.id = :pitchId " +
           "AND b.bookingDate = :bookingDate " +
           "AND b.status IN ('PENDING', 'CONFIRMED') " +
           "AND b.startTime < :endTime " +
           "AND b.endTime > :startTime")
    boolean existsConflictingBooking(
            @Param("pitchId") Long pitchId,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
    // TÍNH NĂNG MỚI: Tìm toàn bộ lịch sử đặt sân của 1 người dùng, xếp theo thời gian mới nhất
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
}
