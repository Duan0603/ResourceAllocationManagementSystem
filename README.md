# Resource Allocation Management System

## 1. Tổng quan
Hệ thống quản lý phân bổ nguồn lực (Resource Allocation Management System) dành cho các công ty phần mềm, đặc biệt là các công ty outsourcing quản lý nhiều dự án song song. Hệ thống giúp quản lý thông tin nhân viên, dự án, chi tiết phân bổ công việc, theo dõi tải công việc (workload/utilization) và cung cấp các tính năng thông minh tích hợp trí tuệ nhân tạo (đề xuất nhân sự tối ưu, nhận diện rủi ro quá tải).

## 2. Cách chạy Database & Backend (Spring Boot)
- **Yêu cầu:** Java 17+, PostgreSQL 15+, Docker (nếu chạy qua Docker Compose).
- **Cấu hình Cơ sở dữ liệu:**
  - Tên database: `resource_alloc_db`
  - Tài khoản / Mật khẩu: `postgres` / `postgres`
  - *(Có thể tùy chỉnh thông qua các biến môi trường hoặc cấu hình trong file [application.yml](file:///d:/OJT/week1/src/main/resources/application.yml))*
- **Cách khởi động nhanh bằng Docker Compose:**
  Tại thư mục gốc của dự án:
  ```bash
  docker-compose up -d
  ```
  Lệnh này sẽ tự động khởi chạy database PostgreSQL 15 và dịch vụ pgAdmin (truy cập tại `http://localhost:5050`).
- **Cách chạy ứng dụng Spring Boot:**
  Tại thư mục gốc của dự án:
  ```bash
  mvn clean install
  mvn spring-boot:run
  ```
- **Tài liệu API (Swagger UI):**
  Truy cập: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) để xem và thử nghiệm trực tiếp các API.

## 3. Cách chạy Frontend (Tích hợp tĩnh)
- **Cơ chế triển khai:** 
  Dự án này tích hợp giao diện người dùng trực tiếp dưới dạng tài nguyên tĩnh (Single Page Application viết bằng HTML/CSS/JS) nằm tại đường dẫn [src/main/resources/static/index.html](file:///d:/OJT/week1/src/main/resources/static/index.html).
- **Cách sử dụng:**
  Không cần cài đặt Node.js hay npm riêng biệt. Khi Backend Spring Boot được khởi chạy thành công ở bước 2, giao diện sẽ tự động được phục vụ.
- **Truy cập:** [http://localhost:8080](http://localhost:8080)

## 4. Các chức năng
- **Quản lý Nhân viên & Dự án (CRUD):** Thêm, sửa, xóa, tìm kiếm thông tin nhân viên và các dự án (`PLANNING`, `ACTIVE`, `COMPLETED`).
- **Quản lý Phân bổ (Allocations):** Gán nhân sự vào dự án theo tỷ lệ phần trăm (%), thời gian bắt đầu, kết thúc và vai trò.
- **Ràng buộc nghiệp vụ mạnh mẽ (Business Rules):**
  - Giới hạn phân bổ mỗi dòng từ $1\%$ đến $100\%$.
  - Quy tắc trần 100% dung lượng (Max Capacity Rule): Tổng tỷ lệ phân bổ hoạt động tại một thời điểm của nhân viên không được vượt quá $100\%$.
  - Từ chối phân bổ vào dự án đã hoàn thành (`COMPLETED`).
  - Đảm bảo logic ngày bắt đầu trước ngày kết thúc.
- **Báo cáo Workload:** Theo dõi tỷ lệ sử dụng (Utilization) và dung lượng khả dụng (Availability) của từng nhân viên.
- **Tính năng AI thông minh:**
  - Đề xuất tài nguyên (AI Resource Recommendation) phù hợp dựa trên ngôn ngữ tự nhiên.
  - Phân tích rủi ro quá tải (AI Risk Detection) nguồn lực trong tương lai.
- **Giao diện hiện đại:** Thiết kế theo phong cách Glassmorphism với các hiệu ứng chuyển động mượt mà, responsive tốt.

## 5. Kết nối FE <-> BE
- Giao diện Frontend gọi trực tiếp tới các REST API tương đối trên cùng máy chủ (ví dụ: `/employees`, `/projects`, `/allocations`).
- Xử lý lỗi toàn cục (Global Exception Handler) phía Backend trả về các mã lỗi có cấu trúc JSON rõ ràng. Giao diện Frontend hiển thị thông báo lỗi và trạng thái trực quan, realtime.

## 6. Đóng gói bàn giao
Toàn bộ source code của dự án được đóng gói tại thư mục gốc, bao gồm các thành phần:
- [src/main/java/com/company/resourcealloc](file:///d:/OJT/week1/src/main/java/com/company/resourcealloc): Toàn bộ mã nguồn Java của Backend (Controller, Service, Repository, Model, DTO, Exception).
- [src/main/resources/static/index.html](file:///d:/OJT/week1/src/main/resources/static/index.html): Giao diện người dùng tích hợp.
- [pom.xml](file:///d:/OJT/week1/pom.xml): Cấu hình dependencies của dự án Maven.
- [docker-compose.yml](file:///d:/OJT/week1/docker-compose.yml): Cấu hình môi trường cơ sở dữ liệu Docker.
- [resource_alloc_postman_collection.json](file:///d:/OJT/week1/resource_alloc_postman_collection.json): Bộ sưu tập Postman chứa danh sách API mẫu giúp kiểm thử nhanh chóng.
