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
 ('USER_MANAGE',          N'Quan ly tai khoan nguoi dung'),
 ('ROLE_MANAGE',          N'Quan ly role va permission'),
 ('BOOK_READ',            N'Xem va tim kiem sach, tac gia, the loai, nha xuat ban'),
 ('BOOK_WRITE',           N'Them, sua, xoa sach, tac gia, the loai, nha xuat ban'),
 ('MEMBER_READ',          N'Xem danh sach thanh vien'),
 ('MEMBER_WRITE',         N'Them, sua, xoa thanh vien'),
 ('BORROW_CREATE',        N'Lap phieu muon va ghi nhan tra sach'),
 ('BORROW_READ_ALL',      N'Xem tat ca phieu muon'),
 ('BORROW_READ_OWN',      N'Xem phieu muon cua chinh minh'),
 ('NOTIFICATION_READ_ALL',N'Xem tat ca thong bao'),
 ('NOTIFICATION_READ_OWN',N'Xem thong bao cua chinh minh'),
 ('REPORT_VIEW',          N'Xem bao cao thong ke');

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
 ('BOOK_READ','BOOK_WRITE','MEMBER_READ','MEMBER_WRITE',
  'BORROW_CREATE','BORROW_READ_ALL','NOTIFICATION_READ_ALL','REPORT_VIEW')
WHERE r.name = 'LIBRARIAN';

-- MEMBER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN
 ('BOOK_READ','BORROW_READ_OWN','NOTIFICATION_READ_OWN')
WHERE r.name = 'MEMBER';

-- ---------- USERS DEMO (mat khau: Library@123) ----------
INSERT INTO users (username, password_hash, full_name, no_accent, email, role_id, identity_number)
SELECT 'admin', '$2a$10$oBtQmWU9wIIEqhe8n5FS1.NcukW5XEGg.T/51Sgmap.RpTZozW3va', N'Quản trị viên', 'quan tri vien', 'admin@library.local', r.id, '111111111111'
FROM roles r WHERE r.name = 'ADMIN';