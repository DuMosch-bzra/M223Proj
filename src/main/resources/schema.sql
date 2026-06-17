-- ============================================================
-- Glauser Illnau AG – Auftragsverwaltung
-- Supabase / PostgreSQL Schema
-- ============================================================

-- Enums
CREATE TYPE order_status AS ENUM (
    'CREATED', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'APPROVED', 'INVOICED'
);
CREATE TYPE user_role AS ENUM ('ADMIN', 'MANAGER', 'EMPLOYEE');
CREATE TYPE document_type AS ENUM (
    'ORDER_DOCUMENT', 'REPORT', 'INVOICE', 'IMAGE', 'OTHER'
);

-- ============================================================
-- Customer
-- ============================================================
CREATE TABLE customer (
    id           BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    phone_number VARCHAR(50),
    email        VARCHAR(255),
    address      TEXT
);

-- ============================================================
-- User (Employee / Mitarbeiter)
-- NOTE: "user" is reserved in PostgreSQL → always quote it
-- Spring maps Employee entity to this table
-- ============================================================
CREATE TABLE "user" (
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(50) NOT NULL DEFAULT 'EMPLOYEE'
);

-- ============================================================
-- Work Order (mapped as "work_order" in Spring, DB table = work_order)
-- ============================================================
CREATE TABLE work_order (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    address      VARCHAR(255),
    status       VARCHAR(50) NOT NULL DEFAULT 'CREATED',
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    scheduled_at TIMESTAMP,
    completed_at TIMESTAMP,
    invoiced_at  TIMESTAMP,
    customer_id  BIGINT NOT NULL REFERENCES customer(id),
    employee_id  BIGINT REFERENCES "user"(id)
);

-- ============================================================
-- Report
-- ============================================================
CREATE TABLE report (
    id               BIGSERIAL PRIMARY KEY,
    work_description TEXT,
    working_hours    DECIMAL(5,2),
    used_materials   TEXT,
    approved         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    order_id         BIGINT NOT NULL UNIQUE REFERENCES work_order(id) ON DELETE CASCADE
);

-- ============================================================
-- Invoice
-- ============================================================
CREATE TABLE invoice (
    id           BIGSERIAL PRIMARY KEY,
    amount       DECIMAL(12,2) NOT NULL,
    paid         BOOLEAN NOT NULL DEFAULT FALSE,
    invoice_date DATE,
    order_id     BIGINT NOT NULL UNIQUE REFERENCES work_order(id) ON DELETE CASCADE
);

-- ============================================================
-- Appointment
-- ============================================================
CREATE TABLE appointment (
    id         BIGSERIAL PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time   TIMESTAMP,
    note       TEXT,
    order_id   BIGINT NOT NULL REFERENCES work_order(id) ON DELETE CASCADE
);

-- ============================================================
-- Document
-- ============================================================
CREATE TABLE document (
    id          BIGSERIAL PRIMARY KEY,
    file_name   VARCHAR(255) NOT NULL,
    file_path   VARCHAR(500) NOT NULL,
    type        VARCHAR(50) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW(),
    order_id    BIGINT NOT NULL REFERENCES work_order(id) ON DELETE CASCADE
);

-- ============================================================
-- Seed Data (Testdaten)
-- ============================================================

-- Admin-Benutzer (Passwort: admin123 → bcrypt hash)
INSERT INTO "user" (first_name, last_name, email, password, role) VALUES
('Hans', 'Glauser', 'admin@glauser-illnau.ch',
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'ADMIN'),
('Thomas', 'Meier', 'manager@glauser-illnau.ch',
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'MANAGER'),
('Peter', 'Keller', 'peter.keller@glauser-illnau.ch',
 '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'EMPLOYEE');

-- Testkunden
INSERT INTO customer (company_name, contact_person, phone_number, email, address) VALUES
('Muster AG', 'Max Muster', '044 123 45 67', 'info@muster-ag.ch', 'Musterstrasse 1, 8400 Winterthur'),
('Beispiel GmbH', 'Anna Beispiel', '052 987 65 43', 'anna@beispiel.ch', 'Bahnhofstrasse 10, 8001 Zürich'),
('Technik Rapperswil', 'Beat Technik', '055 210 30 40', 'bt@technik-rswil.ch', 'Seestrasse 5, 8640 Rapperswil');

-- Testaufträge
INSERT INTO work_order (title, description, address, status, customer_id) VALUES
('Heizungsservice Muster AG', 'Jährlicher Service der Heizungsanlage', 'Musterstrasse 1, 8400 Winterthur', 'CREATED', 1),
('Rohrbruch Reparatur', 'Wasserrohrbruch im Keller', 'Bahnhofstrasse 10, 8001 Zürich', 'SCHEDULED', 2),
('Badezimmer Umbau', 'Kompletter Umbau des EG-Badezimmers', 'Seestrasse 5, 8640 Rapperswil', 'IN_PROGRESS', 3);

-- Zweiten Auftrag disponieren
UPDATE work_order SET employee_id = 3, scheduled_at = NOW() + INTERVAL '2 days' WHERE id = 2;
UPDATE work_order SET employee_id = 3, scheduled_at = NOW() WHERE id = 3;
