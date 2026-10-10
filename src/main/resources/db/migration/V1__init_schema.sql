-- =====================================================================
-- V1__init_schema.sql  (SQL Server)  -  gop V1..V4 + ke sach + thuoc tinh sach
-- Quy uoc:
--  * CHI XOA MEM: moi bang co is_deleted (BIT, mac dinh 0). Khong dung DELETE that.
--  * no_accent: chuoi khong dau, ung dung tu tinh va luu de tim kiem.
--  * UNIQUE tren cot nghiep vu dung filtered unique index (WHERE is_deleted = 0)
--    de ban ghi da xoa mem khong chan viec tao lai gia tri trung.
--  * Chuoi co tieng Viet dung NVARCHAR. Anh (avatar, cover) luu base64 trong VARCHAR(MAX).
--  * SQL Server khong co ENUM: dung VARCHAR + CHECK constraint.
-- =====================================================================

-- =====================================================================
-- 1. AUTHENTICATION / AUTHORIZATION
-- =====================================================================
CREATE TABLE roles (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    name        VARCHAR(30)   NOT NULL,
    description NVARCHAR(255) NULL,
    is_deleted  BIT NOT NULL CONSTRAINT DF_roles_deleted DEFAULT 0
);
CREATE UNIQUE INDEX UX_roles_name ON roles(name) WHERE is_deleted = 0;

CREATE TABLE permissions (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    code        VARCHAR(50)   NOT NULL,
    description NVARCHAR(255) NULL,
    is_deleted  BIT NOT NULL CONSTRAINT DF_permissions_deleted DEFAULT 0
);
CREATE UNIQUE INDEX UX_permissions_code ON permissions(code) WHERE is_deleted = 0;

CREATE TABLE role_permissions (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    CONSTRAINT PK_role_permissions PRIMARY KEY (role_id, permission_id),
    CONSTRAINT FK_rp_role       FOREIGN KEY (role_id)       REFERENCES roles(id),
    CONSTRAINT FK_rp_permission FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

CREATE TABLE users (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    username        VARCHAR(50)   NOT NULL,
    password_hash   VARCHAR(255)  NOT NULL,
    full_name       NVARCHAR(100) NOT NULL,
    no_accent       VARCHAR(100)  NOT NULL,      -- full_name khong dau, de tim kiem
    email           VARCHAR(100)  NOT NULL,
    avatar          VARCHAR(MAX)  NULL,          -- base64 (data URI), vd: data:image/png;base64,...
    role_id         BIGINT        NOT NULL,
    is_active       BIT           NOT NULL CONSTRAINT DF_users_active  DEFAULT 1,
    failed_attempts INT           NOT NULL CONSTRAINT DF_users_failed  DEFAULT 0,
    created_at      DATETIME2     NOT NULL CONSTRAINT DF_users_created DEFAULT SYSDATETIME(),
    is_deleted      BIT           NOT NULL CONSTRAINT DF_users_deleted DEFAULT 0,
    CONSTRAINT FK_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);
CREATE UNIQUE INDEX UX_users_username ON users(username) WHERE is_deleted = 0;
CREATE UNIQUE INDEX UX_users_email    ON users(email)    WHERE is_deleted = 0;
CREATE INDEX IX_users_role_id   ON users(role_id);
CREATE INDEX IX_users_no_accent ON users(no_accent);

CREATE TABLE refresh_tokens (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    token      VARCHAR(255) NOT NULL,
    expires_at DATETIME2    NOT NULL,
    revoked    BIT          NOT NULL CONSTRAINT DF_rt_revoked DEFAULT 0,
    CONSTRAINT UQ_refresh_tokens_token UNIQUE (token),
    CONSTRAINT FK_rt_user FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE INDEX IX_refresh_tokens_user_id ON refresh_tokens(user_id);

-- =====================================================================
-- 2. THANH VIEN
-- =====================================================================
CREATE TABLE members (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id         BIGINT        NOT NULL,
    member_code     VARCHAR(20)   NOT NULL,
    phone           VARCHAR(15)   NULL,
    identity_number VARCHAR(12)   NOT NULL,
    address         NVARCHAR(255) NULL,
    card_expiry     DATE          NOT NULL,   -- PENDING: han du kien, tinh lai khi cap the
    card_status     VARCHAR(10)   NOT NULL CONSTRAINT DF_members_card_status DEFAULT 'ISSUED',
                                              -- ISSUED = da cap the, PENDING = doi cap the
    is_deleted      BIT           NOT NULL CONSTRAINT DF_members_deleted DEFAULT 0,
    created_at      DATETIME2     NOT NULL CONSTRAINT DF_members_created_at DEFAULT SYSDATETIME(),
    CONSTRAINT FK_members_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT CK_members_card_status CHECK (card_status IN ('ISSUED','PENDING'))
);
CREATE UNIQUE INDEX UX_members_user_id     ON members(user_id)     WHERE is_deleted = 0;
CREATE UNIQUE INDEX UX_members_member_code ON members(member_code) WHERE is_deleted = 0;

-- Phi dang ky / gia han the thanh vien
CREATE TABLE member_payments (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    member_id    BIGINT        NOT NULL,
    amount       DECIMAL(10,2) NOT NULL,                    -- so tien da nop
    payment_type VARCHAR(10)   NOT NULL CONSTRAINT DF_mp_type DEFAULT 'REGISTER',   -- REGISTER (dang ky) / RENEW (gia han) / REISSUE (cap lai the)
    paid_at      DATETIME2     NOT NULL CONSTRAINT DF_mp_paid DEFAULT SYSDATETIME(),
    received_by  BIGINT        NOT NULL,                    -- users.id cua thu thu thu tien
    note         NVARCHAR(255) NULL,
    is_deleted   BIT           NOT NULL CONSTRAINT DF_mp_deleted DEFAULT 0,
    CONSTRAINT FK_mp_member   FOREIGN KEY (member_id)   REFERENCES members(id),
    CONSTRAINT FK_mp_receiver FOREIGN KEY (received_by) REFERENCES users(id),
    CONSTRAINT CK_mp_amount   CHECK (amount >= 0),
    CONSTRAINT CK_mp_type     CHECK (payment_type IN ('REGISTER','RENEW','REISSUE'))
);
CREATE INDEX IX_mp_member_id   ON member_payments(member_id);
CREATE INDEX IX_mp_received_by ON member_payments(received_by);
CREATE INDEX IX_mp_paid_at     ON member_payments(paid_at);

-- =====================================================================
-- 3. DANH MUC SACH
-- =====================================================================
CREATE TABLE categories (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    name       NVARCHAR(100) NOT NULL,
    no_accent  VARCHAR(100)  NOT NULL,
    is_deleted BIT           NOT NULL CONSTRAINT DF_categories_deleted DEFAULT 0
);
CREATE UNIQUE INDEX UX_categories_name ON categories(name) WHERE is_deleted = 0;
CREATE INDEX IX_categories_no_accent ON categories(no_accent);

CREATE TABLE authors (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    name       NVARCHAR(100) NOT NULL,
    no_accent  VARCHAR(100)  NOT NULL,
    bio        NVARCHAR(500) NULL,
    is_deleted BIT           NOT NULL CONSTRAINT DF_authors_deleted DEFAULT 0
);
CREATE INDEX IX_authors_no_accent ON authors(no_accent);

CREATE TABLE publishers (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    name       NVARCHAR(100) NOT NULL,
    no_accent  VARCHAR(100)  NOT NULL,
    address    NVARCHAR(255) NULL,
    is_deleted BIT           NOT NULL CONSTRAINT DF_publishers_deleted DEFAULT 0
);
CREATE UNIQUE INDEX UX_publishers_name ON publishers(name) WHERE is_deleted = 0;
CREATE INDEX IX_publishers_no_accent ON publishers(no_accent);

-- Ke sach (MOI)
CREATE TABLE shelves (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    code       VARCHAR(20)   NOT NULL,                 -- ma ke, vd: K-A01
    name       NVARCHAR(100) NOT NULL,                 -- ten ke
    location   NVARCHAR(255) NULL,                      -- vi tri, vd: Tang 2 - Khu A
    is_deleted BIT           NOT NULL CONSTRAINT DF_shelves_deleted DEFAULT 0
);
CREATE UNIQUE INDEX UX_shelves_code ON shelves(code) WHERE is_deleted = 0;

CREATE TABLE books (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    book_code    VARCHAR(30)    NOT NULL,                -- ma sach (MOI)
    title        NVARCHAR(255)  NOT NULL,
    no_accent    VARCHAR(255)   NOT NULL,                -- title khong dau, de tim kiem
    isbn         VARCHAR(20)    NOT NULL,
    publish_year INT            NULL,
    price        DECIMAL(15,2)  NOT NULL,
    quantity     INT            NOT NULL CONSTRAINT DF_books_quantity  DEFAULT 0,
    available    INT            NOT NULL CONSTRAINT DF_books_available DEFAULT 0,
    width        DECIMAL(6,2)   NULL,                    -- chieu rong (cm) (MOI)
    height       DECIMAL(6,2)   NULL,                    -- chieu cao (cm)  (MOI)
    pages        INT            NULL,                    -- so trang        (MOI)
    synopsis     NVARCHAR(MAX)  NULL,                    -- tom tat noi dung (MOI)
    language     VARCHAR(5)     NOT NULL CONSTRAINT DF_books_language DEFAULT 'VI',  -- (MOI)
    shelf_id     BIGINT         NULL,                    -- sach dang nam o ke nao (MOI)
    cover        VARCHAR(MAX)   NULL,                    -- base64 (data URI) anh bia
    is_deleted   BIT            NOT NULL CONSTRAINT DF_books_deleted DEFAULT 0,
    CONSTRAINT FK_books_shelf     FOREIGN KEY (shelf_id) REFERENCES shelves(id),
    CONSTRAINT CK_books_quantity  CHECK (quantity >= 0),
    CONSTRAINT CK_books_available CHECK (available >= 0 AND available <= quantity),
    CONSTRAINT CK_books_width     CHECK (width  IS NULL OR width  > 0),
    CONSTRAINT CK_books_height    CHECK (height IS NULL OR height > 0),
    CONSTRAINT CK_books_pages     CHECK (pages  IS NULL OR pages  > 0),
    -- Enum ngon ngu: VI=Viet, EN=Anh, RU=Nga, ZH=Trung, KO=Han, JA=Nhat
    CONSTRAINT CK_books_language  CHECK (language IN ('VI','EN','RU','ZH','KO','JA'))
);
CREATE UNIQUE INDEX UX_books_isbn      ON books(isbn)      WHERE is_deleted = 0;
CREATE UNIQUE INDEX UX_books_book_code ON books(book_code) WHERE is_deleted = 0;
CREATE INDEX IX_books_no_accent ON books(no_accent);
CREATE INDEX IX_books_shelf_id  ON books(shelf_id);

-- ---------- BANG TRUNG GIAN (quan he N-N) ----------
CREATE TABLE book_categories (
    book_id     BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    CONSTRAINT PK_book_categories PRIMARY KEY (book_id, category_id),
    CONSTRAINT FK_bc_book     FOREIGN KEY (book_id)     REFERENCES books(id),
    CONSTRAINT FK_bc_category FOREIGN KEY (category_id) REFERENCES categories(id)
);
CREATE INDEX IX_bc_category_id ON book_categories(category_id);

CREATE TABLE book_authors (
    book_id    BIGINT NOT NULL,
    author_id  BIGINT NOT NULL,
    CONSTRAINT PK_book_authors PRIMARY KEY (book_id, author_id),
    CONSTRAINT FK_ba_book   FOREIGN KEY (book_id)   REFERENCES books(id),
    CONSTRAINT FK_ba_author FOREIGN KEY (author_id) REFERENCES authors(id)
);
CREATE INDEX IX_ba_author_id ON book_authors(author_id);

CREATE TABLE book_publishers (
    book_id      BIGINT NOT NULL,
    publisher_id BIGINT NOT NULL,
    CONSTRAINT PK_book_publishers PRIMARY KEY (book_id, publisher_id),
    CONSTRAINT FK_bp_book      FOREIGN KEY (book_id)      REFERENCES books(id),
    CONSTRAINT FK_bp_publisher FOREIGN KEY (publisher_id) REFERENCES publishers(id)
);
CREATE INDEX IX_bp_publisher_id ON book_publishers(publisher_id);

-- =====================================================================
-- 4. MUON / TRA
-- =====================================================================
CREATE TABLE borrow_records (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    member_id    BIGINT        NOT NULL,
    librarian_id BIGINT        NOT NULL,        -- users.id cua thu thu lap phieu
    borrow_date  DATE          NOT NULL,
    due_date     DATE          NOT NULL,
    note         NVARCHAR(255) NULL,
    is_deleted   BIT           NOT NULL CONSTRAINT DF_br_deleted DEFAULT 0,
    CONSTRAINT FK_br_member    FOREIGN KEY (member_id)    REFERENCES members(id),
    CONSTRAINT FK_br_librarian FOREIGN KEY (librarian_id) REFERENCES users(id),
    CONSTRAINT CK_br_dates     CHECK (due_date >= borrow_date)
);
CREATE INDEX IX_br_member_id    ON borrow_records(member_id);
CREATE INDEX IX_br_librarian_id ON borrow_records(librarian_id);
CREATE INDEX IX_br_due_date     ON borrow_records(due_date);

CREATE TABLE borrow_details (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    borrow_id   BIGINT        NOT NULL,
    book_id     BIGINT        NOT NULL,
    return_date DATE          NULL,
    status      VARCHAR(20)   NOT NULL CONSTRAINT DF_bd_status  DEFAULT 'BORROWING',  -- trang thai TUNG cuon: BORROWING / RETURNED / OVERDUE
    fine_amount DECIMAL(10,2) NOT NULL CONSTRAINT DF_bd_fine    DEFAULT 0,
    is_deleted  BIT           NOT NULL CONSTRAINT DF_bd_deleted DEFAULT 0,
    CONSTRAINT FK_bd_borrow FOREIGN KEY (borrow_id) REFERENCES borrow_records(id),
    CONSTRAINT FK_bd_book   FOREIGN KEY (book_id)   REFERENCES books(id),
    CONSTRAINT CK_bd_fine   CHECK (fine_amount >= 0),
    CONSTRAINT CK_bd_status CHECK (status IN ('BORROWING','RETURNED','OVERDUE'))
);
CREATE INDEX IX_bd_borrow_id ON borrow_details(borrow_id);
CREATE INDEX IX_bd_book_id   ON borrow_details(book_id);
CREATE INDEX IX_bd_status    ON borrow_details(status);

-- =====================================================================
-- 5. TIEN PHAT / THANH TOAN PHAT
-- =====================================================================
CREATE TABLE fines (
    id               BIGINT IDENTITY(1,1) PRIMARY KEY,
    borrow_id        BIGINT        NOT NULL,                 -- phat den tu phieu muon nao
    borrow_detail_id BIGINT        NULL,                     -- cuon sach nao (NULL neu phat chung ca phieu)
    amount           DECIMAL(10,2) NOT NULL,
    reason           VARCHAR(50)   NOT NULL,                 -- OVERDUE / LOST / DAMAGED
    overdue_days     INT           NULL,                     -- so ngay tre (chi dung khi reason = OVERDUE)
    note             NVARCHAR(255) NULL,
    created_at       DATETIME2     NOT NULL CONSTRAINT DF_fines_created DEFAULT SYSDATETIME(),
    is_deleted       BIT           NOT NULL CONSTRAINT DF_fines_deleted DEFAULT 0,
    attachment       VARCHAR(MAX)  NULL,
    CONSTRAINT FK_fines_borrow FOREIGN KEY (borrow_id)        REFERENCES borrow_records(id),
    CONSTRAINT FK_fines_detail FOREIGN KEY (borrow_detail_id) REFERENCES borrow_details(id),
    CONSTRAINT CK_fines_amount CHECK (amount > 0),
    CONSTRAINT CK_fines_reason CHECK (reason IN ('OVERDUE','LOST','DAMAGED', 'DAMAGED_LIGHT', 'DAMAGED_HEAVY_REPAIRABLE', 'DAMAGED_HEAVY_IRREPARABLE'))
);
CREATE INDEX IX_fines_borrow_id ON fines(borrow_id);
CREATE INDEX IX_fines_detail_id ON fines(borrow_detail_id);

-- Moi dong la 1 lan thanh vien nop tien; tru vao tong tien phat cua thanh vien
CREATE TABLE fine_payments (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    member_id   BIGINT        NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    paid_at     DATETIME2     NOT NULL CONSTRAINT DF_fp_paid    DEFAULT SYSDATETIME(),
    received_by BIGINT        NOT NULL,                     -- users.id cua thu thu thu tien
    note        NVARCHAR(255) NULL,
    is_deleted  BIT           NOT NULL CONSTRAINT DF_fp_deleted DEFAULT 0,
    CONSTRAINT FK_fp_member   FOREIGN KEY (member_id)   REFERENCES members(id),
    CONSTRAINT FK_fp_receiver FOREIGN KEY (received_by) REFERENCES users(id),
    CONSTRAINT CK_fp_amount   CHECK (amount > 0)
);
CREATE INDEX IX_fp_member_id   ON fine_payments(member_id);
CREATE INDEX IX_fp_received_by ON fine_payments(received_by);

-- =====================================================================
-- 6. THONG BAO (RabbitMQ consumer ghi vao)
-- =====================================================================
CREATE TABLE notifications (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    member_id  BIGINT        NOT NULL,
    content    NVARCHAR(500) NOT NULL,
    is_read    BIT           NOT NULL CONSTRAINT DF_nt_read    DEFAULT 0,
    created_at DATETIME2     NOT NULL CONSTRAINT DF_nt_created DEFAULT SYSDATETIME(),
    is_deleted BIT           NOT NULL CONSTRAINT DF_nt_deleted DEFAULT 0,
    CONSTRAINT FK_nt_member FOREIGN KEY (member_id) REFERENCES members(id)
);
CREATE INDEX IX_nt_member_read ON notifications(member_id, is_read);

-- =====================================================================
-- 7. SETTINGS (KEY - VALUE)
-- =====================================================================
CREATE TABLE settings (
    id            BIGINT IDENTITY(1,1) PRIMARY KEY,
    setting_key   VARCHAR(100)  NOT NULL,
    setting_value NVARCHAR(500) NOT NULL,
    description   NVARCHAR(255) NULL,
    updated_at    DATETIME2     NOT NULL CONSTRAINT DF_settings_updated DEFAULT SYSDATETIME(),
    is_deleted    BIT           NOT NULL CONSTRAINT DF_settings_deleted DEFAULT 0
);
CREATE UNIQUE INDEX UX_settings_key ON settings(setting_key) WHERE is_deleted = 0;

-- =====================================================================
-- DU LIEU KHOI TAO
-- =====================================================================

-- ---------- SETTINGS ----------
INSERT INTO settings (setting_key, setting_value, description) VALUES
 ('FINE_OVERDUE_PER_DAY',                N'5000',   N'Tiền phạt mỗi ngày trả trễ cho mỗi cuốn sách (VND)'),
 ('FINE_LOST_RATE',                      N'2',      N'Trọng số phạt cho sách làm mất'),
 ('FINE_DAMAGED_LIGHT_RATE',             N'0.5',    N'Trọng số phạt cho sách hư nhẹ'),
 ('FINE_DAMAGED_HEAVY_REPAIRABLE_RATE',  N'1',      N'Trọng số phạt cho sách hư nặng có thể phục hồi'),
 ('FINE_DAMAGED_HEAVY_IRREPARABLE_RATE', N'1.5',    N'Trọng số phạt cho sách hư nặng, không thể phục hồi'),
 ('MAX_BORROW_DAYS',                     N'14',     N'Số ngày mượn tối đa cho mỗi cuốn sách'),
 ('MAX_BOOKS_BORROW',                    N'5',      N'Số sách tối đa được mượn'),
 ('MAX_FINE_BEFORE_BLOCK',               N'50000',  N'Tổng tiền phạt còn nợ tối đa; vượt quá sẽ không được mượn thêm (VND)'),
 ('DUE_REMINDER_DAYS',                   N'1',      N'Số ngày trước hạn trả để gửi thông báo nhắc'),
 ('MEMBERSHIP_FEE',                      N'100000', N'Phí đăng ký thẻ thành viên (VND)'),
 ('CARD_MAKER_FEE',                      N'50000',  N'Phí làm thẻ cứng thành viên (VND)');

-- ---------- ROLES ----------
INSERT INTO roles (name, description) VALUES
 ('ADMIN',     N'Quản trị hệ thống'),
 ('LIBRARIAN', N'Thủ thư'),
 ('MEMBER',    N'Thành viên thư viện (có thẻ mượn sách)'),
 ('READER',    N'Độc giả (chưa có thẻ thành viên, chỉ xem)');

-- ---------- PERMISSIONS ----------
-- Moi tai nguyen co bo quyen READ / WRITE / DELETE.
-- WRITE: tao/cap nhat/thuc hien thao tac ghi; DELETE: xoa mem/huy theo nghiep vu.
INSERT INTO permissions (code, description) VALUES
 -- Tai khoan
 ('USER_READ',             N'Xem tài khoản người dùng'),
 ('USER_WRITE',            N'Tạo và cập nhật tài khoản người dùng'),
 ('USER_DELETE',           N'Xóa mềm tài khoản người dùng'),
 -- Vai trò và quyền
 ('ROLE_READ',             N'Xem danh sách vai trò'),
 ('ROLE_WRITE',            N'Tạo và cập nhật vai trò'),
 ('ROLE_DELETE',           N'Xóa mềm vai trò'),
 ('PERMISSION_READ',       N'Xem danh sách quyền'),
 ('PERMISSION_WRITE',      N'Tạo và cập nhật quyền'),
 ('PERMISSION_DELETE',     N'Xóa mềm quyền'),
 -- Sách và các danh mục liên quan
 ('BOOK_READ',             N'Xem và tìm kiếm sách'),
 ('BOOK_WRITE',            N'Tạo và cập nhật sách'),
 ('BOOK_DELETE',           N'Xóa mềm sách'),
 ('CATEGORY_READ',         N'Xem thể loại sách'),
 ('CATEGORY_WRITE',        N'Tạo và cập nhật thể loại sách'),
 ('CATEGORY_DELETE',       N'Xóa mềm thể loại sách'),
 ('AUTHOR_READ',           N'Xem tác giả'),
 ('AUTHOR_WRITE',          N'Tạo và cập nhật tác giả'),
 ('AUTHOR_DELETE',         N'Xóa mềm tác giả'),
 ('PUBLISHER_READ',        N'Xem nhà xuất bản'),
 ('PUBLISHER_WRITE',       N'Tạo và cập nhật nhà xuất bản'),
 ('PUBLISHER_DELETE',      N'Xóa mềm nhà xuất bản'),
 ('SHELF_READ',            N'Xem kệ sách'),
 ('SHELF_WRITE',           N'Tạo và cập nhật kệ sách'),
 ('SHELF_DELETE',          N'Xóa mềm kệ sách'),
 -- Thành viên
 ('MEMBER_READ',           N'Xem thông tin thành viên'),
 ('MEMBER_WRITE',          N'Tạo và cập nhật thành viên'),
 ('MEMBER_DELETE',         N'Xóa mềm thành viên'),
 -- Mượn trả
 ('BORROW_READ',           N'Xem phiếu mượn và chi tiết mượn'),
 ('BORROW_WRITE',          N'Lập phiếu mượn, gia hạn và ghi nhận trả sách'),
 ('BORROW_DELETE',         N'Hủy phiếu mượn theo quy định'),
 -- Tiền phạt
 ('FINE_READ',             N'Xem thông tin tiền phạt'),
 ('FINE_WRITE',            N'Tạo và cập nhật khoản phạt'),
 ('FINE_DELETE',           N'Hủy khoản phạt theo quy định'),
 ('FINE_PAYMENT_READ',     N'Xem thông tin thanh toán tiền phạt'),
 ('FINE_PAYMENT_WRITE',    N'Ghi nhận thanh toán tiền phạt'),
 ('FINE_PAYMENT_DELETE',   N'Hủy giao dịch thanh toán tiền phạt theo quy định'),
 -- Thanh toán thẻ thành viên
 ('MEMBER_PAYMENT_READ',   N'Xem hóa đơn đăng ký và gia hạn thẻ'),
 ('MEMBER_PAYMENT_WRITE',  N'Ghi nhận thanh toán phí thẻ thành viên'),
 ('MEMBER_PAYMENT_DELETE', N'Hủy giao dịch thanh toán phí thẻ theo quy định'),
 -- Cài đặt và thông báo
 ('SETTING_READ',          N'Xem cài đặt hệ thống'),
 ('SETTING_WRITE',         N'Tạo và cập nhật cài đặt hệ thống'),
 ('SETTING_DELETE',        N'Xóa cài đặt hệ thống'),
 ('NOTIFICATION_READ',     N'Xem thông báo'),
 ('NOTIFICATION_WRITE',    N'Tạo thông báo hoặc đánh dấu đã đọc'),
 ('NOTIFICATION_DELETE',   N'Xóa mềm thông báo');

-- ---------- ROLE_PERMISSIONS ----------
-- ADMIN: toàn bộ permission
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r CROSS JOIN permissions p
WHERE r.name = 'ADMIN';

-- LIBRARIAN: nghiệp vụ thư viện; chỉ xem ROLE_READ, không quản trị role/permission/cài đặt;
--            không có USER_WRITE/USER_DELETE, FINE_PAYMENT_DELETE, MEMBER_PAYMENT_DELETE
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
 'USER_READ',
 'ROLE_READ',
 'BOOK_READ', 'BOOK_WRITE', 'BOOK_DELETE', 'CATEGORY_READ', 'CATEGORY_WRITE', 'CATEGORY_DELETE', 'AUTHOR_READ', 'AUTHOR_WRITE', 'AUTHOR_DELETE', 'PUBLISHER_READ', 'PUBLISHER_WRITE', 'PUBLISHER_DELETE', 'SHELF_READ', 'SHELF_WRITE', 'SHELF_DELETE',
 'MEMBER_READ', 'MEMBER_WRITE', 'MEMBER_DELETE',
 'BORROW_READ', 'BORROW_WRITE', 'BORROW_DELETE',
 'FINE_READ', 'FINE_WRITE', 'FINE_DELETE', 'FINE_PAYMENT_READ', 'FINE_PAYMENT_WRITE',
 'MEMBER_PAYMENT_READ', 'MEMBER_PAYMENT_WRITE',
 'NOTIFICATION_READ', 'NOTIFICATION_WRITE'
)
WHERE r.name = 'LIBRARIAN';

-- MEMBER: chỉ đọc dữ liệu liên quan và cập nhật thông tin tài khoản cá nhân
-- API vẫn phải giới hạn dữ liệu theo user/member hiện tại, không chỉ dựa vào permission.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
 'USER_READ', 'USER_WRITE',
 'ROLE_READ',
 'BOOK_READ', 'CATEGORY_READ', 'AUTHOR_READ', 'PUBLISHER_READ', 'SHELF_READ',
 'BORROW_READ',
 'FINE_READ', 'FINE_PAYMENT_READ',
 'MEMBER_PAYMENT_READ',
 'NOTIFICATION_READ', 'NOTIFICATION_WRITE'
)
WHERE r.name = 'MEMBER';

-- READER: chỉ xem sách/danh mục và quản lý thông tin tài khoản cá nhân
-- Các API USER_WRITE phải tự giới hạn chỉ sửa tài khoản của chính mình.
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
 'USER_READ', 'USER_WRITE',
 'ROLE_READ',
 'BOOK_READ', 'CATEGORY_READ', 'AUTHOR_READ', 'PUBLISHER_READ', 'SHELF_READ',
 'NOTIFICATION_READ', 'NOTIFICATION_WRITE'
)
WHERE r.name = 'READER';

-- ---------- USERS DEMO ----------
-- Mat khau chung cua ca 3 tai khoan: Library@123  (BCrypt, doi sau khi chay that)
INSERT INTO users (username, password_hash, full_name, no_accent, email, role_id)
SELECT 'admin', '$2a$10$oBtQmWU9wIIEqhe8n5FS1.NcukW5XEGg.T/51Sgmap.RpTZozW3va',
       N'Quản trị viên', 'quan tri vien', 'admin@library.local', r.id
FROM roles r WHERE r.name = 'ADMIN';

INSERT INTO users (username, password_hash, full_name, no_accent, email, role_id)
SELECT 'librarian', '$2a$10$oBtQmWU9wIIEqhe8n5FS1.NcukW5XEGg.T/51Sgmap.RpTZozW3va',
       N'Thủ thư demo', 'thu thu demo', 'librarian@library.local', r.id
FROM roles r WHERE r.name = 'LIBRARIAN';

INSERT INTO users (username, password_hash, full_name, no_accent, email, role_id)
SELECT 'user', '$2a$10$oBtQmWU9wIIEqhe8n5FS1.NcukW5XEGg.T/51Sgmap.RpTZozW3va',
       N'Độc giả demo', 'doc gia demo', 'user@library.local', r.id
FROM roles r WHERE r.name = 'MEMBER';

-- Ho so thanh vien cho tai khoan 'user' (de dang nhap duoc chuc nang doc gia)
INSERT INTO members (user_id, member_code, phone, identity_number, address, card_expiry, card_status)
SELECT u.id, 'TV000001', '0900000001', '012345678901', N'Hà Nội',
       DATEADD(YEAR, 1, CAST(SYSDATETIME() AS DATE)), 'ISSUED'
FROM users u WHERE u.username = 'user';