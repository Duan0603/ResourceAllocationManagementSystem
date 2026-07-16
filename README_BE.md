# Project Resource Allocation Management System - Backend (BE)

Tài liệu hướng dẫn phát triển, cấu hình và khởi chạy phần **Backend** của Hệ thống Quản lý Phân bổ Nhân sự.

---

## 🛠️ Công nghệ Sử dụng

- **Ngôn ngữ & Framework**: Java 17+, Spring Boot 3.3.x, Spring Data JPA
- **Database**: PostgreSQL 15, Hibernate
- **Validation**: Jakarta Validation API (`@NotBlank`, `@Email`, `@Min`, `@Max`)
- **API Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Tích hợp AI**: Bộ phân tích Heuristic cục bộ (phân tích ngôn ngữ tự nhiên từ khóa và dung lượng)
- **Công cụ bổ trợ**: Docker, Maven, Lombok, JUnit 5, Mockito

---

## 📁 Cấu trúc Thư mục Backend

```text
d:\OJT\week1
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.company.resourcealloc
│   │   │       ├── ResourceAllocApplication.java # Entry point của ứng dụng
│   │   │       ├── controller/                  # Tầng REST Controllers (API Endpoints)
│   │   │       ├── dto/                         # Các Data Transfer Objects (Request/Response)
│   │   │       ├── model/                       # Tầng thực thể JPA (Database Mapping)
│   │   │       ├── exception/                   # Xử lý lỗi tập trung (Global Exception Handler)
│   │   │       ├── repository/                  # Tầng thao tác với Database (Spring Data JPA)
│   │   │       └── service/                     # Tầng xử lý nghiệp vụ chính (Business Logic & AI)
│   │   └── resources
│   │       ├── static/                      # Giao diện người dùng tĩnh (Single Page Application)
│   │       ├── application.yml              # File cấu hình cấu trúc & biến môi trường
│   │       └── schema.sql                   # Script SQL khởi tạo database trong dự án
│   └── test
│       └── java
│           └── com.company.resourcealloc.service.impl
│               └── AllocationServiceImplTest.java # Unit tests cho tầng Service
├── docker-compose.yml                       # Khởi chạy PostgreSQL trong Docker
├── schema.sql                               # Script SQL khởi tạo cấu trúc bảng ở thư mục gốc
├── resource_alloc_postman_collection.json   # Bộ sưu tập Postman gốc
└── PRAA.postman_collection.json            # File cấu hình Postman để import và test (được cập nhật)
```

---

## 🚀 Hướng dẫn Cài đặt & Chạy Backend

### Bước 1: Khởi chạy Database
Chạy lệnh sau tại thư mục gốc dự án để khởi tạo PostgreSQL thông qua Docker Compose:
```bash
docker-compose up -d
```
*Lưu ý:* Cơ sở dữ liệu sẽ tự động tạo database `resource_alloc_db` với username/password mặc định là `postgres` / `postgres` như đã cấu hình trong [docker-compose.yml](file:///d:/OJT/week1/docker-compose.yml).

### Bước 2: Chạy ứng dụng Spring Boot
Chạy lệnh sau tại thư mục gốc để khởi động ứng dụng:
```bash
mvn spring-boot:run
```
Ứng dụng backend và frontend tĩnh sẽ hoạt động tại địa chỉ: 🔗 **[http://localhost:8080](http://localhost:8080)**

---

## 📖 API Documentation & Hướng dẫn Kiểm thử

### 1. Swagger UI (Tài liệu trực quan)
Truy cập đường dẫn sau khi ứng dụng đã khởi động để xem danh sách API và test trực tiếp trên trình duyệt:
🔗 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### 2. Danh sách API Endpoints chính

| Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| **GET** | `/api/health` | Kiểm tra trạng thái kết nối Database & hệ thống |
| **POST** | `/employees` | Tạo mới nhân viên |
| **GET** | `/employees` | Lấy danh sách nhân viên |
| **GET** | `/employees/{id}` | Lấy thông tin nhân viên theo ID |
| **PUT** | `/employees/{id}` | Cập nhật thông tin nhân viên |
| **DELETE** | `/employees/{id}` | Xóa nhân viên |
| **GET** | `/employees/{id}/workload` | Tính toán workload và capacity khả dụng của một nhân viên |
| **POST** | `/employees/{id}/skills` | Cập nhật/Thêm danh sách kỹ năng cho nhân viên |
| **GET** | `/employees/{id}/skills` | Lấy danh sách kỹ năng của nhân viên |
| **GET** | `/employees/search?skill={name}` | Tìm kiếm nhân viên sở hữu kỹ năng tương ứng (contains match) |
| **POST** | `/projects` | Tạo mới dự án |
| **GET** | `/projects` | Lấy danh sách dự án |
| **GET** | `/projects/{id}` | Lấy thông tin dự án theo ID |
| **PUT** | `/projects/{id}` | Cập nhật thông tin dự án |
| **DELETE** | `/projects/{id}` | Xóa dự án |
| **POST** | `/allocations` | Tạo mới phân bổ (Mặc định ở trạng thái PENDING) |
| **PUT** | `/allocations/{id}` | Cập nhật thông tin phân bổ nhân sự |
| **DELETE**| `/allocations/{id}` | Xóa bỏ phân bổ nhân sự |
| **GET** | `/allocations` | Lấy danh sách phân bổ nhân sự |
| **PUT** | `/allocations/{id}/activate` | Kích hoạt phân bổ từ PENDING sang ACTIVE |
| **PUT** | `/allocations/{id}/end` | Kết thúc một phân bổ (chuyển sang ENDED) |
| **POST** | `/api/ai/recommend` | AI đề xuất nhân viên theo yêu cầu bằng ngôn ngữ tự nhiên |
| **POST** | `/api/ai/risk-detect` | AI phân tích và cảnh báo rủi ro về nguồn lực |

*Lưu ý:* Các báo cáo thống kê hiển thị trên giao diện (Dashboard Reports) được tính toán động ở phía Client-side từ kết quả của các API `/employees` và `/allocations`, hệ thống backend không cung cấp các endpoint `/reports/*` trực tiếp để tránh dư thừa dữ liệu.

### 3. Sử dụng Postman
Bạn có thể Import file [PRAA.postman_collection.json](file:///d:/OJT/week1/PRAA.postman_collection.json) trực tiếp vào Postman của bạn để chạy toàn bộ testcase được thiết lập sẵn.

---

## 🧪 Chạy Unit Tests
Để thực hiện việc kiểm thử tự động toàn bộ tầng Service để xác nhận các quy định nghiệp vụ:
```bash
mvn test
```
Các kịch bản kiểm thử bao gồm:
- Tạo phân bổ thành công cho nhân viên trong giới hạn capacity.
- Trả về lỗi `IllegalArgumentException` khi cố tình allocate nhân viên vào dự án đã `COMPLETED`.
- Trả về lỗi `AllocationExceededException` khi tổng dung lượng phân bổ của nhân viên vượt quá 100%.
