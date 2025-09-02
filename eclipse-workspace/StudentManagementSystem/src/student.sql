-- create database (if not exists)
CREATE DATABASE IF NOT EXISTS smartstudent;
USE smartstudent;

-- drop old table while developing
DROP TABLE IF EXISTS students;

CREATE TABLE students (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  roll_no VARCHAR(50) NOT NULL UNIQUE,
  department VARCHAR(100),
  email VARCHAR(150) UNIQUE,
  phone VARCHAR(20) UNIQUE,
  marks INT DEFAULT 0
) ENGINE=InnoDB AUTO_INCREMENT=1;
