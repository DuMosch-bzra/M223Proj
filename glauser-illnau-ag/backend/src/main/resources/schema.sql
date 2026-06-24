-- ============================================================
-- Glauser Illnau AG – Auftragsverwaltung
-- Supabase / PostgreSQL Schema
-- Run this ONCE in the Supabase SQL Editor
-- ============================================================

-- ============================================================
-- Customer
-- ============================================================
CREATE TABLE IF NOT EXISTS customer (
    id             BIGSERIAL PRIMARY KEY,
    company_name   VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    phone_number   VARCHAR(50),
    email          VARCHAR(255),
    address        TEXT
);

-- ============================================================
-- User (= Employee / Mitarbeiter)
-- "user" is a reserved word in PostgreSQL – always quote it
-- ============================================================
CREATE TABLE IF NOT EXISTS "user" (
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  NOT NULL DEFAULT 'EMPLOYEE'
);

-- ============================================================
-- Work Order
-- Table is named work_order (Spring entity: WorkOrder)
-- ============================================================
CREATE TABLE IF NOT EXISTS work_order (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    address      VARCHAR(255),
    status       VARCHAR(50)  NOT NULL DEFAULT 'CREATED',
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    scheduled_at TIMESTAMP,
    completed_at TIMESTAMP,
    invoiced_at  TIMESTAMP,
    customer_id  BIGINT       NOT NULL REFERENCES customer(id),
    employee_id  BIGINT       REFERENCES "user"(id)
);

-- ============================================================
-- Report
-- ============================================================
CREATE TABLE IF NOT EXISTS report (
    id               BIGSERIAL PRIMARY KEY,
    work_description TEXT,
    working_hours    DECIMAL(5,2),
    used_materials   TEXT,
    approved         BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    order_id         BIGINT       NOT NULL UNIQUE REFERENCES work_order(id) ON DELETE CASCADE
);

-- ============================================================
-- Invoice
-- ============================================================
CREATE TABLE IF NOT EXISTS invoice (
    id           BIGSERIAL PRIMARY KEY,
    amount       DECIMAL(12,2) NOT NULL,
    paid         BOOLEAN       NOT NULL DEFAULT FALSE,
    invoice_date DATE,
    order_id     BIGINT        NOT NULL UNIQUE REFERENCES work_order(id) ON DELETE CASCADE
);

-- ============================================================
-- Appointment
-- ============================================================
CREATE TABLE IF NOT EXISTS appointment (
    id         BIGSERIAL PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time   TIMESTAMP,
    note       TEXT,
    order_id   BIGINT    NOT NULL REFERENCES work_order(id) ON DELETE CASCADE
);

-- ============================================================
-- Document
-- ============================================================
CREATE TABLE IF NOT EXISTS document (
    id          BIGSERIAL PRIMARY KEY,
    file_name   VARCHAR(255) NOT NULL,
    file_path   VARCHAR(500) NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    uploaded_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    order_id    BIGINT       NOT NULL REFERENCES work_order(id) ON DELETE CASCADE
);

-- ============================================================
-- Seed Data – run only once
-- Password for all users: admin123
-- ============================================================
INSERT INTO "user" (first_name, last_name, email, password, role)
SELECT 'Hans','Glauser','admin@glauser-illnau.ch',
       '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM "user" WHERE email='admin@glauser-illnau.ch');

INSERT INTO "user" (first_name, last_name, email, password, role)
SELECT 'Thomas','Meier','manager@glauser-illnau.ch',
       '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','MANAGER'
WHERE NOT EXISTS (SELECT 1 FROM "user" WHERE email='manager@glauser-illnau.ch');

INSERT INTO "user" (first_name, last_name, email, password, role)
SELECT 'Peter','Keller','peter.keller@glauser-illnau.ch',
       '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','EMPLOYEE'
WHERE NOT EXISTS (SELECT 1 FROM "user" WHERE email='peter.keller@glauser-illnau.ch');

INSERT INTO customer (company_name, contact_person, phone_number, email, address)
SELECT 'Muster AG','Max Muster','044 123 45 67','info@muster-ag.ch','Musterstrasse 1, 8400 Winterthur'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE company_name='Muster AG');

INSERT INTO customer (company_name, contact_person, phone_number, email, address)
SELECT 'Beispiel GmbH','Anna Beispiel','052 987 65 43','anna@beispiel.ch','Bahnhofstrasse 10, 8001 Zürich'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE company_name='Beispiel GmbH');

INSERT INTO customer (company_name, contact_person, phone_number, email, address)
SELECT 'Technik Rapperswil','Beat Technik','055 210 30 40','bt@technik-rswil.ch','Seestrasse 5, 8640 Rapperswil'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE company_name='Technik Rapperswil');
