CREATE DATABASE IF NOT EXISTS on_campus_recruitment;
USE on_campus_recruitment;

CREATE TABLE student (
                         student_id INT AUTO_INCREMENT PRIMARY KEY,
                         first_name VARCHAR(100) NOT NULL,
                         last_name VARCHAR(100) NOT NULL,
                         email VARCHAR(100) NOT NULL UNIQUE,
                         university VARCHAR(100),
                         gpa DECIMAL(3,2)
);
