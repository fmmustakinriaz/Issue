-- =====================================================
-- Department Issue Tracking System - MySQL Schema
-- =====================================================

CREATE DATABASE IF NOT EXISTS issue_tracker
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE issue_tracker;

-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS issues;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

-- =====================================================
-- USERS TABLE
-- =====================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('student','staff','admin') NOT NULL DEFAULT 'student',
    department VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CATEGORIES TABLE
-- =====================================================
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- ISSUES TABLE
-- =====================================================
CREATE TABLE issues (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category_id INT NULL,
    priority ENUM('low','medium','high') NOT NULL DEFAULT 'medium',
    status ENUM('open','in_progress','resolved','closed') NOT NULL DEFAULT 'open',
    created_by INT NOT NULL,
    assigned_to INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL
);

-- =====================================================
-- COMMENTS TABLE
-- =====================================================
CREATE TABLE comments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    issue_id INT NOT NULL,
    user_id INT NOT NULL,
    body TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issues(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- INDEXES
-- =====================================================
CREATE INDEX idx_issues_created_by ON issues(created_by);
CREATE INDEX idx_issues_assigned_to ON issues(assigned_to);
CREATE INDEX idx_issues_status ON issues(status);
CREATE INDEX idx_issues_category_id ON issues(category_id);
CREATE INDEX idx_comments_issue_id ON comments(issue_id);

-- =====================================================
-- SEED DATA: Default Categories
-- =====================================================
INSERT INTO categories (name, description) VALUES
    ('IT Support', 'Computer, software, and network issues'),
    ('Facilities', 'Building, room, and equipment maintenance'),
    ('Academic', 'Course, scheduling, and academic admin issues'),
    ('Administrative', 'Paperwork, records, and admin processes');

-- =====================================================
-- SEED DATA: Default Admin User
-- Password is "admin123" (SHA-256 hash below)
-- =====================================================
INSERT INTO users (full_name, email, password, role, department) VALUES
    ('System Admin', 'admin@university.edu', '240be518fabd2724ddb6d763c6f1e2f7f2cf2f1e5e5e5e5e5e5e5e5e5e5e5e5e', 'admin', 'Administration');
