-- ============================================================================
-- V4__fix_guardian_links.sql
-- Correct guardian links: Mohammad Tariq Khan is exclusively Ali Khan's father.
-- Sara Khan's guardian is Abdul Hameed Khan.
-- ============================================================================

-- 1. Insert Abdul Hameed Khan as guardian
INSERT INTO guardians (id, user_id, full_name, relationship_type, cnic_number, phone_primary, occupation, address)
VALUES (3, NULL, 'Abdul Hameed Khan', 'FATHER', '41303-9876543-1', '+92 300 9876543', 'Bank Manager', 'House # 18, Block B, Unit 3, Latifabad, Hyderabad')
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name);

-- 2. Remove erroneous link between Mohammad Tariq Khan (guardian_id = 1) and Sara Khan (student_id = 2)
DELETE FROM guardian_student_links WHERE guardian_id = 1 AND student_id = 2;

-- 3. Link Abdul Hameed Khan (guardian_id = 3) to Sara Khan (student_id = 2)
INSERT IGNORE INTO guardian_student_links (guardian_id, student_id, is_primary_contact, is_financial_sponsor)
VALUES (3, 2, TRUE, TRUE);
