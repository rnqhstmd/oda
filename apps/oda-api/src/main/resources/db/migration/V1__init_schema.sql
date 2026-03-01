-- Users
CREATE TABLE users (
    id                      BIGSERIAL PRIMARY KEY,
    email                   VARCHAR(255) UNIQUE NOT NULL,
    name                    VARCHAR(100) NOT NULL,
    oauth_provider          VARCHAR(20),
    oauth_id                VARCHAR(255),
    password_hash           VARCHAR(255),
    consent_personal_info   BOOLEAN DEFAULT false,
    consent_sensitive_info  BOOLEAN DEFAULT false,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW()
);

-- User Profiles
CREATE TABLE user_profiles (
    id                          BIGSERIAL PRIMARY KEY,
    user_id                     BIGINT UNIQUE NOT NULL REFERENCES users(id),
    birth_date                  DATE,
    sido                        VARCHAR(50),
    sigungu                     VARCHAR(50),
    personal_income_encrypted   VARCHAR(500),
    household_income_encrypted  VARCHAR(500),
    household_size              INTEGER,
    employment_status           VARCHAR(30),
    target_job_categories       TEXT,
    skills                      TEXT,
    created_at                  TIMESTAMP,
    updated_at                  TIMESTAMP
);

-- Refresh Tokens
CREATE TABLE refresh_tokens (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id),
    token       VARCHAR(500) UNIQUE NOT NULL,
    expires_at  TIMESTAMP NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW()
);

-- Educations
CREATE TABLE educations (
    id              BIGSERIAL PRIMARY KEY,
    user_profile_id BIGINT NOT NULL REFERENCES user_profiles(id),
    school_name     VARCHAR(200),
    major           VARCHAR(200),
    degree          VARCHAR(50),
    graduated       BOOLEAN,
    created_at      TIMESTAMP DEFAULT NOW()
);

-- Certifications
CREATE TABLE certifications (
    id              BIGSERIAL PRIMARY KEY,
    user_profile_id BIGINT NOT NULL REFERENCES user_profiles(id),
    name            VARCHAR(200) NOT NULL,
    issuer          VARCHAR(200),
    acquired_date   DATE,
    created_at      TIMESTAMP DEFAULT NOW()
);

-- Work Experiences
CREATE TABLE work_experiences (
    id              BIGSERIAL PRIMARY KEY,
    user_profile_id BIGINT NOT NULL REFERENCES user_profiles(id),
    company_name    VARCHAR(200),
    position        VARCHAR(200),
    start_date      DATE,
    end_date        DATE,
    is_current      BOOLEAN DEFAULT false,
    created_at      TIMESTAMP DEFAULT NOW()
);

-- Policies
CREATE TABLE policies (
    id                          BIGSERIAL PRIMARY KEY,
    external_id                 VARCHAR(100) UNIQUE,
    title                       VARCHAR(500) NOT NULL,
    summary                     TEXT,
    description                 TEXT,
    category                    VARCHAR(50),
    organization_name           VARCHAR(200),
    min_age                     INTEGER,
    max_age                     INTEGER,
    max_personal_income         BIGINT,
    max_household_income        BIGINT,
    max_median_income_percent   INTEGER,
    required_regions            TEXT,
    target_employment_statuses  TEXT,
    exclude_conditions          TEXT,
    application_start_date      DATE,
    application_end_date        DATE,
    application_url             VARCHAR(1000),
    last_synced_at              TIMESTAMP,
    is_active                   BOOLEAN DEFAULT true,
    created_at                  TIMESTAMP DEFAULT NOW(),
    updated_at                  TIMESTAMP DEFAULT NOW()
);

-- Job Postings
CREATE TABLE job_postings (
    id                  BIGSERIAL PRIMARY KEY,
    external_id         VARCHAR(100) UNIQUE,
    title               VARCHAR(500) NOT NULL,
    company_name        VARCHAR(200),
    location            VARCHAR(200),
    job_source          VARCHAR(50),
    skill_requirements  TEXT,
    education_level     VARCHAR(50),
    min_experience_years INTEGER,
    salary_info         VARCHAR(500),
    deadline            DATE,
    job_url             VARCHAR(1000),
    last_synced_at      TIMESTAMP,
    is_active           BOOLEAN DEFAULT true,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
);

-- Calendar Events
CREATE TABLE calendar_events (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    title           VARCHAR(500) NOT NULL,
    description     TEXT,
    start_date      DATE NOT NULL,
    end_date        DATE,
    start_time      TIME,
    end_time        TIME,
    event_type      VARCHAR(30) NOT NULL,
    source_type     VARCHAR(30),
    source_id       BIGINT,
    action_url      VARCHAR(1000),
    all_day         BOOLEAN DEFAULT true,
    recurrence_rule VARCHAR(500),
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

-- Todos
CREATE TABLE todos (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id),
    calendar_event_id   BIGINT REFERENCES calendar_events(id),
    title               VARCHAR(500) NOT NULL,
    description         TEXT,
    priority            VARCHAR(20) DEFAULT 'MEDIUM',
    status              VARCHAR(20) DEFAULT 'PENDING',
    due_date            DATE,
    completed_at        TIMESTAMP,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
);

-- Notifications
CREATE TABLE notifications (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id),
    type                VARCHAR(50) NOT NULL,
    channel             VARCHAR(30) NOT NULL,
    title               VARCHAR(500),
    message             TEXT,
    is_read             BOOLEAN DEFAULT false,
    related_event_id    BIGINT,
    created_at          TIMESTAMP DEFAULT NOW()
);

-- Notification Preferences
CREATE TABLE notification_preferences (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id),
    notification_type   VARCHAR(50) NOT NULL,
    channel             VARCHAR(30) NOT NULL,
    enabled             BOOLEAN DEFAULT true,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
);

-- Indexes
CREATE UNIQUE INDEX idx_users_oauth ON users(oauth_provider, oauth_id) WHERE oauth_provider IS NOT NULL;
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_policies_category ON policies(category);
CREATE INDEX idx_policies_active ON policies(is_active);
CREATE INDEX idx_job_postings_source ON job_postings(job_source);
CREATE INDEX idx_job_postings_active ON job_postings(is_active);
CREATE INDEX idx_calendar_events_user_date ON calendar_events(user_id, start_date);
CREATE INDEX idx_todos_user_status ON todos(user_id, status);
CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read);
CREATE UNIQUE INDEX idx_notification_prefs ON notification_preferences(user_id, notification_type, channel);
