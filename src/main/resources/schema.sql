-- Create the 'tasks' table
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    name VARCHAR(256) NOT NULL,
    description TEXT NULL
);

-- Create the 'users' table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(256) NOT NULL UNIQUE,
    password VARCHAR(512) NOT NULL,
    salt VARCHAR(128) NOT NULL,
    role VARCHAR(128) NOT NULL
);

-- Alter the 'tasks' table to include a 'user_id' column
ALTER TABLE tasks
ADD COLUMN user_id BIGINT;

-- Add a foreign key constraint to 'tasks' that references the 'id' column in the 'users' table
ALTER TABLE tasks
ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id);