-- Drop existing tables to ensure clean initialization in development
DROP TABLE IF EXISTS employee_skill CASCADE;
DROP TABLE IF EXISTS skill CASCADE;
DROP TABLE IF EXISTS allocation CASCADE;
DROP TABLE IF EXISTS project CASCADE;
DROP TABLE IF EXISTS employee CASCADE;

-- Create Employee table
CREATE TABLE employee (
    employee_id BIGSERIAL PRIMARY KEY,
    employee_code VARCHAR(20) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(50),
    department VARCHAR(50)
);

-- Create Project table
CREATE TABLE project (
    project_id BIGSERIAL PRIMARY KEY,
    project_code VARCHAR(20) UNIQUE NOT NULL,
    project_name VARCHAR(200) NOT NULL,
    customer VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- Create Allocation table
CREATE TABLE allocation (
    allocation_id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    allocation_percent INTEGER NOT NULL CHECK (allocation_percent >= 1 AND allocation_percent <= 100),
    role_in_project VARCHAR(100),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_allocation_employee FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE CASCADE,
    CONSTRAINT fk_allocation_project FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
    CONSTRAINT chk_allocation_dates CHECK (start_date <= end_date)
);

-- Create Skill table
CREATE TABLE skill (
    skill_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- Create Employee Skill Join Table
CREATE TABLE employee_skill (
    employee_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    PRIMARY KEY (employee_id, skill_id),
    CONSTRAINT fk_employee_skill_employee FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE CASCADE,
    CONSTRAINT fk_employee_skill_skill FOREIGN KEY (skill_id) REFERENCES skill(skill_id) ON DELETE CASCADE
);

-- Create Indexes for performance optimization
CREATE INDEX idx_allocation_employee ON allocation(employee_id);
CREATE INDEX idx_allocation_project ON allocation(project_id);
CREATE INDEX idx_employee_skill_employee ON employee_skill(employee_id);
CREATE INDEX idx_employee_skill_skill ON employee_skill(skill_id);
