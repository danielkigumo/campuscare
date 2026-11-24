-- =====================================================================
-- Campus Care - Module 5: Database & Integration
-- PostgreSQL schema (use as src/main/resources/db/migration/V1__schema.sql)
-- Group 7 - Software Engineering Group Project
-- =====================================================================

-- ---------- Lookup tables ----------------------------------------------

CREATE TABLE roles (
    role_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    VARCHAR(20) NOT NULL UNIQUE
            CHECK (name IN ('STUDENT', 'STAFF', 'ADMIN'))
);

CREATE TABLE departments (
    department_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          VARCHAR(100) NOT NULL UNIQUE,
    contact_email VARCHAR(150)
);

-- ---------- Users --------------------------------------------------------

CREATE TABLE users (
    user_id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_id       BIGINT       NOT NULL REFERENCES roles (role_id) ON DELETE RESTRICT,
    department_id BIGINT       REFERENCES departments (department_id) ON DELETE SET NULL,
    admission_no  VARCHAR(20)  UNIQUE,
    full_name     VARCHAR(100) NOT NULL CHECK (length(trim(full_name)) > 0),
    email         VARCHAR(150) NOT NULL,
    phone         VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Email is unique ignoring upper/lower case
CREATE UNIQUE INDEX ux_users_email_lower ON users (lower(email));

-- ---------- Categories ---------------------------------------------------

CREATE TABLE categories (
    category_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          VARCHAR(100) NOT NULL UNIQUE,
    description   VARCHAR(255),
    department_id BIGINT REFERENCES departments (department_id) ON DELETE SET NULL,
    is_active     BOOLEAN NOT NULL DEFAULT TRUE
);

-- ---------- Requests -----------------------------------------------------

CREATE TABLE requests (
    request_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id   BIGINT       NOT NULL REFERENCES users (user_id) ON DELETE RESTRICT,
    category_id  BIGINT       NOT NULL REFERENCES categories (category_id) ON DELETE RESTRICT,
    assigned_to  BIGINT       REFERENCES users (user_id) ON DELETE SET NULL,
    request_type VARCHAR(20)  NOT NULL
                 CHECK (request_type IN ('SERVICE_REQUEST', 'COMPLAINT')),
    title        VARCHAR(150) NOT NULL CHECK (length(trim(title)) >= 5),
    description  TEXT         NOT NULL CHECK (length(trim(description)) >= 10),
    priority     VARCHAR(10)  NOT NULL DEFAULT 'MEDIUM'
                 CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
    status       VARCHAR(20)  NOT NULL DEFAULT 'SUBMITTED'
                 CHECK (status IN ('SUBMITTED', 'ASSIGNED', 'IN_PROGRESS',
                                   'RESOLVED', 'CLOSED', 'REJECTED', 'CANCELLED')),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    resolved_at  TIMESTAMPTZ
);

CREATE INDEX ix_requests_student  ON requests (student_id);
CREATE INDEX ix_requests_assignee ON requests (assigned_to);
CREATE INDEX ix_requests_category ON requests (category_id);
CREATE INDEX ix_requests_status   ON requests (status);
CREATE INDEX ix_requests_created  ON requests (created_at);

-- ---------- Status history -----------------------------------------------

CREATE TABLE request_status_history (
    history_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    request_id BIGINT      NOT NULL REFERENCES requests (request_id) ON DELETE CASCADE,
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    changed_by BIGINT      NOT NULL REFERENCES users (user_id) ON DELETE RESTRICT,
    note       VARCHAR(255),
    changed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (new_status IN ('SUBMITTED', 'ASSIGNED', 'IN_PROGRESS',
                          'RESOLVED', 'CLOSED', 'REJECTED', 'CANCELLED'))
);

CREATE INDEX ix_history_request ON request_status_history (request_id, changed_at);

-- ---------- Responses ----------------------------------------------------

CREATE TABLE responses (
    response_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    request_id  BIGINT      NOT NULL REFERENCES requests (request_id) ON DELETE CASCADE,
    author_id   BIGINT      NOT NULL REFERENCES users (user_id) ON DELETE RESTRICT,
    message     TEXT        NOT NULL CHECK (length(trim(message)) > 0),
    is_internal BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX ix_responses_request ON responses (request_id, created_at);

-- ---------- Notifications ------------------------------------------------

CREATE TABLE notifications (
    notification_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    request_id      BIGINT       REFERENCES requests (request_id) ON DELETE CASCADE,
    message         VARCHAR(255) NOT NULL,
    is_read         BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX ix_notifications_user ON notifications (user_id, is_read);

-- =====================================================================
-- Seed data (same records for every team member)
-- =====================================================================

INSERT INTO roles (name) VALUES ('STUDENT'), ('STAFF'), ('ADMIN');

INSERT INTO departments (name, contact_email) VALUES
    ('Accommodation',     'accommodation@campus.example'),
    ('ICT Services',      'ict@campus.example'),
    ('Finance',           'finance@campus.example'),
    ('Academic Registry', 'registry@campus.example'),
    ('Facilities',        'facilities@campus.example');

INSERT INTO categories (name, description, department_id) VALUES
    ('Hostel & Accommodation', 'Room, hostel and housing issues',    (SELECT department_id FROM departments WHERE name = 'Accommodation')),
    ('ICT & Wi-Fi',            'Internet, portal and email issues',  (SELECT department_id FROM departments WHERE name = 'ICT Services')),
    ('Fees & Finance',         'Fee statements, payments, refunds',  (SELECT department_id FROM departments WHERE name = 'Finance')),
    ('Academic Records',       'Results, transcripts, registration', (SELECT department_id FROM departments WHERE name = 'Academic Registry')),
    ('Facilities & Maintenance','Classrooms, water, power, repairs', (SELECT department_id FROM departments WHERE name = 'Facilities')),
    ('Other',                  'Anything not listed above',          NULL);

-- Test accounts for development ONLY. password_hash below is a placeholder,
-- not a real BCrypt hash: replace it by registering through the app.
INSERT INTO users (role_id, department_id, admission_no, full_name, email, password_hash) VALUES
    ((SELECT role_id FROM roles WHERE name = 'ADMIN'),   NULL, NULL,     'Test Admin',   'admin@strathmore.edu',   'REPLACE_WITH_BCRYPT_HASH'),
    ((SELECT role_id FROM roles WHERE name = 'STAFF'),   (SELECT department_id FROM departments WHERE name = 'ICT Services'), NULL, 'Test Staff', 'staff@strathmore.edu', 'REPLACE_WITH_BCRYPT_HASH'),
    ((SELECT role_id FROM roles WHERE name = 'STUDENT'), NULL, '000001', 'Test Student', 'student@strathmore.edu', 'REPLACE_WITH_BCRYPT_HASH');