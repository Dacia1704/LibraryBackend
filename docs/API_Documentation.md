# 📚 Library Management System — API Documentation

> **Base URL:** `http://localhost:8080`  
> **Auth:** JWT Bearer Token (header `Authorization: Bearer <accessToken>`)  
> **Content-Type:** `application/json` (trừ khi có ghi chú khác)

---

## 📋 Cấu trúc Response chung

```json
{
  "code": 200,
  "message": "Success",
  "data": { }
}
```

### PageResponse (dùng cho các API phân trang)
```json
{
  "code": 200,
  "data": {
    "currentPage": 0,
    "pageSize": 10,
    "totalPages": 5,
    "totalElements": 48,
    "data": [ ]
  }
}
```

---

## 🔐 1. Authentication — `/api/auth`

### 1.1 Đăng nhập
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/auth/login` |
| **Auth** | ❌ Không cần |

**Request Body:**
```json
{
  "email": "admin@library.com",
  "password": "123456"
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `email` | string | ✅ | Email hợp lệ |
| `password` | string | ✅ | Tối thiểu 6 ký tự |

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "email": "admin@library.com",
    "username": "admin",
    "avatar": "https://...",
    "authorities": ["ROLE_ADMIN", "USER_READ", "BOOK_WRITE"],
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "a1b2c3d4-e5f6-..."
  }
}
```

---

### 1.2 Làm mới Token
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/auth/refresh` |
| **Auth** | ❌ Không cần |

**Request Body:**
```json
{
  "refreshToken": "a1b2c3d4-e5f6-..."
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "new-refresh-token-..."
  }
}
```

---

### 1.3 Đăng xuất
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/auth/logout` |
| **Auth** | ✅ Bearer Token |

**Request Body:**
```json
{
  "refreshToken": "a1b2c3d4-e5f6-..."
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": "Đăng xuất thành công"
}
```

---

## 👤 2. Users — `/api/users`

> **Quyền yêu cầu:** `USER_READ`, `USER_WRITE`

### 2.1 Tạo người dùng
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/users` |
| **Auth** | ✅ `USER_WRITE` |

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "password123",
  "fullName": "John Doe",
  "email": "john@example.com",
  "identityNumber": "123456789012",
  "avatar": "https://...",
  "roleId": 2,
  "isActive": true
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `username` | string | ✅ | Max 50 ký tự |
| `password` | string | ✅ | 6–100 ký tự |
| `fullName` | string | ✅ | Max 100 ký tự |
| `email` | string | ✅ | Email hợp lệ, max 100 ký tự |
| `identityNumber` | string | ✅ | Max 12 ký tự |
| `avatar` | string | ❌ | URL avatar |
| `roleId` | long | ✅ | ID của role |
| `isActive` | boolean | ❌ | Mặc định `true` |

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "username": "johndoe",
    "fullName": "John Doe",
    "email": "john@example.com",
    "avatar": "https://...",
    "roleId": 2,
    "roleName": "LIBRARIAN",
    "isActive": true,
    "createdAt": "2026-10-03T10:00:00",
    "member": null
  }
}
```

---

### 2.2 Cập nhật người dùng
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/users/{id}` |
| **Auth** | ✅ `USER_WRITE` |

**Path Variables:**
| Param | Type | Mô tả |
|---|---|---|
| `id` | long | ID người dùng |

**Request Body:** Giống 2.1

---

### 2.3 Xoá người dùng (soft delete)
| | |
|---|---|
| **Method** | `DELETE` |
| **URL** | `/api/users/{id}` |
| **Auth** | ✅ `USER_WRITE` |

**Path Variables:** `id` — string ID người dùng

**Response:** Trả về `UserResponse` của người dùng đã xoá

---

### 2.4 Lấy thông tin người dùng theo ID
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/users/{id}` |
| **Auth** | ✅ `USER_READ` |

---

### 2.5 Lấy danh sách người dùng (phân trang + lọc)
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/users` |
| **Auth** | ✅ `USER_READ` |

**Query Params:**
| Param | Type | Required | Mô tả |
|---|---|---|---|
| `keyword` | string | ❌ | Tìm theo tên/username |
| `email` | string | ❌ | Lọc theo email |
| `roleId` | long | ❌ | Lọc theo role |
| `isMember` | boolean | ❌ | Lọc user có/không có thẻ thư viện |
| `phone` | string | ❌ | Lọc theo số điện thoại |
| `page` | int | ❌ | Trang (mặc định: `0`) |
| `size` | int | ❌ | Kích thước trang (mặc định: `10`) |

**Response:** `PageResponse<UserResponse>`

---

### 2.6 Lấy thông tin người dùng đã xoá
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/users/deleted/{id}` |
| **Auth** | ✅ `USER_READ` |

---

## 👥 3. Members — `/api/members`

> **Quyền yêu cầu:** `MEMBER_READ`, `MEMBER_WRITE`

### 3.1 Tạo thẻ thư viện
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/members` |
| **Auth** | ✅ `MEMBER_WRITE` |

**Request Body:**
```json
{
  "userId": 5,
  "phone": "0901234567",
  "address": "123 Nguyen Van A, TPHCM",
  "amount": 50000.00
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `userId` | long | ✅ | ID user |
| `phone` | string | ❌ | Max 15 ký tự |
| `address` | string | ❌ | Max 255 ký tự |
| `amount` | decimal | ❌ | Phí đăng ký |

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "userId": 5,
    "username": "johndoe",
    "fullName": "John Doe",
    "memberCode": "MBR-2026-001",
    "phone": "0901234567",
    "address": "123 Nguyen Van A, TPHCM",
    "cardExpiry": "2027-10-03",
    "isDeleted": false
  }
}
```

---

### 3.2 Gia hạn thẻ thư viện
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/members/{id}/renew` |
| **Auth** | ✅ `MEMBER_WRITE` |

**Path Variables:** `id` — ID thành viên

**Request Body:**
```json
{
  "amount": 50000.00
}
```

---

### 3.3 Cập nhật thành viên
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/members/{id}` |
| **Auth** | ✅ `MEMBER_WRITE` |

**Request Body:**
```json
{
  "userId": 5,
  "memberCode": "MBR-2026-001",
  "phone": "0901234567",
  "address": "456 Le Loi, TPHCM",
  "cardExpiry": "2028-01-01"
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `userId` | long | ✅ | |
| `memberCode` | string | ✅ | Max 20 ký tự |
| `phone` | string | ❌ | Max 15 ký tự |
| `address` | string | ❌ | Max 255 ký tự |
| `cardExpiry` | date | ✅ | Định dạng `YYYY-MM-DD` |

---

### 3.4 Xoá thành viên
| | |
|---|---|
| **Method** | `DELETE` |
| **URL** | `/api/members/{id}` |
| **Auth** | ✅ `MEMBER_WRITE` |

---

### 3.5 Lấy thông tin thành viên theo ID
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/members/{id}` |
| **Auth** | ✅ `MEMBER_READ` |

---

### 3.6 Lấy phí thành viên tháng
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/members/fee` |
| **Auth** | ✅ `MEMBER_READ` |

**Response:** Trả về giá trị `BigDecimal` (phí thành viên tháng)

---

### 3.7 Lấy tất cả thành viên
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/members` |
| **Auth** | ✅ `BOOK_READ` |

**Response:** `List<MemberResponse>`

---

### 3.8 Lấy thành viên đã xoá theo ID
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/members/deleted/{id}` |
| **Auth** | ✅ Role `BOOK_READ` |

---

## 📖 4. Books — `/api/books`

> **Lưu ý:** Các API tạo/cập nhật dùng `multipart/form-data`

### 4.1 Tạo sách
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/books` |
| **Content-Type** | `multipart/form-data` |
| **Auth** | ✅ `BOOK_WRITE` |

**Form Data:**
| Field | Type | Required | Mô tả |
|---|---|---|---|
| `title` | string | ✅ | Tên sách |
| `isbn` | string | ✅ | Mã ISBN |
| `publishYear` | int | ❌ | Năm xuất bản |
| `quantity` | int | ✅ | Tổng số lượng |
| `available` | int | ✅ | Số lượng còn có thể mượn |
| `price` | decimal | ❌ | Giá sách |
| `cover` | file | ❌ | Ảnh bìa sách |
| `categoryIds` | Set\<long\> | ❌ | Danh sách ID danh mục |
| `authorIds` | Set\<long\> | ❌ | Danh sách ID tác giả |
| `publisherIds` | Set\<long\> | ❌ | Danh sách ID nhà xuất bản |

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "title": "Clean Code",
    "isbn": "978-0132350884",
    "publishYear": 2008,
    "quantity": 10,
    "available": 8,
    "cover": "https://cdn.../cover.jpg",
    "price": 350000.00,
    "categories": [{ "id": 1, "name": "Programming" }],
    "authors": [{ "id": 1, "name": "Robert C. Martin", "bio": "..." }],
    "publishers": [{ "id": 1, "name": "Prentice Hall", "address": "..." }]
  }
}
```

---

### 4.2 Cập nhật sách
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/books/{id}` |
| **Content-Type** | `multipart/form-data` |
| **Auth** | ✅ `BOOK_WRITE` |

**Path Variables:** `id` — ID sách  
**Form Data:** Giống 4.1

---

### 4.3 Khôi phục sách đã xoá
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/books/{id}/restore` |
| **Content-Type** | `multipart/form-data` |
| **Auth** | ✅ `BOOK_WRITE` |

---

### 4.4 Xoá sách (soft delete)
| | |
|---|---|
| **Method** | `DELETE` |
| **URL** | `/api/books/{id}` |
| **Auth** | ✅ `BOOK_WRITE` + Role `ADMIN` |

---

### 4.5 Lấy thông tin sách theo ID
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/books/{id}` |
| **Auth** | ❌ Không cần |

---

### 4.6 Lấy tất cả sách
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/books` |
| **Auth** | ❌ Không cần |

**Response:** `List<BookResponse>`

---

### 4.7 Lấy danh sách sách (phân trang + lọc)
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/books/` |
| **Auth** | ❌ Không cần |

**Query Params:**
| Param | Type | Mô tả |
|---|---|---|
| `keyword` | string | Tìm theo tên |
| `isbn` | string | Lọc theo ISBN |
| `minPublishYear` | int | Năm xuất bản tối thiểu |
| `maxPublishYear` | int | Năm xuất bản tối đa |
| `minQuantity` | int | Số lượng tối thiểu |
| `maxQuantity` | int | Số lượng tối đa |
| `minAvailable` | int | Còn lại tối thiểu |
| `maxAvailable` | int | Còn lại tối đa |
| `categoryIds` | Set\<long\> | Lọc theo danh mục |
| `authorIds` | Set\<long\> | Lọc theo tác giả |
| `publisherIds` | Set\<long\> | Lọc theo NXB |
| `page` | int | Trang (mặc định: `0`) |
| `size` | int | Kích thước (mặc định: `10`) |

**Response:** `PageResponse<BookResponse>`

---

### 4.8 Lấy sách đã xoá theo ID
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/books/deleted/{id}` |
| **Auth** | ✅ `BOOK_WRITE` |

---

## 📁 5. Categories — `/api/categories`

> **Quyền yêu cầu:** `CATEGORY_MANAGE`

### 5.1 Tạo danh mục
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/categories` |
| **Auth** | ✅ `CATEGORY_MANAGE` |

**Request Body:**
```json
{ "name": "Khoa học máy tính" }
```

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": { "id": 1, "name": "Khoa học máy tính" }
}
```

---

### 5.2 Cập nhật danh mục
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/categories/{id}` |
| **Auth** | ✅ `CATEGORY_MANAGE` |

**Request Body:** `{ "name": "Tên mới" }`

---

### 5.3 Khôi phục danh mục đã xoá
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/categories/{id}/restore` |
| **Auth** | ✅ `CATEGORY_MANAGE` |

**Request Body:** `{ "name": "Tên danh mục" }`

---

### 5.4 Xoá danh mục
| | |
|---|---|
| **Method** | `DELETE` |
| **URL** | `/api/categories/{id}` |
| **Auth** | ✅ `CATEGORY_MANAGE` |

---

### 5.5 Lấy tất cả danh mục
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/categories` |
| **Auth** | ❌ Không cần |

**Response:** `List<CategoryResponse>`

---

## ✍️ 6. Authors — `/api/authors`

### 6.1 Tạo tác giả
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/authors` |
| **Auth** | ✅ `AUTHOR_MANAGE` |

**Request Body:**
```json
{
  "name": "Robert C. Martin",
  "bio": "Tác giả nổi tiếng trong lĩnh vực phần mềm..."
}
```

| Field | Type | Required |
|---|---|---|
| `name` | string | ✅ |
| `bio` | string | ✅ |

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "Robert C. Martin",
    "bio": "Tác giả nổi tiếng..."
  }
}
```

---

### 6.2 Cập nhật tác giả
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/authors/{id}` |
| **Auth** | ✅ `AUTHOR_MANAGE` |

**Request Body:** Giống 6.1

---

### 6.3 Khôi phục tác giả đã xoá
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/authors/{id}/restore` |
| **Auth** | ✅ `AUTHOR_MANAGE` |

---

### 6.4 Xoá tác giả
| | |
|---|---|
| **Method** | `DELETE` |
| **URL** | `/api/authors/{id}` |
| **Auth** | ✅ `AUTHOR_MANAGE` |

---

### 6.5 Lấy tất cả tác giả
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/authors/all` |
| **Auth** | ❌ Không cần |

**Response:** `List<AuthorResponse>`

---

### 6.6 Lấy danh sách tác giả (phân trang)
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/authors/` |
| **Auth** | ❌ Không cần |

**Query Params:** `keyword`, `page` (mặc định `0`), `size` (mặc định `10`)

**Response:** `PageResponse<AuthorResponse>`

---

## 🏢 7. Publishers — `/api/publishers`

### 7.1 Tạo nhà xuất bản
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/publishers` |
| **Auth** | ✅ `PUBLISHER_MANAGE` |

**Request Body:**
```json
{
  "name": "NXB Kim Đồng",
  "address": "55 Quang Trung, Hà Nội"
}
```

| Field | Type | Required |
|---|---|---|
| `name` | string | ✅ |
| `address` | string | ✅ |

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "NXB Kim Đồng",
    "address": "55 Quang Trung, Hà Nội"
  }
}
```

---

### 7.2 Cập nhật nhà xuất bản
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/publishers/{id}` |
| **Auth** | ✅ `PUBLISHER_MANAGE` |

**Request Body:** Giống 7.1

---

### 7.3 Khôi phục NXB đã xoá
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/publishers/{id}/restore` |
| **Auth** | ✅ `PUBLISHER_MANAGE` |

---

### 7.4 Xoá nhà xuất bản
| | |
|---|---|
| **Method** | `DELETE` |
| **URL** | `/api/publishers/{id}` |
| **Auth** | ✅ `PUBLISHER_MANAGE` |

---

### 7.5 Lấy tất cả nhà xuất bản
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/publishers/all` |
| **Auth** | ❌ Không cần |

**Query Params:** `keyword` (string, tuỳ chọn)

**Response:** `List<PublisherResponse>`

---

### 7.6 Lấy danh sách NXB (phân trang)
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/publishers` |
| **Auth** | ❌ Không cần |

**Query Params:** `keyword`, `page` (mặc định `0`), `size` (mặc định `10`)

**Response:** `PageResponse<PublisherResponse>`

---

## 📋 8. Borrow Records — `/api/borrow-records`

### 8.1 Tạo phiếu mượn
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/borrow-records` |
| **Auth** | ✅ `BORROW_WRITE` |

**Request Body:**
```json
{
  "memberId": "1",
  "librarianId": "2",
  "borrowDate": "2026-10-03",
  "dayBorrow": 14,
  "status": "BORROWING",
  "note": "Ghi chú thêm",
  "bookIds": ["1", "2", "3"]
}
```

| Field | Type | Required | Mô tả |
|---|---|---|---|
| `memberId` | string | ✅ | ID thành viên |
| `librarianId` | string | ✅ | ID thủ thư |
| `borrowDate` | date | ✅ | Ngày mượn (YYYY-MM-DD) |
| `dayBorrow` | long | ✅ | Số ngày mượn |
| `status` | enum | ❌ | `BORROWING` \| `RETURNED` \| `OVERDUE` |
| `note` | string | ❌ | Ghi chú |
| `bookIds` | List\<string\> | ✅ | Danh sách ID sách |

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "member": {},
    "librarian": {},
    "borrowDate": "2026-10-03",
    "dueDate": "2026-10-17",
    "status": "BORROWING",
    "note": "Ghi chú thêm",
    "borrowDetails": [
      {
        "id": 1,
        "bookResponse": {},
        "returnDate": null,
        "fineAmount": null
      }
    ]
  }
}
```

---

### 8.2 Cập nhật phiếu mượn
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/borrow-records/{id}` |
| **Auth** | ✅ `BORROW_WRITE` |

**Request Body:** Giống 8.1

---

### 8.3 Xoá phiếu mượn
| | |
|---|---|
| **Method** | `DELETE` |
| **URL** | `/api/borrow-records/{id}` |
| **Auth** | ✅ `BORROW_WRITE` + Role `ADMIN` |

---

### 8.4 Lấy phiếu mượn theo ID
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/borrow-records/{id}` |
| **Auth** | ✅ Bearer Token |

---

### 8.5 Lấy tất cả phiếu mượn
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/borrow-records` |
| **Auth** | ✅ Bearer Token |

---

### 8.6 Lấy phiếu mượn (phân trang + lọc)
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/borrow-records/pagination` |
| **Auth** | ✅ Bearer Token |

**Query Params:**
| Param | Type | Mô tả |
|---|---|---|
| `memberId` | long | Lọc theo thành viên |
| `borrowDate` | date | Lọc theo ngày mượn |
| `status` | enum | `BORROWING` \| `RETURNED` \| `OVERDUE` |
| `bookId` | long | Lọc theo sách |
| `page` | int | Trang (mặc định: `0`) |
| `size` | int | Kích thước (mặc định: `10`) |

**Response:** `PageResponse<BorrowRecordResponse>`

---

## 📦 9. Borrow Details — `/api/borrow-details`

### 9.1 Lấy chi tiết mượn theo ID
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/borrow-details/{id}` |
| **Auth** | ✅ Bearer Token |

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "bookResponse": {},
    "returnDate": "2026-10-17",
    "fineAmount": 0.00
  }
}
```

---

### 9.2 Lấy tất cả chi tiết mượn
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/borrow-details` |
| **Auth** | ✅ Bearer Token |

**Response:** `List<BorrowDetailResponse>`

---

### 9.3 Trả sách
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/borrow-details/{id}/return` |
| **Content-Type** | `multipart/form-data` |
| **Auth** | ✅ `BORROW_WRITE` |

**Path Variables:** `id` — ID chi tiết mượn (string)

**Form Data:**

| Field | Type | Mô tả |
|---|---|---|
| `fineRequests[i].reason` | enum | `OVERDUE` \| `LOST` \| `DAMAGED_LIGHT` \| `DAMAGED_HEAVY_REPAIRABLE` \| `DAMAGED_HEAVY_IRREPARABLE` |
| `fineRequests[i].note` | string | Ghi chú phạt |
| `fineRequests[i].attachment` | file | Ảnh minh chứng |

---

## 💰 10. Fines — `/api/fines`

### 10.1 Lấy danh sách phạt (phân trang)
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/fines` |
| **Auth** | ✅ `FINE_READ` |

**Query Params:** `memberId` (long, tuỳ chọn), `page`, `size`

**Response:**
```json
{
  "code": 200,
  "data": {
    "currentPage": 0,
    "pageSize": 10,
    "totalPages": 2,
    "totalElements": 15,
    "data": [
      {
        "id": 1,
        "borrowId": 1,
        "borrowDetailId": 2,
        "amount": 50000.00,
        "reason": "OVERDUE",
        "overdueDays": 5,
        "note": "Trả trễ",
        "createdAt": "2026-10-03T10:00:00",
        "isDeleted": false
      }
    ]
  }
}
```

---

### 10.2 Lấy phạt của tôi
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/fines/me` |
| **Auth** | ✅ Bearer Token |

**Query Params:** `page`, `size`

---

## 💳 11. Fine Payments — `/api/fine-payments`

### 11.1 Lấy danh sách thanh toán phạt (phân trang)
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/fine-payments` |
| **Auth** | ✅ `FINE_PAYMENT_READ` |

**Query Params:** `memberId` (long, tuỳ chọn), `page`, `size`

**Response:**
```json
{
  "code": 200,
  "data": {
    "currentPage": 0,
    "pageSize": 10,
    "totalPages": 1,
    "totalElements": 3,
    "data": [
      {
        "id": 1,
        "memberId": 5,
        "amount": 150000.00,
        "paidAt": "2026-10-03T14:30:00",
        "receivedBy": 2,
        "receivedByUsername": "librarian01",
        "note": "Thanh toán phạt trễ",
        "isDeleted": false
      }
    ]
  }
}
```

---

### 11.2 Lấy thanh toán phạt của tôi
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/fine-payments/me` |
| **Auth** | ✅ Bearer Token |

**Query Params:** `page`, `size`

---

### 11.3 Tạo thanh toán phạt
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/fine-payments` |
| **Auth** | ✅ `FINE_PAYMENT_WRITE` |

**Request Body:**
```json
{
  "memberId": 5,
  "amount": 150000.00,
  "note": "Thanh toán phạt tháng 10"
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `memberId` | long | ✅ | |
| `amount` | decimal | ✅ | Tối thiểu `0.01` |
| `note` | string | ❌ | Max 255 ký tự |

---

## 💵 12. Member Payments — `/api/member-payments`

### 12.1 Lấy lịch sử đóng phí thành viên
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/member-payments/{memberId}` |
| **Auth** | ✅ `MEMBER_PAYMENT_READ` |

**Path Variables:** `memberId` — ID thành viên (long)

**Query Params:** `month` (int, tuỳ chọn), `page`, `size`

**Response:**
```json
{
  "code": 200,
  "data": {
    "currentPage": 0,
    "pageSize": 10,
    "totalPages": 1,
    "totalElements": 2,
    "data": [
      {
        "id": 1,
        "memberId": 5,
        "memberCode": "MBR-2026-001",
        "memberName": "John Doe",
        "amount": 50000.00,
        "paymentType": "REGISTER",
        "paidAt": "2026-10-03T09:00:00",
        "receivedBy": 2,
        "receivedByUsername": "admin",
        "note": null,
        "isDeleted": false
      }
    ]
  }
}
```

---

### 12.2 Lấy lịch sử đóng phí của tôi
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/member-payments/me` |
| **Auth** | ✅ `MEMBER_PAYMENT_READ` |

**Response:** `List<MemberPaymentResponse>`

---

## 🔑 13. Roles — `/api/roles`

### 13.1 Tạo role
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/roles` |
| **Auth** | ✅ `ROLE_MANAGE` |

**Request Body:**
```json
{
  "name": "LIBRARIAN",
  "description": "Thủ thư quản lý sách và mượn trả",
  "permissionIds": [1, 2, 3, 4]
}
```

| Field | Type | Required |
|---|---|---|
| `name` | string | ✅ |
| `description` | string | ✅ |
| `permissionIds` | List\<long\> | ✅ |

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "LIBRARIAN",
    "description": "Thủ thư quản lý sách và mượn trả",
    "permissionIds": [1, 2, 3, 4]
  }
}
```

---

### 13.2 Cập nhật role
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/roles/{id}` |
| **Auth** | ✅ `ROLE_MANAGE` |

**Request Body:** Giống 13.1

---

### 13.3 Xoá role
| | |
|---|---|
| **Method** | `DELETE` |
| **URL** | `/api/roles/{id}` |
| **Auth** | ✅ `ROLE_MANAGE` |

---

### 13.4 Lấy tất cả roles
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/roles` |
| **Auth** | ✅ `ROLE_MANAGE` |

**Response:** `List<RoleResponse>`

---

## 🛡️ 14. Permissions — `/api/permissions`

### 14.1 Tạo quyền
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/permissions` |
| **Auth** | ✅ `PERMISSION_MANAGE` |

**Request Body:**
```json
{
  "code": "BOOK_WRITE",
  "description": "Quyền thêm/sửa/xoá sách"
}
```

| Field | Type | Required |
|---|---|---|
| `code` | string | ✅ |
| `description` | string | ✅ |

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "code": "BOOK_WRITE",
    "description": "Quyền thêm/sửa/xoá sách"
  }
}
```

---

### 14.2 Cập nhật quyền
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/permissions/{id}` |
| **Auth** | ✅ `PERMISSION_MANAGE` |

---

### 14.3 Xoá quyền
| | |
|---|---|
| **Method** | `DELETE` |
| **URL** | `/api/permissions/{id}` |
| **Auth** | ✅ `PERMISSION_MANAGE` |

---

### 14.4 Lấy tất cả quyền
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/permissions` |
| **Auth** | ✅ `PERMISSION_MANAGE` |

**Response:** `List<PermissionResponse>`

---

## ⚙️ 15. Settings — `/api/settings`

### 15.1 Tạo cấu hình
| | |
|---|---|
| **Method** | `POST` |
| **URL** | `/api/settings` |
| **Auth** | ✅ `SETTING_MANAGE` |

**Request Body:**
```json
{
  "settingKey": "FINE_OVERDUE_PER_DAY",
  "settingValue": "5000",
  "description": "Tiền phạt quá hạn mỗi ngày (VND)"
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `settingKey` | string | ✅ | Max 100 ký tự |
| `settingValue` | string | ✅ | Max 500 ký tự |
| `description` | string | ❌ | Max 255 ký tự |

**Response:**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "settingKey": "FINE_OVERDUE_PER_DAY",
    "settingValue": "5000",
    "description": "Tiền phạt quá hạn mỗi ngày (VND)",
    "updatedAt": "2026-10-03T10:00:00",
    "isDeleted": false
  }
}
```

---

### 15.2 Cập nhật cấu hình
| | |
|---|---|
| **Method** | `PUT` |
| **URL** | `/api/settings/{id}` |
| **Auth** | ✅ `SETTING_MANAGE` |

**Request Body:** Giống 15.1

---

### 15.3 Xoá cấu hình
| | |
|---|---|
| **Method** | `DELETE` |
| **URL** | `/api/settings/{id}` |
| **Auth** | ✅ `SETTING_MANAGE` |

---

### 15.4 Lấy cấu hình theo ID
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/settings/{id}` |
| **Auth** | ✅ `SETTING_MANAGE` |

---

### 15.5 Lấy tất cả cấu hình
| | |
|---|---|
| **Method** | `GET` |
| **URL** | `/api/settings` |
| **Auth** | ✅ `SETTING_MANAGE` |

**Response:** `List<SettingResponse>`

---

## 📊 Bảng tóm tắt các Quyền (Authorities)

| Permission Code | Mô tả |
|---|---|
| `USER_READ` | Xem danh sách người dùng |
| `USER_WRITE` | Tạo/sửa/xoá người dùng |
| `BOOK_READ` | Xem danh sách sách |
| `BOOK_WRITE` | Tạo/sửa/xoá sách |
| `MEMBER_READ` | Xem thành viên |
| `MEMBER_WRITE` | Tạo/sửa/xoá thành viên |
| `MEMBER_PAYMENT_READ` | Xem lịch sử đóng phí |
| `BORROW_WRITE` | Quản lý mượn/trả sách |
| `FINE_READ` | Xem danh sách phạt |
| `FINE_PAYMENT_READ` | Xem lịch sử thanh toán phạt |
| `FINE_PAYMENT_WRITE` | Tạo thanh toán phạt |
| `CATEGORY_MANAGE` | Quản lý danh mục |
| `AUTHOR_MANAGE` | Quản lý tác giả |
| `PUBLISHER_MANAGE` | Quản lý nhà xuất bản |
| `ROLE_MANAGE` | Quản lý roles |
| `PERMISSION_MANAGE` | Quản lý quyền |
| `SETTING_MANAGE` | Quản lý cấu hình hệ thống |

---

## 📅 Các Setting Key hệ thống

| Key | Mô tả |
|---|---|
| `FINE_OVERDUE_PER_DAY` | Tiền phạt quá hạn mỗi ngày |
| `FINE_LOST_RATE` | Tỷ lệ phạt khi mất sách |
| `FINE_DAMAGED_LIGHT_RATE` | Tỷ lệ phạt hư nhẹ |
| `FINE_DAMAGED_HEAVY_REPAIRABLE_RATE` | Tỷ lệ phạt hư nặng, có thể phục hồi |
| `FINE_DAMAGED_HEAVY_IRREPARABLE_RATE` | Tỷ lệ phạt hư nặng, không thể phục hồi |
| `MAX_BORROW_DAYS` | Số ngày mượn tối đa |
| `MAX_BOOKS_BORROW` | Số sách mượn tối đa một lần |
| `MAX_FINE_BEFORE_BLOCK` | Số tiền phạt tối đa trước khi bị khoá |
| `DUE_REMINDER_DAYS` | Số ngày trước hạn gửi nhắc nhở |
| `MEMBER_PAYMENT_MONTH` | Phí thành viên hàng tháng |
