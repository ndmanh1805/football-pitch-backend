package com.booking.football_pitch.service.impl;

import com.booking.football_pitch.dto.request.BookingRequest;
import com.booking.football_pitch.dto.response.BookingResponse;
import com.booking.football_pitch.entity.Booking;
import com.booking.football_pitch.entity.Pitch;
import com.booking.football_pitch.entity.User;
import com.booking.football_pitch.repository.BookingRepository;
import com.booking.football_pitch.repository.PitchRepository;
import com.booking.football_pitch.repository.UserRepository;
import com.booking.football_pitch.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PitchRepository pitchRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        
        // 1. Kiểm tra tính hợp lệ của thời gian
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new RuntimeException("Thời gian bắt đầu phải trước thời gian kết thúc.");
        }

        // 2. Chặn lỗi đè lịch (Race Condition)
        boolean isConflict = bookingRepository.existsConflictingBooking(
                request.getPitchId(),
                request.getBookingDate(),
                request.getStartTime(),
                request.getEndTime()
        );
        if (isConflict) {
            throw new RuntimeException("Rất tiếc, sân bóng đã có người đặt trong khung giờ này.");
        }

        // 3. Truy xuất dữ liệu
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng."));
                
        Pitch pitch = pitchRepository.findById(request.getPitchId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin sân bóng."));

        // 4. Nghiệp vụ Tài chính
        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
                
        // FIX 1: Dùng biến totalAmount cho chuẩn với DB
        BigDecimal totalAmount = pitch.getHourlyPrice().multiply(hours);

        // 5. Khởi tạo Entity
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setPitch(pitch);
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        
        // Gắn tiền vào đúng hàm setTotalAmount
        booking.setTotalAmount(totalAmount); 
        
        // FIX 2: Sinh mã Code ngẫu nhiên cho đơn hàng (Bắt buộc 12 ký tự)
        // Ví dụ: Tạo ra chuỗi "BK" + 10 ký tự ngẫu nhiên
        String randomCode = "BK" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        booking.setCode(randomCode);
        
        booking.setStatus(Booking.Status.PENDING); 

        Booking savedBooking = bookingRepository.save(booking);

        // 6. Map dữ liệu ra Response (Lấy tiền từ getTotalAmount)
        return BookingResponse.builder()
                .id(savedBooking.getId())
                .customerName(user.getFullName())
                .pitchName(pitch.getName())
                .bookingDate(savedBooking.getBookingDate())
                .startTime(savedBooking.getStartTime())
                .endTime(savedBooking.getEndTime())
                .totalPrice(savedBooking.getTotalAmount()) 
                .status(savedBooking.getStatus().name())
                .build();
    }
}