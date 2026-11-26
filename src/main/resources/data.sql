-- Seed data for H2 development database
-- Roles
INSERT INTO roles (name) VALUES ('STUDENT'), ('STAFF'), ('ADMIN');

-- Departments
INSERT INTO departments (name, contact_email) VALUES
    ('Accommodation', 'accommodation@campus.example'),
    ('ICT Services', 'ict@campus.example'),
    ('Finance', 'finance@campus.example'),
    ('Academic Registry', 'registry@campus.example'),
    ('Facilities', 'facilities@campus.example');

-- Categories
INSERT INTO categories (name, description, department_id, is_active) VALUES
    ('Hostel & Accommodation', 'Room, hostel and housing issues', 1, true),
    ('ICT & Wi-Fi', 'Internet, portal and email issues', 2, true),
    ('Fees & Finance', 'Fee statements, payments, refunds', 3, true),
    ('Academic Records', 'Results, transcripts, registration', 4, true),
    ('Facilities & Maintenance', 'Classrooms, water, power, repairs', 5, true),
    ('Other', 'Anything not listed above', NULL, true);

-- Test users (password = "password123" encoded with BCrypt)
-- Note: In production, register users through the app
INSERT INTO users (role_id, department_id, admission_no, full_name, email, phone, password_hash, is_active, created_at, updated_at) VALUES
    (3, NULL, NULL, 'Test Admin', 'admin@strathmore.edu', NULL, '$2a$10$yWAOs4lS.2r6GvYVoOOv2eOugvZ9n5BZjkr5ju4siBFYD4MDj1s2O', true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (2, 2, NULL, 'Test Staff', 'staff@strathmore.edu', NULL, '$2a$10$yWAOs4lS.2r6GvYVoOOv2eOugvZ9n5BZjkr5ju4siBFYD4MDj1s2O', true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (1, NULL, '000001', 'Test Student', 'student@strathmore.edu', NULL, '$2a$10$yWAOs4lS.2r6GvYVoOOv2eOugvZ9n5BZjkr5ju4siBFYD4MDj1s2O', true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());