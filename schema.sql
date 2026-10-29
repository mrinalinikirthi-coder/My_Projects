    -- ============================================
-- CANTEENFLOW DATABASE SCHEMA (v2 - FINAL)
-- Run this once in MySQL Workbench
-- ============================================

DROP DATABASE IF EXISTS canteenflow;
CREATE DATABASE canteenflow;
USE canteenflow;

-- ============================================
-- TABLE 1: students
-- ============================================
CREATE TABLE students (
    student_id      INT PRIMARY KEY AUTO_INCREMENT,
    roll_number     VARCHAR(20) UNIQUE NOT NULL,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(100) UNIQUE NOT NULL,
    department      VARCHAR(50),
    year_of_study   INT,
    password        VARCHAR(100) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLE 2: staff
-- ============================================
CREATE TABLE staff (
    staff_id    INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    role        ENUM('CASHIER','COOK','MANAGER') DEFAULT 'CASHIER',
    login_pin   VARCHAR(10) NOT NULL
);

-- ============================================
-- TABLE 3: menu_items
-- ============================================
CREATE TABLE menu_items (
    item_id              INT PRIMARY KEY AUTO_INCREMENT,
    item_name            VARCHAR(100) NOT NULL,
    price                DECIMAL(6,2) NOT NULL,
    category             ENUM('SNACKS','MEALS','BEVERAGES','DESSERTS'),
    avg_prep_time_min    INT DEFAULT 5,
    is_available         BOOLEAN DEFAULT TRUE
);

-- ============================================
-- TABLE 4: canteen_sessions (CORE TABLE)
-- Status flow: IN_QUEUE -> ORDERED -> SERVED
-- Session ends ONLY when staff marks SERVED
-- ============================================
CREATE TABLE canteen_sessions (
    session_id      INT PRIMARY KEY AUTO_INCREMENT,
    student_id      INT NOT NULL,
    entry_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    served_time     TIMESTAMP NULL,
    exit_time       TIMESTAMP NULL,
    wait_minutes    INT NULL,
    status          ENUM('IN_QUEUE','ORDERED','SERVED') DEFAULT 'IN_QUEUE',
    day_of_week     VARCHAR(10),
    hour_of_day     INT,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- ============================================
-- TABLE 5: orders
-- Status flow: PLACED -> PREPARING -> READY -> SERVED
-- ============================================
CREATE TABLE orders (
    order_id                INT PRIMARY KEY AUTO_INCREMENT,
    session_id              INT NOT NULL,
    student_id              INT NOT NULL,
    placed_by               ENUM('STUDENT','CASHIER') DEFAULT 'STUDENT',
    confirmed_by_staff_id   INT NULL,
    total_amount            DECIMAL(8,2) NOT NULL,
    order_time              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_at            TIMESTAMP NULL,
    ready_at                TIMESTAMP NULL,
    served_at               TIMESTAMP NULL,
    status                  ENUM('PLACED','PREPARING','READY','SERVED') DEFAULT 'PLACED',
    FOREIGN KEY (session_id) REFERENCES canteen_sessions(session_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (confirmed_by_staff_id) REFERENCES staff(staff_id) ON DELETE SET NULL
);

-- ============================================
-- TABLE 6: order_items
-- ============================================
CREATE TABLE order_items (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    order_id    INT NOT NULL,
    item_id     INT NOT NULL,
    quantity    INT DEFAULT 1,
    subtotal    DECIMAL(8,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id)  REFERENCES menu_items(item_id)
);

-- ============================================
-- TABLE 7: timetable
-- ============================================
CREATE TABLE timetable (
    timetable_id    INT PRIMARY KEY AUTO_INCREMENT,
    student_id      INT NOT NULL,
    subject_name    VARCHAR(100) NOT NULL,
    day_of_week     ENUM('MON','TUE','WED','THU','FRI','SAT') NOT NULL,
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- ============================================
-- TABLE 8: notifications (STUDENTS ONLY)
-- Staff use queue screens (no notifications)
-- ============================================
CREATE TABLE notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id      INT NOT NULL,
    message         TEXT NOT NULL,
    is_read         BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- ============================================
-- SAMPLE DATA
-- ============================================

-- Students
INSERT INTO students (roll_number, name, email, department, year_of_study, password) VALUES
('21CS001', 'Rahul Sharma',  'rahul@college.edu',  'CSE', 2, 'pass123'),
('21CS002', 'Priya Verma',   'priya@college.edu',  'CSE', 2, 'pass123'),
('21EC010', 'Amit Kumar',    'amit@college.edu',   'ECE', 3, 'pass123');

-- Staff
INSERT INTO staff (name, role, login_pin) VALUES
('Ramesh (Cashier)', 'CASHIER', '1234'),
('Suresh (Cook)',    'COOK',    '5678');

-- Menu
INSERT INTO menu_items (item_name, price, category, avg_prep_time_min) VALUES
('Samosa',        15.00, 'SNACKS',    3),
('Masala Dosa',   50.00, 'MEALS',     8),
('Veg Biryani',   80.00, 'MEALS',     12),
('Masala Chai',   10.00, 'BEVERAGES', 2),
('Cold Coffee',   40.00, 'BEVERAGES', 5),
('Gulab Jamun',   25.00, 'DESSERTS',  4);

-- Timetable for Rahul (student_id = 1)
INSERT INTO timetable (student_id, subject_name, day_of_week, start_time, end_time) VALUES
(1, 'Data Structures', 'MON', '09:00:00', '10:00:00'),
(1, 'DBMS',             'MON', '10:15:00', '11:15:00'),
(1, 'Java Lab',         'MON', '11:30:00', '13:00:00'),
(1, 'Maths',            'MON', '14:00:00', '15:00:00');

-- ============================================
-- VERIFY
-- ============================================
SELECT '✅ Tables created:' AS info;
SHOW TABLES;
SELECT 'Students:' AS info;
SELECT student_id, roll_number, name FROM students;
SELECT 'Staff:' AS info;
SELECT staff_id, name, role FROM staff;
SELECT 'Menu:' AS info;
SELECT item_id, item_name, price FROM menu_items;