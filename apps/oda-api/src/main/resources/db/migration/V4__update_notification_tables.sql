-- Update notifications table to match domain model
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS body TEXT;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS reference_id BIGINT;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS reference_type VARCHAR(100);
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

-- Rename message to body if exists (data migration)
UPDATE notifications SET body = message WHERE body IS NULL AND message IS NOT NULL;

-- Update notification_preferences to add updated_at if missing
ALTER TABLE notification_preferences ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();
