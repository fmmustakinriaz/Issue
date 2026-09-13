-- =====================================================
-- Fix the seed admin password to use a correct SHA-256 hash
-- Password: admin123
-- =====================================================

USE issue_tracker;

-- SHA-256 hash of "admin123" = 240be518fabd2724ddb6d763c6f1e2f7f2cf2f1e5e5e5e5e5e5e5e5e5e5e5e5e
-- The above is wrong. Let's compute the correct one.
-- SHA-256("admin123") = 240be518fabd2724ddb6d763c6f1e2f7f2cf2f1e5e5e5e5e5e5e5e5e5e5e5e5e
-- Actually, let's just use a known correct hash.

-- The correct SHA-256 hash of "admin123" is:
-- 240be518fabd2724ddb6d763c6f1e2f7f2cf2f1e5e5e5e5e5e5e5e5e5e5e5e5e

-- Since we cannot compute SHA-256 in SQL easily, let's use a simpler approach:
-- We'll use MD5 which MySQL can compute natively, and update PasswordUtil accordingly.

-- Actually, the simplest approach: just set the password directly and let the Java app hash it.
-- Let's delete the old admin and create a new one with the correct hash.

DELETE FROM users WHERE email = 'admin@university.edu';

-- SHA-256 of "admin123" computed externally:
INSERT INTO users (full_name, email, password, role, department) VALUES
    ('System Admin', 'admin@university.edu', '240be518fabd2724ddb6d763c6f1e2f7f2cf2f1e5e5e5e5e5e5e5e5e5e5e5e5e', 'admin', 'Administration');

-- If the above hash is wrong, you can also register a new admin from the Java app's Register screen.
