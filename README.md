#  Football Pitch Booking System - Backend API

Mã nguồn **Backend** của dự án Hệ Thống Đặt Sân Bóng Đá Trực Tuyến. Dự án cung cấp hệ thống RESTful API hoàn chỉnh phục vụ nghiệp vụ tìm kiếm sân theo cơ sở, đặt lịch thi đấu chống trùng giờ, quản lý hồ sơ người dùng và bảng điều khiển quản trị viên (Admin Dashboard).

 **Liên kết kho lưu trữ Frontend (ReactJS):** [football-pitch-frontend](https://github.com/ndmanh1805/football-pitch-frontend)

---

##  Tác giả & Chủ sở hữu
* **Họ và tên:** Nguyễn Đức Mạnh
* **GitHub:** [@ndmanh1805](https://github.com/ndmanh1805)
* **Vai trò:** Full-stack Developer (Java Spring Boot & ReactJS)

---

## Công nghệ & Công cụ sử dụng (Tech Stack)
* **Ngôn ngữ:** Java (JDK 17+)
* **Framework chính:** Spring Boot 3.x (Spring Web, Spring Data JPA, Spring Security)
* **Cơ sở dữ liệu:** MySQL
* **Bảo mật & Xác thực:** JWT (JSON Web Token - `jjwt`), BCrypt Password Encoder
* **Thư viện hỗ trợ:** Project Lombok
* **Quản lý gói & Build:** Maven (`mvnw`)
* **Công cụ kiểm thử API:** Postman

---

## Kiến trúc hệ thống & Mô hình thiết kế (Architecture & Patterns)

Dự án được xây dựng theo các tiêu chuẩn kỹ thuật thực tế tại doanh nghiệp:

1. **Kiến trúc phân tầng (Layered Architecture):**
   * `Controller Layer`: Tiếp nhận HTTP Request, định tuyến RESTful API và trả về chuẩn `ResponseEntity`.
   * `Service Layer` (`Service` & `ServiceImpl`): Xử lý toàn bộ logic nghiệp vụ (kiểm tra trùng lịch đặt sân, tính tổng tiền tự động theo khung giờ, kiểm tra trạng thái khóa tài khoản, phân quyền).
   * `Repository Layer`: Giao tiếp với cơ sở dữ liệu MySQL thông qua Spring Data JPA / Hibernate.
   * `Entity Layer`: Ánh xạ cấu trúc các bảng trong MySQL (`User`, `Role`, `Venue`, `Pitch`, `Booking`).
2. **Mô hình DTO (Data Transfer Object Pattern):**
   * Tách biệt hoàn toàn dữ liệu đầu vào (`Request DTO`) và đầu ra (`Response DTO`) với `Entity`.
   * **Mục đích bảo mật:** Ngăn chặn lộ thông tin nhạy cảm (`passwordHash`), chống lỗ hổng *Mass Assignment* khi cập nhật dữ liệu và tránh lỗi đệ quy vô tận (Infinite Recursion) khi serialize các mối quan hệ `@OneToMany` / `@ManyToOne`.
3. **Cơ chế Xác thực Phi trạng thái (Stateless JWT Authentication):**
   * Sử dụng bộ lọc `JwtAuthenticationFilter` đứng trước `UsernamePasswordAuthenticationFilter` để kiểm tra chữ ký số của Token trong Header `Authorization: Bearer <token>`.
   * Mật khẩu người dùng được băm một chiều bằng thuật toán `BCrypt` trước khi lưu xuống MySQL.
4. **Quản lý Giao dịch & Ràng buộc Khóa ngoại:**
   * Sử dụng `@Transactional` đảm bảo tính toàn vẹn dữ liệu (ACID) khi tạo đơn, hủy đơn hoặc thay đổi quyền người dùng.
   * Thiết lập `cascade = CascadeType.ALL` và `orphanRemoval = true` giúp xử lý đồng bộ dữ liệu liên kết khi xóa sân bóng mà không gây lỗi khóa ngoại.

---

## Các tính năng & API chính (API Endpoints)

### 1. Xác thực & Người dùng (`/api/v1/users`)
* `POST /api/v1/users/register`: Đăng ký tài khoản mới, mã hóa mật khẩu, gán quyền mặc định `USER` và cấp phát ngay JWT Token.
* `POST /api/v1/users/login`: Đăng nhập, kiểm tra trạng thái tài khoản (`ACTIVE` / `LOCKED`) và trả về thông tin kèm Token và Role.
* `GET /api/v1/users/{id}`: Xem thông tin hồ sơ cá nhân.
* `PUT /api/v1/users/{id}`: Cập nhật Họ tên và Số điện thoại cá nhân.
* `GET /api/v1/users/all`: *(Admin)* Lấy danh sách toàn bộ người dùng kèm quyền hạn.
* `PUT /api/v1/users/{id}/role`: *(Admin)* Chuyển đổi quyền người dùng (`USER` $\leftrightarrow$ `ADMIN`).
* `PUT /api/v1/users/{id}/status`: *(Admin)* Khóa hoặc mở khóa tài khoản (`ACTIVE` $\leftrightarrow$ `LOCKED`).

### 2. Cụm sân & Sân bóng (`/api/v1/pitches`)
* `GET /api/v1/pitches/venue/{venueId}`: Lọc danh sách các sân bóng thuộc một cơ sở cụ thể (Cầu Giấy, Mỹ Đình, Bách Khoa).
* `GET /api/v1/pitches/all`: Lấy toàn bộ danh sách sân bóng trên hệ thống.
* `POST /api/v1/pitches`: *(Admin)* Thêm sân bóng mới.
* `PUT /api/v1/pitches/{id}`: *(Admin)* Cập nhật thông tin, giá thuê theo giờ và trạng thái sân (`ACTIVE`, `MAINTENANCE`, `INACTIVE`).
* `DELETE /api/v1/pitches/{id}`: *(Admin)* Xóa sân bóng khỏi hệ thống.

### 3. Đặt sân & Quản lý đơn (`/api/v1/bookings`)
* `POST /api/v1/bookings`: Tạo đơn đặt sân mới (tự động kiểm tra chống đặt trùng khung giờ và tính tổng tiền).
* `GET /api/v1/bookings/pitch/{pitchId}/booked-slots?date=YYYY-MM-DD`: Tra cứu các khung giờ đã có người đặt trong ngày của một sân.
* `GET /api/v1/bookings/user/{userId}`: Xem lịch sử đặt sân của cá nhân người dùng.
* `PUT /api/v1/bookings/{bookingId}/cancel`: Khách hàng hủy đơn đặt sân.
* `GET /api/v1/bookings/all`: *(Admin)* Lấy toàn bộ danh sách đơn đặt sân trên hệ thống.
* `PUT /api/v1/bookings/{bookingId}/status?status=CONFIRMED|CANCELLED`: *(Admin)* Duyệt đơn hoặc Hủy đơn đặt sân.

---

## Hướng dẫn cài đặt và chạy chương trình

### 1. Yêu cầu hệ thống
* **Java:** JDK 17 trở lên.
* **Database:** MySQL Server (đang chạy ở cổng `3306`).
* **IDE:** IntelliJ IDEA, VS Code hoặc Cursor.

### 2. Các bước khởi chạy
**Bước 1: Clone kho lưu trữ về máy**
```bash
git clone [https://github.com/ndmanh1805/football-pitch-backend.git](https://github.com/ndmanh1805/football-pitch-backend.git)
cd football-pitch-backend
```

**Bước 2: Cấu hình Cơ sở dữ liệu MySQL**
* Tạo database trong MySQL (ví dụ: `football_pitch_booking`).
* Kiểm tra và cập nhật thông tin tài khoản `username` / `password` của MySQL trong file `src/main/resources/application.properties` (hoặc `application.yml`).

**Bước 3: Chạy ứng dụng Spring Boot**
```bash
./mvnw spring-boot:run
```
*(Hoặc mở dự án trong IDE và nhấn **Run** tại file `FootballPitchApplication.java`).*

* Máy chủ Backend sẽ khởi chạy mặc định tại địa chỉ: `http://localhost:8080`