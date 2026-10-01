-- =====================================================================
-- V1__init_schema.sql  (SQL Server)  -  16 bang
-- Quy uoc:
--  * CHI XOA MEM: moi bang co is_deleted (BIT, mac dinh 0). Khong dung DELETE that.
--  * no_accent: chuoi khong dau, ung dung tu tinh va luu de tim kiem.
--  * UNIQUE tren cot nghiep vu dung filtered unique index (WHERE is_deleted = 0)
--    de ban ghi da xoa mem khong chan viec tao lai gia tri trung.
--  * Chuoi co tieng Viet dung NVARCHAR. Anh (avatar, cover) luu base64 trong VARCHAR(MAX).
-- =====================================================================

-- ---------- AUTHENTICATION / AUTHORIZATION ----------
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
    identity_number VARCHAR(12)   NOT NULL,
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

-- ---------- THANH VIEN ----------
CREATE TABLE members (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id     BIGINT        NOT NULL,
    member_code VARCHAR(20)   NOT NULL,
    phone       VARCHAR(15)   NULL,
    address     NVARCHAR(255) NULL,
    card_expiry DATE          NOT NULL,
    is_deleted  BIT           NOT NULL CONSTRAINT DF_members_deleted DEFAULT 0,
    CONSTRAINT FK_members_user FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE UNIQUE INDEX UX_members_user_id     ON members(user_id)     WHERE is_deleted = 0;
CREATE UNIQUE INDEX UX_members_member_code ON members(member_code) WHERE is_deleted = 0;

-- ---------- DANH MUC SACH ----------
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

CREATE TABLE books (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    title        NVARCHAR(255) NOT NULL,
    no_accent    VARCHAR(255)  NOT NULL,        -- title khong dau, de tim kiem
    isbn         VARCHAR(20)   NOT NULL,
    publish_year INT           NULL,
    quantity     INT           NOT NULL CONSTRAINT DF_books_quantity  DEFAULT 0,
    available    INT           NOT NULL CONSTRAINT DF_books_available DEFAULT 0,
    cover        VARCHAR(MAX)  NULL,            -- base64 (data URI) anh bia
    is_deleted   BIT           NOT NULL CONSTRAINT DF_books_deleted DEFAULT 0,
    CONSTRAINT CK_books_quantity  CHECK (quantity >= 0),
    CONSTRAINT CK_books_available CHECK (available >= 0 AND available <= quantity)
);
CREATE UNIQUE INDEX UX_books_isbn ON books(isbn) WHERE is_deleted = 0;
CREATE INDEX IX_books_no_accent ON books(no_accent);

-- ---------- BANG TRUNG GIAN (quan he N-N, cung xoa mem) ----------
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

-- ---------- MUON / TRA ----------
CREATE TABLE borrow_records (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    member_id    BIGINT        NOT NULL,
    librarian_id BIGINT        NOT NULL,        -- users.id cua thu thu lap phieu
    borrow_date  DATE          NOT NULL,
    due_date     DATE          NOT NULL,
    status       VARCHAR(20)   NOT NULL CONSTRAINT DF_br_status DEFAULT 'BORROWING',
    note         NVARCHAR(255) NULL,
    is_deleted   BIT           NOT NULL CONSTRAINT DF_br_deleted DEFAULT 0,
    CONSTRAINT FK_br_member    FOREIGN KEY (member_id)    REFERENCES members(id),
    CONSTRAINT FK_br_librarian FOREIGN KEY (librarian_id) REFERENCES users(id),
    CONSTRAINT CK_br_status    CHECK (status IN ('BORROWING','RETURNED','OVERDUE')),
    CONSTRAINT CK_br_dates     CHECK (due_date >= borrow_date)
);
CREATE INDEX IX_br_member_id    ON borrow_records(member_id);
CREATE INDEX IX_br_librarian_id ON borrow_records(librarian_id);
CREATE INDEX IX_br_status_due   ON borrow_records(status, due_date);

CREATE TABLE borrow_details (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    borrow_id   BIGINT        NOT NULL,
    book_id     BIGINT        NOT NULL,
    return_date DATE          NULL,
    fine_amount DECIMAL(10,2) NOT NULL CONSTRAINT DF_bd_fine    DEFAULT 0,
    is_deleted  BIT           NOT NULL CONSTRAINT DF_bd_deleted DEFAULT 0,
    CONSTRAINT FK_bd_borrow FOREIGN KEY (borrow_id) REFERENCES borrow_records(id),
    CONSTRAINT FK_bd_book   FOREIGN KEY (book_id)   REFERENCES books(id),
    CONSTRAINT CK_bd_fine   CHECK (fine_amount >= 0)
);
CREATE INDEX IX_bd_borrow_id ON borrow_details(borrow_id);
CREATE INDEX IX_bd_book_id   ON borrow_details(book_id);

-- ---------- THONG BAO (RabbitMQ consumer ghi vao) ----------
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
