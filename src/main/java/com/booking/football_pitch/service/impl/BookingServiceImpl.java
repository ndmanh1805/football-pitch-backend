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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    // ================= TÍNH NĂNG MỚI NẰM Ở ĐÂY =================
    @Override
    public List<BookingResponse> getBookingHistory(Long userId) {
        // 1. Nhờ Repository tìm toàn bộ hóa đơn của User này trong Database
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        // 2. Biến đổi (Map) danh sách Entity thô thành danh sách DTO sạch sẽ để trả về
        return bookings.stream().map(booking -> BookingResponse.builder()
                .id(booking.getId())
                .customerName(booking.getUser().getFullName())
                .pitchName(booking.getPitch().getName())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .totalPrice(booking.getTotalAmount())
                .status(booking.getStatus().name())
                .build()).collect(Collectors.toList());
    }
    // ================= TÍNH NĂNG MỚI: HỦY ĐẶT SÂN =================
    @Override
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, com.booking.football_pitch.dto.request.CancelBookingRequest request) {
        
        // 1. Tìm đơn đặt sân trong DB
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn đặt sân này."));

        // 2. Bảo mật: Chặn đứng hành vi hủy trộm sân của người khác
        if (!booking.getUser().getId().equals(request.getUserId())) {
            throw new RuntimeException("Lỗi bảo mật: Bạn không có quyền hủy đơn đặt sân của người khác!");
        }

        // 3. Ràng buộc nghiệp vụ: Không cho phép hủy nếu đơn đã hoàn thành hoặc đã hủy
        if (booking.getStatus() == Booking.Status.CANCELLED) {
            throw new RuntimeException("Đơn đặt sân này đã được hủy trước đó rồi.");
        }
        if (booking.getStatus() == Booking.Status.COMPLETED) {
            throw new RuntimeException("Không thể hủy đơn đặt sân đã đá xong.");
        }

        // 4. Thực hiện Hủy: Đổi trạng thái, ghi nhận thời gian và lý do
        booking.setStatus(Booking.Status.CANCELLED);
        booking.setCancelledAt(java.time.LocalDateTime.now());
        booking.setCancelReason(request.getReason());

        Booking savedBooking = bookingRepository.save(booking);

        // 5. Trả về kết quả mới cho Frontend
        return BookingResponse.builder()
                .id(savedBooking.getId())
                .customerName(savedBooking.getUser().getFullName())
                .pitchName(savedBooking.getPitch().getName())
                .bookingDate(savedBooking.getBookingDate())
                .startTime(savedBooking.getStartTime())
                .endTime(savedBooking.getEndTime())
                .totalPrice(savedBooking.getTotalAmount())
                .status(savedBooking.getStatus().name())
                .build();
    }
    @Override
    public java.util.List<com.booking.football_pitch.dto.response.BookedSlotResponse> getBookedSlots(Long pitchId, java.time.LocalDate date) {
        // Lấy danh sách các hóa đơn đã đặt thành công
        java.util.List<Booking> bookedList = bookingRepository.findBookedSlotsByPitchAndDate(pitchId, date);
        
        // Chỉ trích xuất đúng 2 thông tin là Giờ bắt đầu và Giờ kết thúc trả về cho Frontend
        return bookedList.stream().map(b -> com.booking.football_pitch.dto.response.BookedSlotResponse.builder()
                .startTime(b.getStartTime())
                .endTime(b.getEndTime())
                .build()).collect(java.util.stream.Collectors.toList());
    }
}