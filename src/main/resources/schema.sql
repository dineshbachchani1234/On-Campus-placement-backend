
DROP DATABASE IF EXISTS campus_recruitment;
CREATE DATABASE campus_recruitment;
USE campus_recruitment;

CREATE TABLE student (
                         studentID INT AUTO_INCREMENT PRIMARY KEY,
                         firstName VARCHAR(100),
                         lastName VARCHAR(100),
                         email VARCHAR(100) UNIQUE,
                         university VARCHAR(100),
                         gpa DECIMAL(3,2)
);

CREATE TABLE joblisting (
                            jobID INT AUTO_INCREMENT PRIMARY KEY,
                            title VARCHAR(100),
                            description TEXT,
                            salary DECIMAL(10,2),
                            job_type VARCHAR(50)
);

CREATE TABLE application (
                             applicationID INT AUTO_INCREMENT PRIMARY KEY,
                             studentID INT,
                             jobID INT,
                             applicationDate DATE DEFAULT (CURRENT_DATE),
                             status ENUM('PENDING','SHORTLISTED','REJECTED','INTERVIEWED','OFFERED','ACCEPTED','DECLINED') DEFAULT 'PENDING',
                             CONSTRAINT fk_application_student FOREIGN KEY (studentID) REFERENCES student(studentID) ON DELETE CASCADE,
                             CONSTRAINT fk_application_job FOREIGN KEY (jobID) REFERENCES joblisting(jobID) ON DELETE CASCADE
);

CREATE TABLE recruiter (
                           recruiterID INT AUTO_INCREMENT PRIMARY KEY,
                           firstName VARCHAR(100),
                           lastName VARCHAR(100),
                           email VARCHAR(100),
                           position VARCHAR(100)
);

CREATE TABLE company (
                         companyID INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100),
                         industry VARCHAR(100),
                         email VARCHAR(100)
);

CREATE TABLE interview (
                           interviewID INT AUTO_INCREMENT PRIMARY KEY,
                           applicationID INT,
                           interviewDate DATE,
                           feedback TEXT,
                           result VARCHAR(50),
                           CONSTRAINT fk_interview_application FOREIGN KEY (applicationID) REFERENCES application(applicationID) ON DELETE CASCADE
);

CREATE TABLE careerfair (
                            fairID INT AUTO_INCREMENT PRIMARY KEY,
                            title VARCHAR(100),
                            description TEXT,
                            date DATE,
                            location VARCHAR(100)
);
