-- Stored Procedures for Student Skills and Certifications
-- Add these definitions to your database schema or execute them separately.

DELIMITER ;;

-- --- Skill Procedures ---

-- Get skills for a specific student
DROP PROCEDURE IF EXISTS `sp_get_student_skills`;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_student_skills`(
  IN p_student_id INT
)
BEGIN
SELECT
    sk.skillID,
    sk.skillName,
    sk.description
FROM skill sk
         JOIN studentskill ss ON sk.skillID = ss.skillID
WHERE ss.studentID = p_student_id;
END ;;

-- Find skill by name, create if not exists, link to student
DROP PROCEDURE IF EXISTS `sp_find_or_create_skill_and_link_student`;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_find_or_create_skill_and_link_student`(
  IN  p_student_id INT,
  IN  p_skill_name VARCHAR(100),
  OUT p_skill_id   INT,
  OUT p_skill_desc TEXT
)
BEGIN
    DECLARE v_skill_id INT;
    DECLARE v_skill_desc TEXT;

    -- Check if skill exists
SELECT skillID, description INTO v_skill_id, v_skill_desc
FROM skill
WHERE skillName = p_skill_name;

-- If skill doesn't exist, create it
IF v_skill_id IS NULL THEN
INSERT INTO skill (skillName, description) VALUES (p_skill_name, NULL); -- Assuming description is optional
SET v_skill_id = LAST_INSERT_ID();
        SET v_skill_desc = NULL; -- Or fetch the default if any
END IF;

-- Link skill to student (ignore if already linked)
INSERT IGNORE INTO studentskill (studentID, skillID, proficiencyLevel)
VALUES (p_student_id, v_skill_id, 'BEGINNER'); -- Default proficiency

-- Return the skill ID and description
SET p_skill_id = v_skill_id;
    SET p_skill_desc = v_skill_desc;

END ;;

-- Remove a skill link from a student
DROP PROCEDURE IF EXISTS `sp_remove_student_skill`;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_remove_student_skill`(
  IN  p_student_id INT,
  IN  p_skill_id   INT,
  OUT p_rows_deleted INT
)
BEGIN
DELETE FROM studentskill
WHERE studentID = p_student_id AND skillID = p_skill_id;

SELECT ROW_COUNT() INTO p_rows_deleted;
END ;;


-- --- Certification Procedures ---

-- Get certifications for a specific student
DROP PROCEDURE IF EXISTS `sp_get_student_certifications`;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_student_certifications`(
  IN p_student_id INT
)
BEGIN
SELECT
    c.certificationID,
    c.name,
    c.issuingOrganization,
    sc.certificationDate,
    sc.expiryDate,
    sc.credentialID
FROM certification c
         JOIN studentcertification sc ON c.certificationID = sc.certificationID
WHERE sc.studentID = p_student_id;
END ;;

-- Add certification link for a student (find/create base certification if needed)
DROP PROCEDURE IF EXISTS `sp_add_student_certification`;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_add_student_certification`(
  IN  p_student_id      INT,
  IN  p_cert_name       VARCHAR(100),
  IN  p_issuing_org     VARCHAR(100),
  IN  p_cert_date       DATE,
  IN  p_expiry_date     DATE,         -- Can be NULL
  IN  p_credential_id   VARCHAR(100), -- Can be NULL
  OUT p_certification_id INT
)
BEGIN
    DECLARE v_cert_id INT;

    -- Check if base certification exists
SELECT certificationID INTO v_cert_id
FROM certification
WHERE name = p_cert_name AND issuingOrganization = p_issuing_org;

-- If not, create it
IF v_cert_id IS NULL THEN
INSERT INTO certification (name, issuingOrganization)
VALUES (p_cert_name, p_issuing_org);
SET v_cert_id = LAST_INSERT_ID();
END IF;

-- Link certification to student (replace if exists, or insert)
INSERT INTO studentcertification
    (studentID, certificationID, certificationDate, expiryDate, credentialID)
VALUES
    (p_student_id, v_cert_id, p_cert_date, p_expiry_date, p_credential_id)
    ON DUPLICATE KEY UPDATE
                         certificationDate = VALUES(certificationDate),
                         expiryDate = VALUES(expiryDate),
                         credentialID = VALUES(credentialID);

-- Return the certification ID
SET p_certification_id = v_cert_id;

END ;;

-- Remove a certification link from a student
DROP PROCEDURE IF EXISTS `sp_remove_student_certification`;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_remove_student_certification`(
  IN  p_student_id      INT,
  IN  p_certification_id INT,
  OUT p_rows_deleted    INT
)
BEGIN
DELETE FROM studentcertification
WHERE studentID = p_student_id AND certificationID = p_certification_id;

SELECT ROW_COUNT() INTO p_rows_deleted;
END ;;


-- --- Procedures for Applicant Viewing and Interview Scheduling ---

-- Get multiple students by a comma-separated list of IDs
DROP PROCEDURE IF EXISTS `sp_get_students_by_ids`;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_students_by_ids`(
  IN p_student_ids TEXT -- Use TEXT for potentially long comma-separated list
)
BEGIN
    -- Select student details, joining with user table for name/email
    -- FIND_IN_SET is suitable for comma-separated strings
SELECT
    s.studentID,
    s.collegeID,
    s.major,
    s.gpa,
    s.resume,
    s.isPlaced,
    s.totalApplicationsCount,
    u.firstName, -- Include user details needed by frontend/mapper
    u.lastName,
    u.email
FROM
    student s
        JOIN
    user u ON s.studentID = u.userID
WHERE
    FIND_IN_SET(s.studentID, p_student_ids);
END ;;


-- Get a specific application by job ID and student ID
DROP PROCEDURE IF EXISTS `sp_get_application_by_job_and_student`;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_get_application_by_job_and_student`(
  IN p_job_id INT,
  IN p_student_id INT
)
BEGIN
SELECT
    applicationID,
    studentID,
    jobID,
    applicationDate,
    status
FROM
    application
WHERE
    jobID = p_job_id AND studentID = p_student_id;
END ;;


DELIMITER ; -- Reset delimiter
