# Hướng dẫn cập nhật phân quyền API theo ma trận quyền mới

> File này dùng làm context cho AI IDE. **Nguồn sự thật (source of truth)** là bảng ma trận ở mục 2 và file `V1__init_schema.sql` (các câu `INSERT INTO role_permissions`). Nếu code API đang kiểm tra quyền khác bảng này thì sửa code, **không sửa ma trận**.

## 1. Bối cảnh

- Hệ thống quản lý thư viện, CSDL SQL Server, migration kiểu Flyway (`V1__init_schema.sql`).
- Mô hình: `users.role_id → roles`; `roles ↔ permissions` qua bảng `role_permissions`. Mã quyền (`permissions.code`) có dạng `<RESOURCE>_<READ|WRITE|DELETE>`.
- 4 vai trò: `ADMIN` (quản trị), `LIBRARIAN` (thủ thư), `MEMBER` (thành viên có thẻ), `READER` (độc giả chưa có thẻ, chỉ xem).
- Số quyền mỗi vai trò (ADMIN | LIBRARIAN | MEMBER | READER): **45 | 32 | 14 | 10** trên tổng 45.
- Xóa mềm toàn hệ thống (`is_deleted`), vì vậy `*_DELETE` nghĩa là xóa mềm hoặc hủy theo nghiệp vụ.

## 2. Ma trận quyền (nguồn sự thật)

| Nhóm | Mã quyền | Mô tả | ADMIN | LIBRARIAN | MEMBER | READER |
|---|---|---|:--:|:--:|:--:|:--:|
| Tài khoản | `USER_READ` | Xem tài khoản người dùng | ✅ | ✅ | ✅ | ✅ |
| Tài khoản | `USER_WRITE` | Tạo và cập nhật tài khoản người dùng | ✅ | — | ✅ | ✅ |
| Tài khoản | `USER_DELETE` | Xóa mềm tài khoản người dùng | ✅ | — | — | — |
| Vai trò và quyền | `ROLE_READ` | Xem danh sách vai trò | ✅ | ✅ | ✅ | ✅ |
| Vai trò và quyền | `ROLE_WRITE` | Tạo và cập nhật vai trò | ✅ | — | — | — |
| Vai trò và quyền | `ROLE_DELETE` | Xóa mềm vai trò | ✅ | — | — | — |
| Vai trò và quyền | `PERMISSION_READ` | Xem danh sách quyền | ✅ | — | — | — |
| Vai trò và quyền | `PERMISSION_WRITE` | Tạo và cập nhật quyền | ✅ | — | — | — |
| Vai trò và quyền | `PERMISSION_DELETE` | Xóa mềm quyền | ✅ | — | — | — |
| Sách và các danh mục liên quan | `BOOK_READ` | Xem và tìm kiếm sách | ✅ | ✅ | ✅ | ✅ |
| Sách và các danh mục liên quan | `BOOK_WRITE` | Tạo và cập nhật sách | ✅ | ✅ | — | — |
| Sách và các danh mục liên quan | `BOOK_DELETE` | Xóa mềm sách | ✅ | ✅ | — | — |
| Sách và các danh mục liên quan | `CATEGORY_READ` | Xem thể loại sách | ✅ | ✅ | ✅ | ✅ |
| Sách và các danh mục liên quan | `CATEGORY_WRITE` | Tạo và cập nhật thể loại sách | ✅ | ✅ | — | — |
| Sách và các danh mục liên quan | `CATEGORY_DELETE` | Xóa mềm thể loại sách | ✅ | ✅ | — | — |
| Sách và các danh mục liên quan | `AUTHOR_READ` | Xem tác giả | ✅ | ✅ | ✅ | ✅ |
| Sách và các danh mục liên quan | `AUTHOR_WRITE` | Tạo và cập nhật tác giả | ✅ | ✅ | — | — |
| Sách và các danh mục liên quan | `AUTHOR_DELETE` | Xóa mềm tác giả | ✅ | ✅ | — | — |
| Sách và các danh mục liên quan | `PUBLISHER_READ` | Xem nhà xuất bản | ✅ | ✅ | ✅ | ✅ |
| Sách và các danh mục liên quan | `PUBLISHER_WRITE` | Tạo và cập nhật nhà xuất bản | ✅ | ✅ | — | — |
| Sách và các danh mục liên quan | `PUBLISHER_DELETE` | Xóa mềm nhà xuất bản | ✅ | ✅ | — | — |
| Sách và các danh mục liên quan | `SHELF_READ` | Xem kệ sách | ✅ | ✅ | ✅ | ✅ |
| Sách và các danh mục liên quan | `SHELF_WRITE` | Tạo và cập nhật kệ sách | ✅ | ✅ | — | — |
| Sách và các danh mục liên quan | `SHELF_DELETE` | Xóa mềm kệ sách | ✅ | ✅ | — | — |
| Thành viên | `MEMBER_READ` | Xem thông tin thành viên | ✅ | ✅ | — | — |
| Thành viên | `MEMBER_WRITE` | Tạo và cập nhật thành viên | ✅ | ✅ | — | — |
| Thành viên | `MEMBER_DELETE` | Xóa mềm thành viên | ✅ | ✅ | — | — |
| Mượn trả | `BORROW_READ` | Xem phiếu mượn và chi tiết mượn | ✅ | ✅ | ✅ | — |
| Mượn trả | `BORROW_WRITE` | Lập phiếu mượn, gia hạn và ghi nhận trả sách | ✅ | ✅ | — | — |
| Mượn trả | `BORROW_DELETE` | Hủy phiếu mượn theo quy định | ✅ | ✅ | — | — |
| Tiền phạt | `FINE_READ` | Xem thông tin tiền phạt | ✅ | ✅ | ✅ | — |
| Tiền phạt | `FINE_WRITE` | Tạo và cập nhật khoản phạt | ✅ | ✅ | — | — |
| Tiền phạt | `FINE_DELETE` | Hủy khoản phạt theo quy định | ✅ | ✅ | — | — |
| Tiền phạt | `FINE_PAYMENT_READ` | Xem thông tin thanh toán tiền phạt | ✅ | ✅ | ✅ | — |
| Tiền phạt | `FINE_PAYMENT_WRITE` | Ghi nhận thanh toán tiền phạt | ✅ | ✅ | — | — |
| Tiền phạt | `FINE_PAYMENT_DELETE` | Hủy giao dịch thanh toán tiền phạt theo quy định | ✅ | — | — | — |
| Thanh toán thẻ thành viên | `MEMBER_PAYMENT_READ` | Xem hóa đơn đăng ký và gia hạn thẻ | ✅ | ✅ | ✅ | — |
| Thanh toán thẻ thành viên | `MEMBER_PAYMENT_WRITE` | Ghi nhận thanh toán phí thẻ thành viên | ✅ | ✅ | — | — |
| Thanh toán thẻ thành viên | `MEMBER_PAYMENT_DELETE` | Hủy giao dịch thanh toán phí thẻ theo quy định | ✅ | — | — | — |
| Cài đặt và thông báo | `SETTING_READ` | Xem cài đặt hệ thống | ✅ | — | — | — |
| Cài đặt và thông báo | `SETTING_WRITE` | Tạo và cập nhật cài đặt hệ thống | ✅ | — | — | — |
| Cài đặt và thông báo | `SETTING_DELETE` | Xóa cài đặt hệ thống | ✅ | — | — | — |
| Cài đặt và thông báo | `NOTIFICATION_READ` | Xem thông báo | ✅ | ✅ | ✅ | ✅ |
| Cài đặt và thông báo | `NOTIFICATION_WRITE` | Tạo thông báo hoặc đánh dấu đã đọc | ✅ | ✅ | ✅ | ✅ |
| Cài đặt và thông báo | `NOTIFICATION_DELETE` | Xóa mềm thông báo | ✅ | — | — | — |

Ý nghĩa: `READ` = xem/tra cứu; `WRITE` = tạo, cập nhật hoặc thao tác ghi nghiệp vụ; `DELETE` = xóa mềm/hủy.

## 3. Thay đổi so với ma trận cũ

- **LIBRARIAN** được thêm: `ROLE_READ`
- **MEMBER** được thêm: `ROLE_READ`, `CATEGORY_READ`, `AUTHOR_READ`, `PUBLISHER_READ`, `SHELF_READ`
- **READER** được thêm: `ROLE_READ`, `CATEGORY_READ`, `AUTHOR_READ`, `PUBLISHER_READ`, `SHELF_READ`

Những quyền này đã được cập nhật trong `V1__init_schema.sql`. Cần đảm bảo các API tương ứng cho phép/chặn đúng.

## 4. Quy tắc phạm vi dữ liệu (ownership) — bắt buộc kiểm tra ở tầng API

Có permission **chưa đủ**; với MEMBER và READER phải lọc thêm theo người dùng hiện tại:

| Quyền | Vai trò bị giới hạn | Quy tắc |
|---|---|---|
| `USER_READ`, `USER_WRITE` | MEMBER, READER | Chỉ xem/sửa chính tài khoản đang đăng nhập (`users.id` = id trong token). Không được đổi `role_id`, `is_active`, `is_deleted`. |
| `BORROW_READ` | MEMBER | Chỉ phiếu có `borrow_records.member_id` = member của user hiện tại (`members.user_id` = user hiện tại); chi tiết `borrow_details` đi qua `borrow_id`. |
| `FINE_READ` | MEMBER | Chỉ khoản phạt có `fines.borrow_id` thuộc phiếu của chính họ. |
| `FINE_PAYMENT_READ` | MEMBER | Chỉ `fine_payments.member_id` của chính họ. |
| `MEMBER_PAYMENT_READ` | MEMBER | Chỉ `member_payments.member_id` của chính họ. |
| `NOTIFICATION_READ`, `NOTIFICATION_WRITE` | MEMBER, READER | Chỉ thông báo của chính họ (`notifications.member_id`); `WRITE` với họ chỉ để đánh dấu đã đọc, **không** được tạo thông báo cho người khác. |
| `ROLE_READ` | LIBRARIAN, MEMBER, READER | Chỉ trả danh sách vai trò ở mức cần thiết (id, name, description). **Không** trả kèm danh sách permission chi tiết. |
| `*_READ` của sách, thể loại, tác giả, NXB, kệ | MEMBER, READER | Dữ liệu công khai, không cần lọc theo owner. |

Lưu ý: `READER` chưa có bản ghi `members`, nên thực tế không có thông báo nào gắn với họ (`notifications.member_id` NOT NULL). Nếu muốn READER nhận thông báo thì cần thiết kế riêng, ngoài phạm vi file này.

## 5. Yêu cầu cho AI IDE

Hãy thực hiện lần lượt, không bỏ bước:

1. **Quét toàn bộ endpoint** (controller/router) và lập bảng `HTTP method + path → permission đang yêu cầu` trước khi sửa.
2. **Áp quy ước ánh xạ** (nếu endpoint có nghiệp vụ đặc biệt thì ưu tiên mô tả quyền ở mục 2):
   - `GET` (list/detail/search) → `<RESOURCE>_READ`
   - `POST`/`PUT`/`PATCH` → `<RESOURCE>_WRITE`
   - `DELETE` hoặc hành động hủy → `<RESOURCE>_DELETE`
   - Mượn sách, gia hạn, trả sách → `BORROW_WRITE`; hủy phiếu mượn → `BORROW_DELETE`
   - Tạo/sửa khoản phạt → `FINE_WRITE`; ghi nhận nộp phạt → `FINE_PAYMENT_WRITE`; hủy giao dịch nộp phạt → `FINE_PAYMENT_DELETE`
   - Thu phí đăng ký/gia hạn/cấp lại thẻ → `MEMBER_PAYMENT_WRITE`; hủy → `MEMBER_PAYMENT_DELETE`
   - Đánh dấu thông báo đã đọc → `NOTIFICATION_WRITE`
3. **Phân quyền theo permission code, không hardcode theo tên role.** Loại bỏ mọi `hasRole('ADMIN')`, `role == "LIBRARIAN"`… thay bằng kiểm tra authority = mã quyền (ví dụ Spring Security: `@PreAuthorize("hasAuthority('BOOK_WRITE')")`; nếu dự án dùng framework khác thì dùng cơ chế tương đương). Authority của user phải được nạp từ `role_permissions` (qua `users.role_id`), không nhét cứng trong code.
4. **Thêm kiểm tra ownership** theo mục 4 trong service/repository (lọc theo `member_id`/`user_id` của người đăng nhập), trả `403` (hoặc `404` để không lộ sự tồn tại) khi truy cập dữ liệu của người khác.
5. **Endpoint công khai** chỉ gồm đăng nhập/refresh token (và đăng ký nếu có). Mọi endpoint còn lại phải yêu cầu đăng nhập.
6. **Mã trạng thái:** chưa đăng nhập → `401`; đăng nhập nhưng thiếu quyền → `403`.
7. **Đồng bộ frontend/Swagger** (nếu có): ẩn nút/menu theo permission và cập nhật mô tả quyền trong tài liệu API.
8. **Không sửa** `V1__init_schema.sql` nữa, không sửa `permissions`, không đổi tên mã quyền.

## 6. Kiểm thử chấp nhận

Viết test tích hợp cho mỗi vai trò (user demo: `admin` / `librarian` / `user`, mật khẩu `Library@123`; tự tạo thêm user READER để test):

- Với **mỗi** cặp (vai trò × permission) ở mục 2: endpoint đại diện cho permission đó trả `2xx` nếu có ✅, `403` nếu `—`.
- `MEMBER` gọi `GET` phiếu mượn/khoản phạt/thanh toán/thông báo của **người khác** → bị từ chối hoặc không thấy dữ liệu.
- `MEMBER`/`READER` gọi sửa tài khoản người khác, hoặc tự đổi `role_id` → bị từ chối.
- `LIBRARIAN` gọi `USER_WRITE`, `USER_DELETE`, `FINE_PAYMENT_DELETE`, `MEMBER_PAYMENT_DELETE`, `ROLE_WRITE`, `PERMISSION_*`, `SETTING_*` → `403`.
- Không đăng nhập → `401`.

## 7. Triển khai CSDL

- **DB mới / chạy lại từ đầu:** dùng `V1__init_schema.sql` bản mới.
- **DB đã chạy V1 cũ (Flyway):** sửa V1 làm checksum lệch và Flyway sẽ báo lỗi validate. Chọn một trong hai:
  - Dùng migration bổ sung `V2__update_role_permissions.sql` (đã kèm theo) và để nguyên V1 cũ; hoặc
  - Xóa DB dev rồi migrate lại, hoặc chạy `flyway repair` sau khi chắc chắn đã áp thay đổi.
- Sau khi cập nhật quyền, **người dùng đang đăng nhập phải đăng nhập lại** (hoặc làm mới token) nếu authority được nhúng trong JWT.
