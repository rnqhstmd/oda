-- Add missing columns to job_postings
ALTER TABLE job_postings ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE job_postings ADD COLUMN IF NOT EXISTS job_type VARCHAR(50);
