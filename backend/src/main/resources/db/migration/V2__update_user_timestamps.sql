-- Update existing records with current timestamp
UPDATE users SET created_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;

-- Make the columns NOT NULL after updating existing records
ALTER TABLE users MODIFY COLUMN created_at DATETIME NOT NULL;
ALTER TABLE users MODIFY COLUMN updated_at DATETIME NOT NULL; 