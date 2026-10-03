-- =====================================================================
-- V3__add_fines_and_settings.sql  (SQL Server)
--  * fines         : tung ban ghi tien phat, biet phat den tu phieu muon / cuon sach nao
--  * fine_payments : cac lan thanh toan tien phat (tra nhieu lan, khong gan voi khoan phat cu the)
--  * settings      : bang cau hinh dang key-value
-- =====================================================================

-- ---------- TIEN PHAT ----------
CREATE TABLE fines (
    id               BIGINT IDENTITY(1,1) PRIMARY KEY,
    borrow_id        BIGINT        NOT NULL,                 -- phat den tu phieu muon nao
    borrow_detail_id BIGINT        NULL,                     -- cuon sach nao trong phieu (NULL neu phat chung ca phieu)
    amount           DECIMAL(10,2) NOT NULL,
    reason           VARCHAR(20)   NOT NULL,                 -- OVERDUE / LOST / DAMAGED
    overdue_days     INT           NULL,                     -- so ngay tre (chi dung khi reason = OVERDUE)
    note             NVARCHAR(255) NULL,
    created_at       DATETIME2     NOT NULL CONSTRAINT DF_fines_created DEFAULT SYSDATETIME(),
    is_deleted       BIT           NOT NULL CONSTRAINT DF_fines_deleted DEFAULT 0,
    attachment       VARCHAR(MAX)  NULL,
    CONSTRAINT FK_fines_borrow FOREIGN KEY (borrow_id)        REFERENCES borrow_records(id),
    CONSTRAINT FK_fines_detail FOREIGN KEY (borrow_detail_id) REFERENCES borrow_details(id),
    CONSTRAINT CK_fines_amount CHECK (amount > 0),
    CONSTRAINT CK_fines_reason CHECK (reason IN ('OVERDUE','LOST','DAMAGED'))
);
CREATE INDEX IX_fines_borrow_id ON fines(borrow_id);
CREATE INDEX IX_fines_detail_id ON fines(borrow_detail_id);

-- ---------- THANH TOAN TIEN PHAT ----------
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

-- ---------- SETTINGS (KEY - VALUE) ----------
CREATE TABLE settings (
    id            BIGINT IDENTITY(1,1) PRIMARY KEY,
    setting_key   VARCHAR(100)  NOT NULL,
    setting_value NVARCHAR(500) NOT NULL,
    description   NVARCHAR(255) NULL,
    updated_at    DATETIME2     NOT NULL CONSTRAINT DF_settings_updated DEFAULT SYSDATETIME(),
    is_deleted    BIT           NOT NULL CONSTRAINT DF_settings_deleted DEFAULT 0
);
CREATE UNIQUE INDEX UX_settings_key ON settings(setting_key) WHERE is_deleted = 0;

INSERT INTO settings (setting_key, setting_value, description) VALUES
 ('FINE_OVERDUE_PER_DAY',          N'5000',  N'Tien phat moi ngay tra tre cho moi cuon sach (VND)'),
 ('FINE_LOST_RATE',                N'2',     N'Trọng số phạt cho cho sách làm mất'),
 ('FINE_DAMAGED_LIGHT_RATE',       N'0.5',   N'Trọng số phạt cho làm sách hư nhẹ'),
 ('FINE_DAMAGED_HEAVY_REPAIRABLE_RATE',       N'1',     N'Trọng số phạt cho làm sách hư nặng có thể phục hồi'),
 ('FINE_DAMAGED_HEAVY_IRREPARABLE_RATE',       N'1.5',     N'Trọng số phạt cho làm sách hư nặng, ko thể phục hồi'),
 ('MAX_BORROW_DAYS',               N'14',    N'So ngay muon toi da cho moi cuon sach'),
 ('MAX_BOOKS_BORROW',              N'5',     N'So sach toi da duoc muon'),
 ('MAX_FINE_BEFORE_BLOCK',         N'50000', N'Tong tien phat con no toi da; vuot qua se khong duoc muon them (VND)'),
 ('DUE_REMINDER_DAYS',             N'1',     N'So ngay truoc han tra de gui thong bao nhac');