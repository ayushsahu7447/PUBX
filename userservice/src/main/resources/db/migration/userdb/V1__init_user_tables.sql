CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Topics taxonomy (parent-child)
CREATE TABLE topics (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    parent_id   UUID REFERENCES topics(id) ON DELETE SET NULL,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order  INT NOT NULL DEFAULT 0
);

-- Person profiles (talent)
CREATE TABLE person_profiles (
    id              UUID PRIMARY KEY,
    full_name       VARCHAR(255) NOT NULL,
    username        VARCHAR(100) UNIQUE,
    bio             TEXT,
    city            VARCHAR(100),
    state           VARCHAR(100),
    country         VARCHAR(100) DEFAULT 'India',
    profile_image   VARCHAR(500),
    website_url     VARCHAR(500),
    linkedin_url    VARCHAR(500),
    twitter_url     VARCHAR(500),
    instagram_url   VARCHAR(500),
    youtube_url     VARCHAR(500),
    avg_rating      DECIMAL(3,2) DEFAULT 0.00,
    total_ratings   INT DEFAULT 0,
    is_verified     BOOLEAN DEFAULT FALSE,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Person-Topics join table (max 5 enforced in code)
CREATE TABLE person_topics (
    person_id   UUID NOT NULL REFERENCES person_profiles(id) ON DELETE CASCADE,
    topic_id    UUID NOT NULL REFERENCES topics(id),
    PRIMARY KEY (person_id, topic_id)
);

-- Project profiles (event organizers)
CREATE TABLE project_profiles (
    id              UUID PRIMARY KEY,
    org_name        VARCHAR(255) NOT NULL,
    username        VARCHAR(100) UNIQUE,
    description     TEXT,
    org_type        VARCHAR(50) CHECK (org_type IN ('COLLEGE','COMPANY','HOTEL','NGO','STARTUP','OTHER')),
    city            VARCHAR(100),
    state           VARCHAR(100),
    country         VARCHAR(100) DEFAULT 'India',
    logo_url        VARCHAR(500),
    website_url     VARCHAR(500),
    linkedin_url    VARCHAR(500),
    avg_rating      DECIMAL(3,2) DEFAULT 0.00,
    total_ratings   INT DEFAULT 0,
    is_verified     BOOLEAN DEFAULT FALSE,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Media posts (portfolio — images/reels)
CREATE TABLE media_posts (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id        UUID NOT NULL,
    owner_type      VARCHAR(10) NOT NULL CHECK (owner_type IN ('PERSON','PROJECT')),
    media_type      VARCHAR(10) NOT NULL CHECK (media_type IN ('IMAGE','REEL')),
    media_url       VARCHAR(500) NOT NULL,
    thumbnail_url   VARCHAR(500),
    caption         TEXT,
    duration_secs   INT,
    sort_order      INT DEFAULT 0,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Availability (which dates a Person is free/blocked)
CREATE TABLE availability (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    person_id       UUID NOT NULL REFERENCES person_profiles(id) ON DELETE CASCADE,
    date            DATE NOT NULL,
    is_available    BOOLEAN NOT NULL DEFAULT TRUE,
    note            VARCHAR(255),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(person_id, date)
);

-- Indexes
CREATE INDEX idx_person_topics_person ON person_topics(person_id);
CREATE INDEX idx_person_topics_topic ON person_topics(topic_id);
CREATE INDEX idx_media_posts_owner ON media_posts(owner_id, owner_type);
CREATE INDEX idx_availability_person_date ON availability(person_id, date);
CREATE INDEX idx_person_profiles_city ON person_profiles(city);
CREATE INDEX idx_topics_parent ON topics(parent_id);