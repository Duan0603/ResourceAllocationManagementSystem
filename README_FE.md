# Project Resource Allocation Management System - Frontend (FE)

Tài liệu hướng dẫn cài đặt, phát triển và sử dụng phần **Frontend Dashboard** của Hệ thống Quản lý Phân bổ Nhân sự.

---

## 🛠️ Công nghệ Sử dụng

- **Core**: HTML5, Vanilla JavaScript (ES6+)
- **Styling**: Vanilla CSS (CSS Variables, Flexbox, CSS Grid) thiết kế theo phong cách hiện đại Glassmorphism với màu sắc hài hòa và responsive đầy đủ.
- **HTTP Client**: Native Fetch API để kết nối trực tiếp với các REST API của Backend.
- **Font**: Google Fonts (Outfit) đem lại trải nghiệm giao diện cao cấp.

---

## 📁 Cấu trúc Thư mục Frontend

Giao diện người dùng được tích hợp trực tiếp dưới dạng Single Page Application (SPA) nằm trong thư mục tài nguyên tĩnh của Spring Boot:
```text
d:\OJT\week1
└── src
    └── main
        └── resources
            └── static
                └── index.html   # Chứa toàn bộ cấu trúc HTML, CSS (style tag) và Logic JS (script tag)
```

Không cần cấu hình hay chạy thêm bất kỳ dev server nào riêng biệt (như Vite, Webpack hay cài đặt package node_modules qua npm).

---

## 🚀 Hướng dẫn Khởi chạy Frontend

### Yêu cầu hệ thống
- Đã khởi chạy Backend Spring Boot thành công (cổng mặc định `8080`).

### Cách truy cập
Khi backend Spring Boot đã chạy (`mvn spring-boot:run` hoặc chạy từ IDE), bạn chỉ cần mở trình duyệt và truy cập:
🔗 **[http://localhost:8080](http://localhost:8080)**

---

## 💡 Các tính năng chính trên Giao diện

Giao diện cung cấp bảng điều khiển (Dashboard) tương tác thời gian thực:
1. **Báo cáo & Thống kê động (Dashboard Stats)**:
   - Thống kê tổng số lượng nhân sự, số lượng dự án, tỷ lệ sử dụng trung bình.
   - Báo cáo chi tiết tỷ lệ sử dụng (Utilization Report), danh sách nhân sự rảnh rỗi (Available Report) và quá tải (Overloaded Report) được tính toán tự động bằng thuật toán client-side trên dữ liệu thực tế.
2. **Quản lý Nhân sự (Employees)**: Thêm, sửa, xóa, tìm kiếm danh sách nhân sự trực tiếp.
3. **Quản lý Kỹ năng (Manage Skills)**: Hộp thoại (Popup Modal) cao cấp cho phép thêm mới nhiều kỹ năng và nhấp chọn xóa nhanh bằng dấu `x` trực quan, hiển thị tag kỹ năng ngay trên bảng nhân viên.
4. **Tìm kiếm Nhân sự theo Kỹ năng Tức thì (Real-time contains search)**: Ô nhập liệu cho phép tìm lọc ngay danh sách nhân viên sở hữu kỹ năng tương ứng (contains, case-insensitive) kèm capacity khả dụng thời gian thực ngay khi gõ.
5. **Quản lý Dự án (Projects)**: Tạo mới, cập nhật trạng thái (`PLANNING`, `ACTIVE`, `COMPLETED`) và xem danh sách dự án.
6. **Phân bổ Nguồn lực (Resource Allocations)**:
   - Gán nhân sự vào dự án cụ thể với tỷ lệ phần trăm capacity, vai trò và thời gian.
   - Hệ thống tự động validate capacity trống và ngăn chặn phân bổ hoạt động (PENDING + ACTIVE) vượt quá 100% hoặc gán vào dự án đã hoàn thành.
7. **Quy trình Trạng thái Phân bổ (Status Badges & Controls)**:
   - Hiển thị badge trạng thái phân bổ (`PENDING`, `ACTIVE`, `ENDED`) rõ ràng.
   - Thêm nút Kích hoạt phân bổ (tích xanh) và Kết thúc phân bổ (ô vuông cam) trực quan trên mỗi dòng phân bổ.
8. **Trợ lý AI (Gemini Heuristic AI Assistant)**:
   - **Đề xuất nguồn lực (Recommend Employees)**: Gõ yêu cầu bằng tiếng Việt/tiếng Anh (Ví dụ: *"Tìm Java Developer còn tối thiểu 50% available."*), trợ lý AI sẽ tự động phân tích và lọc ra danh sách thích hợp.
   - **Cảnh báo rủi ro (Risk Detection)**: Nhập yêu cầu để trợ lý AI tính toán tỷ lệ tải trung bình của nhóm kỹ năng và cảnh báo rủi ro về mặt nguồn lực.
9. **Kiểm tra trạng thái hệ thống**: Hiển thị trạng thái kết nối Database thời gian thực (Database Connected/Offline) thông qua polling API tự động.
10. **Thứ tự sắp xếp**: Danh sách nhân viên và tìm kiếm tự động được sắp xếp theo ID tăng dần (`e.employeeId ASC`) giúp giao diện luôn ngăn nắp và ổn định.
