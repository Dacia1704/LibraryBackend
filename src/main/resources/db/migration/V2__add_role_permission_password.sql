-- =====================================================================
-- V2__seed_roles_permissions_users.sql  (SQL Server)
-- Du lieu khoi tao: role, permission, phan quyen, tai khoan demo
-- Mat khau demo cua ca 3 tai khoan: Library@123  (BCrypt, doi sau khi chay that)
-- =====================================================================

-- ---------- ROLES ----------
INSERT INTO roles (name, description) VALUES
 ('ADMIN',     N'Quan tri he thong'),
 ('LIBRARIAN', N'Thu thu'),
 ('MEMBER',    N'Doc gia');

-- ---------- PERMISSIONS ----------
INSERT INTO permissions (code, description) VALUES
 ('USER_READ',                N'Quan ly tai khoan nguoi dung'),
 ('USER_WRITE',               N'Quan ly tai khoan nguoi dung'),
 ('ROLE_MANAGE',              N'Quan ly role va permission'),
 ('BOOK_READ',                N'Xem va tim kiem sach, tac gia, the loai, nha xuat ban'),
 ('BOOK_WRITE',               N'Them, sua, xoa sach, tac gia, the loai, nha xuat ban'),
 ('MEMBER_READ',              N'Xem danh sach thanh vien'),
 ('MEMBER_WRITE',             N'Them, sua, xoa thanh vien'),
 ('BORROW_WRITE',            N'Lap phieu muon va ghi nhan tra sach'),
 ('BORROW_READ',              N'Xem phieu muon'),
 ('CATEGORY_MANAGE',          N'Quan ly danh muc'),
 ('AUTHOR_MANAGE',           N'Quan ly tac gia'),
 ('PUBLISHER_MANAGE',         N'Quan ly nha xuat ban'),
 ('PERMISSION_MANAGE',         N'Quan ly quyen'),
 ('SETTING_MANAGE',           N'Quan ly cai dat'),
 ('FINE_READ',                N'Xem thong tin phat'),
 ('FINE_PAYMENT_READ',        N'Xem thong tin tra tien phat'),
 ('FINE_PAYMENT_WRITE',       N'Thu phat'),
 ('MEMBER_PAYMENT_WRITE',        N'Mo thanh vien'),
 ('MEMBER_PAYMENT_READ',         N'Xem hoa don mo thanh vien');

-- ---------- ROLE_PERMISSIONS ----------
-- ADMIN: toan bo permission
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r CROSS JOIN permissions p
WHERE r.name = 'ADMIN';

-- LIBRARIAN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN
 ('USER_READ', 'BOOK_READ', 'BOOK_WRITE', 'MEMBER_READ', 'MEMBER_WRITE',
 'BORROW_CREATE', 'BORROW_READ', 'FINE_PAYMENT_WRITE', 'MEMBER_PAYMENT_READ', 'MEMBER_PAYMENT_WRITE')
WHERE r.name = 'LIBRARIAN';

-- MEMBER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN
 ('USER_READ','USER_WRITE','BOOK_READ', 'BORROW_READ',
 'FINE_READ','FINE_PAYMENT_READ', 'MEMBER_PAYMENT_READ')
WHERE r.name = 'MEMBER';

-- ---------- USERS DEMO (mat khau: Library@123) ----------
INSERT INTO users (username, password_hash, full_name, no_accent, email, role_id)
SELECT 'admin', '$2a$10$oBtQmWU9wIIEqhe8n5FS1.NcukW5XEGg.T/51Sgmap.RpTZozW3va', N'Quản trị viên', 'quan tri vien', 'admin@library.local', r.id
FROM roles r WHERE r.name = 'ADMIN';