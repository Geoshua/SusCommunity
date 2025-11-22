-- SusCommunity Database Schema
-- PostgreSQL schema for storing users, posts, and map data

-- =============================================================================
-- USERS TABLE
-- =============================================================================

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,

    -- User role and profile
    role VARCHAR(50) NOT NULL CHECK (role IN ('NEW_MUENCHER', 'OLD_MUENCHER')),
    full_name VARCHAR(255),
    bio TEXT,
    avatar_url TEXT,

    -- Sustainability metrics
    sustainability_score INTEGER NOT NULL DEFAULT 0,
    carbon_footprint_score INTEGER NOT NULL DEFAULT 0,

    -- Account status
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP WITH TIME ZONE,

    -- Location (optional for user's general area)
    location GEOGRAPHY(POINT, 4326),

    CONSTRAINT users_email_check CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Create indexes for users table
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_created_at ON users(created_at DESC);
CREATE INDEX idx_users_location ON users USING GIST(location);

COMMENT ON TABLE users IS 'Stores user accounts for NewMuenchers and OldMuenchers';
COMMENT ON COLUMN users.sustainability_score IS 'Gamification score based on sustainable actions';
COMMENT ON COLUMN users.carbon_footprint_score IS 'Carbon footprint tracking score';


-- =============================================================================
-- POSTS TABLE (Gig & Volunteering Board)
-- =============================================================================

CREATE TABLE IF NOT EXISTS posts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    -- Post content
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    tag VARCHAR(50) NOT NULL CHECK (tag IN ('PET_SITTING', 'TUTORING', 'ELDERLY_COMPANY', 'MOWING', 'MOVING_HELP', 'EVENT', 'VOLUNTEERING', 'OTHER')),

    -- Location data (using PostGIS)
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    address VARCHAR(500),

    -- Post attributes
    due_date TIMESTAMP WITH TIME ZONE NOT NULL,
    female_only BOOLEAN NOT NULL DEFAULT FALSE,

    -- Post status
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),

    -- Metadata
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    views_count INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT posts_title_check CHECK (LENGTH(title) > 0 AND LENGTH(title) <= 200),
    CONSTRAINT posts_description_check CHECK (LENGTH(description) > 0)
);

-- Create indexes for posts table
CREATE INDEX idx_posts_author_id ON posts(author_id);
CREATE INDEX idx_posts_tag ON posts(tag);
CREATE INDEX idx_posts_status ON posts(status);
CREATE INDEX idx_posts_due_date ON posts(due_date);
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
CREATE INDEX idx_posts_location ON posts USING GIST(location);
CREATE INDEX idx_posts_title_trgm ON posts USING GIN(title gin_trgm_ops);
CREATE INDEX idx_posts_description_trgm ON posts USING GIN(description gin_trgm_ops);

COMMENT ON TABLE posts IS 'Posts for the Gig & Volunteering Board where users request help';
COMMENT ON COLUMN posts.location IS 'PostGIS geography point (latitude, longitude)';
COMMENT ON COLUMN posts.female_only IS 'If true, only female volunteers should respond';


-- =============================================================================
-- POST IMAGES TABLE
-- =============================================================================

CREATE TABLE IF NOT EXISTS post_images (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT post_images_url_check CHECK (image_url ~* '^https?://'),
    CONSTRAINT post_images_unique_order UNIQUE(post_id, display_order)
);

CREATE INDEX idx_post_images_post_id ON post_images(post_id);

COMMENT ON TABLE post_images IS 'Images associated with posts (up to 10 per post)';


-- =============================================================================
-- POST RESPONSES TABLE (Volunteers accepting posts)
-- =============================================================================

CREATE TABLE IF NOT EXISTS post_responses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    volunteer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    message TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'COMPLETED')),

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT post_responses_unique_volunteer UNIQUE(post_id, volunteer_id)
);

CREATE INDEX idx_post_responses_post_id ON post_responses(post_id);
CREATE INDEX idx_post_responses_volunteer_id ON post_responses(volunteer_id);
CREATE INDEX idx_post_responses_status ON post_responses(status);

COMMENT ON TABLE post_responses IS 'Volunteers responding to posts';


-- =============================================================================
-- MAP LOCATIONS TABLE (Sustainability Map)
-- =============================================================================

CREATE TABLE IF NOT EXISTS map_locations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    -- Location details
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL CHECK (category IN ('RECYCLING', 'BIKE_RENTAL', 'SUSTAINABLE_SHOP', 'COMMUNITY_EVENT', 'CHARGING_STATION', 'PUBLIC_TRANSPORT', 'OTHER')),

    -- Geographic data
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    address VARCHAR(500),

    -- Contact and details
    website_url TEXT,
    phone VARCHAR(50),
    opening_hours JSONB,

    -- Metadata
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    submitted_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT map_locations_name_check CHECK (LENGTH(name) > 0)
);

CREATE INDEX idx_map_locations_category ON map_locations(category);
CREATE INDEX idx_map_locations_location ON map_locations USING GIST(location);
CREATE INDEX idx_map_locations_is_verified ON map_locations(is_verified);
CREATE INDEX idx_map_locations_name_trgm ON map_locations USING GIN(name gin_trgm_ops);

COMMENT ON TABLE map_locations IS 'Sustainability map showing recycling, bike rentals, sustainable shops, etc.';
COMMENT ON COLUMN map_locations.location IS 'PostGIS geography point for map display';


-- =============================================================================
-- LOCAL REPORTS TABLE (Crowdsourced infrastructure reporting)
-- =============================================================================

CREATE TABLE IF NOT EXISTS local_reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reporter_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    -- Report details
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('BROKEN_INFRASTRUCTURE', 'SAFETY_ISSUE', 'LOCAL_NEWS', 'COMMUNITY_EVENT', 'OTHER')),

    -- Location
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    address VARCHAR(500),

    -- Report status
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED')),
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),

    -- Metadata
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_local_reports_reporter_id ON local_reports(reporter_id);
CREATE INDEX idx_local_reports_category ON local_reports(category);
CREATE INDEX idx_local_reports_status ON local_reports(status);
CREATE INDEX idx_local_reports_priority ON local_reports(priority);
CREATE INDEX idx_local_reports_location ON local_reports USING GIST(location);
CREATE INDEX idx_local_reports_created_at ON local_reports(created_at DESC);

COMMENT ON TABLE local_reports IS 'User-submitted reports of broken infrastructure, local news, etc.';


-- =============================================================================
-- REPORT IMAGES TABLE
-- =============================================================================

CREATE TABLE IF NOT EXISTS report_images (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    report_id UUID NOT NULL REFERENCES local_reports(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT report_images_url_check CHECK (image_url ~* '^https?://'),
    CONSTRAINT report_images_unique_order UNIQUE(report_id, display_order)
);

CREATE INDEX idx_report_images_report_id ON report_images(report_id);

COMMENT ON TABLE report_images IS 'Photos attached to local reports';


-- =============================================================================
-- CARBON ACTIONS TABLE (Carbon Footprint Tracker)
-- =============================================================================

CREATE TABLE IF NOT EXISTS carbon_actions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    -- Action details
    action_type VARCHAR(50) NOT NULL CHECK (action_type IN ('BIKE_RIDE', 'PUBLIC_TRANSPORT', 'RECYCLING', 'LOCAL_SHOPPING', 'VOLUNTEERING', 'PLANT_BASED_MEAL', 'OTHER')),
    description TEXT,

    -- Carbon impact (in kg CO2 saved)
    carbon_saved_kg NUMERIC(10, 2) NOT NULL,

    -- Points awarded
    points_earned INTEGER NOT NULL DEFAULT 0,

    -- Metadata
    action_date DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT carbon_actions_carbon_check CHECK (carbon_saved_kg >= 0),
    CONSTRAINT carbon_actions_points_check CHECK (points_earned >= 0)
);

CREATE INDEX idx_carbon_actions_user_id ON carbon_actions(user_id);
CREATE INDEX idx_carbon_actions_action_type ON carbon_actions(action_type);
CREATE INDEX idx_carbon_actions_action_date ON carbon_actions(action_date DESC);

COMMENT ON TABLE carbon_actions IS 'User sustainable actions for carbon footprint tracking and gamification';


-- =============================================================================
-- TRIGGERS FOR UPDATED_AT TIMESTAMPS
-- =============================================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_posts_updated_at BEFORE UPDATE ON posts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_post_responses_updated_at BEFORE UPDATE ON post_responses
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_map_locations_updated_at BEFORE UPDATE ON map_locations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_local_reports_updated_at BEFORE UPDATE ON local_reports
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
