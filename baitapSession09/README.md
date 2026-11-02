# Session09 - Bài 1 đến 4: Quản lý phòng ban, nhân viên và lỗi nghiệp vụ

Java 21, Spring Boot, JPA và PostgreSQL. Các bài của Session09 tiếp tục làm chung trong project này.

## Chạy ứng dụng

1. Mở project bằng IntelliJ, chọn JDK 21.
2. Chạy `BaitapSession09Application`, Working directory là thư mục `baitapSession09`.
3. Dùng Postman thử API bên dưới.

Database `hrm_session09` và kết nối đã được chuẩn bị trên máy hiện tại. Mật khẩu nằm ngoài project tại `../.local-config/baitapSession09.properties`.

Máy khác cần tạo database bằng `CREATE DATABASE hrm_session09;`, sau đó đặt biến môi trường `DB_USERNAME`, `DB_PASSWORD`. Có thể dùng `DB_URL` để đổi URL kết nối. Hibernate tự tạo bảng departments nhờ `ddl-auto=update`.

## Tạo phòng ban

Chọn **POST → Body → raw → JSON** trong Postman:

```text
POST http://localhost:8080/api/v1/departments
Content-Type: application/json
```

```json
{
  "name": "Phòng Nhân sự",
  "description": "Quản lý nhân sự công ty"
}
```

- name bắt buộc, không được chỉ chứa khoảng trắng, dài 5–50 ký tự.
- description không bắt buộc, tối đa 100 ký tự; có thể bỏ trường này, gửi null hoặc chuỗi rỗng.
- Thành công trả **201 Created**, ApiResponse có status=SUCCESS và data chứa Department gồm id tự tăng, name, description; dữ liệu được lưu vào PostgreSQL.
- Thiếu/sai name hoặc description quá dài trả **400 Bad Request**, ApiResponse có status=FAIL và data chứa thông báo theo từng trường; không lưu bản ghi.

Controller nhận `@Valid @RequestBody DepartmentDTO`. DTO dùng `@NotBlank`, `@Size`; Service gọi DepartmentRepository (JpaRepository) để lưu entity.

## Cấu trúc phản hồi (Bài 2)

`ApiResponse<T>` gồm status (SUCCESS/FAIL), message và data. Controller bọc kết quả thành công; GlobalExceptionHandler dùng `@RestControllerAdvice`, `@ExceptionHandler` để bọc lỗi.

Ví dụ khi tạo thành công:

```json
{
  "status": "SUCCESS",
  "message": "Tạo phòng ban thành công",
  "data": {
    "id": 1,
    "name": "Phòng Nhân sự",
    "description": "Quản lý nhân sự công ty"
  }
}
```

Gửi `{"name":"ABCD"}` sẽ nhận HTTP 400:

```json
{
  "status": "FAIL",
  "message": "Dữ liệu không hợp lệ",
  "data": {
    "name": "Tên phòng ban phải có từ 5 đến 50 ký tự"
  }
}
```

Handler bắt MethodArgumentNotValidException, lấy field và defaultMessage từ BindingResult vào Map. Một trường có nhiều lỗi thì giữ thông báo đầu tiên cho trường đó. JSON sai/thiếu body cũng trả HTTP 400 và ApiResponse với status=FAIL, data=null. Các lỗi HTTP khác như sai URL, sai phương thức hoặc Content-Type cũng dùng wrapper, giữ nguyên mã HTTP tương ứng.

Kiểm tra bằng pgAdmin:

```sql
SELECT id, name, description FROM departments ORDER BY id;
```

## Thêm nhân viên (Bài 3)

Tạo phòng ban bằng API ở trên, lấy id để sử dụng làm departmentId. Chọn **POST → Body → raw → JSON**:

```text
POST http://localhost:8080/api/v1/employees
Content-Type: application/json
```

```json
{
  "fullName": "Nguyen Van An",
  "email": "an@example.com",
  "phone": "0987654321",
  "salary": 5000000,
  "departmentId": 1
}
```

Dùng key `salary` (đề gõ nhầm sala0ry). DTO kiểm tra email bằng @Email; phone dùng @Pattern `^0[35789][0-9]{8}$`; salary dùng @Min(5000000); departmentId dùng @NotNull. Họ tên, email, phone và salary bắt buộc. Thông báo lỗi đều bằng tiếng Việt.

Thành công trả **201 Created**, ApiResponse có status=SUCCESS và data chứa nhân viên, kèm đối tượng department. Sai định dạng trả **400**, status=FAIL, data chứa lỗi từng trường. Ví dụ phone="12345" trả data.phone="Số điện thoại không hợp lệ". Phòng ban không tồn tại trả **404**, status=FAIL, message="Phòng ban không tồn tại".

Employee dùng @ManyToOne và @JoinColumn(name="department_id") để tham chiếu Department, Hibernate tạo khóa ngoại thật trong PostgreSQL. Quan hệ một chiều nên JSON trả về không bị lặp vô hạn. Salary dùng BigDecimal, lưu numeric(19,2).

```sql
SELECT e.id, e.full_name, e.email, e.phone, e.salary, e.department_id, d.name
FROM employees e JOIN departments d ON d.id = e.department_id
ORDER BY e.id;
```

## Custom Exception cho lỗi nghiệp vụ (Bài 4)

API vẫn là `POST /api/v1/employees`. Sau khi DTO vượt qua @Valid, EmployeeService xử lý theo thứ tự:

1. Tìm phòng ban; không thấy thì ném ResourceNotFoundException với message `Phòng ban không tồn tại`.
2. Gọi EmployeeRepository.existsByEmailIgnoreCase để kiểm tra email; đã tồn tại thì ném DuplicateResourceException với message `Email đã được sử dụng`. So sánh email không phân biệt hoa/thường.
3. Nếu hợp lệ thì lưu nhân viên và trả HTTP 201 với status=SUCCESS.

GlobalExceptionHandler map ResourceNotFoundException thành **404**, DuplicateResourceException thành **409 Conflict**. Ví dụ gửi lại email của nhân viên vừa tạo:

```json
{
  "status": "FAIL",
  "message": "Email đã được sử dụng",
  "data": null
}
```

Lỗi định dạng vẫn trả **400** với lỗi từng trường trong data; lỗi nghiệp vụ trả **404/409** với thông báo cụ thể và data=null. Các request bị từ chối không thêm hoặc cập nhật nhân viên.

Để thử: tạo một nhân viên hợp lệ, gửi lại cùng email để nhận 409; gửi một email mới với departmentId=999 (nếu ID này không có) để nhận 404.

Build/cache nằm ngoài project khi kiểm tra. Thư mục nộp không chứa mật khẩu. Khi kéo thả GitHub, bỏ qua build/.gradle/.idea nếu IntelliJ tạo thêm sau này.
