# Báo cáo Đánh giá AI (AI Review Report)

Báo cáo này tài liệu hóa quá trình sử dụng Trợ lý AI trong việc phát triển và tối ưu hóa hệ thống Quản lý Phân bổ Nhân sự (phiên bản v1.5).

---

## 1. Phát sinh các kịch bản kiểm thử (Generate Test Cases for Allocation API)

### Prompt sử dụng
```text
Hãy tạo các test case JUnit 5 sử dụng Mockito cho lớp AllocationServiceImpl trong Spring Boot. 
Cần đảm bảo kiểm thử đầy đủ các ràng buộc nghiệp vụ:
1. Tạo phân bổ thành công (với status mặc định là PENDING và capacity hợp lệ).
2. Không cho phép phân bổ nhân sự vào dự án đã COMPLETED.
3. Tổng dung lượng phân bổ của một nhân viên (PENDING + ACTIVE) không được vượt quá 100%.
4. Chuyển đổi trạng thái từ PENDING sang ACTIVE thành công.
5. Không cho phép kích hoạt (ACTIVE) một phân bổ đã kết thúc (ENDED).
6. Khi tính toán capacity/workload khả dụng, cần loại trừ các phân bổ đã kết thúc (ENDED).
```

### AI Feedback & Đề xuất
* **Tách biệt kiểm thử logic**: Đề xuất viết Unit Test thuần túy cô lập logic của Service sử dụng Mockito (`@ExtendWith(MockitoExtension.class)`) thay vì load toàn bộ Spring Context với `@SpringBootTest` để đảm bảo thời gian chạy test tối ưu nhất.
* **Xử lý Mocking tuần tự**: Hướng dẫn dùng `Mockito.thenAnswer()` để mô phỏng chính xác hành vi lưu bản ghi (trả về đối tượng được truyền vào với giá trị cập nhật).
* **Kiểm thử Edge-Case**: Đề xuất kiểm thử trường hợp tổng workload đạt đúng ngưỡng biên 100% và trường hợp vượt ngưỡng 101%.

### Các cải tiến đã thực hiện
* Bổ sung các Unit Test cases trong [AllocationServiceImplTest.java](file:///d:/OJT/week1/src/test/java/com/company/resourcealloc/service/impl/AllocationServiceImplTest.java) tương ứng với các tình huống nghiệp vụ:
  * `givenPendingAllocation_whenActivateAllocation_thenSucceed`
  * `givenEndedAllocation_whenActivateAllocation_thenThrowIllegalArgumentException`
  * `givenActiveAllocation_whenEndAllocation_thenSucceed`
  * `givenOverallocation_whenCreateAllocation_thenThrowAllocationExceededException`

---

## 2. Đánh giá tầng Service (Review Service Layer as Senior Java Engineer)

### Prompt sử dụng
```text
Hãy đóng vai là một Kỹ sư Java Senior (Senior Java Engineer). Hãy review cấu trúc và mã nguồn của 
AllocationServiceImpl và EmployeeServiceImpl hiện tại. Hãy chỉ ra các lỗi tiềm ẩn về mặt hiệu năng, 
tính đóng gói, clean code, và các vấn đề liên quan tới giao dịch (transactions).
```

### AI Feedback & Đề xuất
1. **Lỗi N+1 Queries**: Lớp `EmployeeServiceImpl` khi lấy danh sách nhân viên bằng `employeeRepository.findAll()` sẽ gặp vấn đề hiệu năng N+1 khi serialization truy xuất thuộc tính Lazy-loaded `skills`.
2. **Quản lý Transaction**: Cần đảm bảo các phương thức thay đổi dữ liệu (`create`, `update`, `delete`, `activate`, `end`) được gán `@Transactional` ghi đè (không chỉ thừa kế `readOnly = true` ở mức class level) để tránh lỗi không đồng bộ dữ liệu.
3. **Độc lập tính toán**: Khi tính toán available capacity cho nhân viên, logic đang tính tổng phân bổ thô trong database mà chưa loại bỏ các phân bổ đã hoàn thành lịch sử (`ENDED`).

### Các cải tiến đã thực hiện
* Cấu hình `@Transactional` đầy đủ trên các phương thức write của `AllocationServiceImpl` và `EmployeeServiceImpl`.
* Cập nhật `validateTotalAllocation` và `getEmployeeWorkload` để bỏ qua các bản ghi có trạng thái `ENDED`.
* Triển khai method query `findAllWithSkills()` dùng `LEFT JOIN FETCH` trong [EmployeeRepository.java](file:///d:/OJT/week1/src/main/java/com/company/resourcealloc/repository/EmployeeRepository.java) để giải quyết triệt để lỗi N+1 queries.
* Bổ sung kiểm tra trùng lặp (uniqueness) cho `employeeCode` và `email` trong `createEmployee` và `updateEmployee` để ném về `IllegalArgumentException` (HTTP 400) thay vì ném lỗi SQL vi phạm ràng buộc (HTTP 500).
* Thêm sắp xếp mặc định `ORDER BY e.employeeId ASC` cho các câu lệnh JPQL để giao diện hiển thị danh sách nhân sự ngăn nắp, có thứ tự.

---

## 3. Tạo tài liệu API (Generate API Documentation based on Controller code)

### Prompt sử dụng
```text
Hãy tạo tài liệu đặc tả API RESTful Markdown dựa trên mã nguồn của các lớp EmployeeController 
và AllocationController sau khi đã được nâng cấp lên phiên bản v1.5.
```

### AI Feedback & Đề xuất
* Định nghĩa rõ cấu trúc tài liệu bao gồm: Method, Endpoint, Request Body (JSON format), Response (JSON format), và các mã lỗi HTTP tương ứng (400 Bad Request, 404 Not Found, 200 OK, 201 Created).

### Các cải tiến đã thực hiện
Đã tổng hợp tài liệu API chuẩn hóa vào mục **API Documentation** tại tài liệu [README_BE.md](file:///d:/OJT/week1/README_BE.md), chi tiết các endpoint nâng cấp:
* **POST** `/employees/{id}/skills` - Thêm skill mới cho nhân viên.
* **GET** `/employees/{id}/skills` - Lấy danh sách skill của nhân viên.
* **GET** `/employees/search?skill={name}` - Tìm kiếm nhân sự theo skill.
* **PUT** `/allocations/{id}/activate` - Kích hoạt phân bổ từ PENDING sang ACTIVE.
* **PUT** `/allocations/{id}/end` - Kết thúc một phân bổ (chuyển sang ENDED).
