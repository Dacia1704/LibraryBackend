-- =====================================================================
-- V4__add_member_payments.sql  (SQL Server)
--  * member_payments : luu so tien thanh vien da nop khi dang ky (hoac gia han) the
--  * settings        : them gia tri mac dinh cho phi lam the
-- Van theo quy uoc V1: xoa mem (is_deleted).
-- =====================================================================

-- ---------- THANH TOAN PHI THANH VIEN ----------
CREATE TABLE member_payments (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    member_id    BIGINT        NOT NULL,
    amount       DECIMAL(10,2) NOT NULL,                    -- so tien da nop
    payment_type VARCHAR(10)   NOT NULL CONSTRAINT DF_mp_type DEFAULT 'REGISTER',   -- REGISTER / RENEW
    paid_at      DATETIME2     NOT NULL CONSTRAINT DF_mp_paid DEFAULT SYSDATETIME(),
    received_by  BIGINT        NOT NULL,                    -- users.id cua thu thu thu tien
    note         NVARCHAR(255) NULL,
    is_deleted   BIT           NOT NULL CONSTRAINT DF_mp_deleted DEFAULT 0,
    CONSTRAINT FK_mp_member   FOREIGN KEY (member_id)   REFERENCES members(id),
    CONSTRAINT FK_mp_receiver FOREIGN KEY (received_by) REFERENCES users(id),
    CONSTRAINT CK_mp_amount   CHECK (amount >= 0),
    CONSTRAINT CK_mp_type     CHECK (payment_type IN ('REGISTER','RENEW'))
);
CREATE INDEX IX_mp_member_id   ON member_payments(member_id);
CREATE INDEX IX_mp_received_by ON member_payments(received_by);
CREATE INDEX IX_mp_paid_at     ON member_payments(paid_at);

-- ---------- SETTINGS ----------
INSERT INTO settings (setting_key, setting_value, description) VALUES
 ('MEMBERSHIP_FEE', N'100000', N'Phi dang ky the thanh vien (VND)');