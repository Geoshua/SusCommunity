-- Seed data for development and testing
-- This file is only run in development environments

-- Insert sample users
INSERT INTO users (id, email, username, password_hash, role, full_name, bio, sustainability_score, location)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'anna@example.com', 'anna_oldmuencher', '$2a$10$placeholder_hash', 'OLD_MUENCHER', 'Anna Schmidt', 'Local Muenchner, happy to help newcomers!', 250, ST_SetSRID(ST_MakePoint(11.5820, 48.1351), 4326)),
    ('22222222-2222-2222-2222-222222222222', 'mark@example.com', 'mark_newmuencher', '$2a$10$placeholder_hash', 'NEW_MUENCHER', 'Mark Johnson', 'Just moved to Munich, excited to explore!', 50, ST_SetSRID(ST_MakePoint(11.5754, 48.1371), 4326)),
    ('33333333-3333-3333-3333-333333333333', 'lisa@example.com', 'lisa_oldmuencher', '$2a$10$placeholder_hash', 'OLD_MUENCHER', 'Lisa Weber', 'Love helping newcomers feel at home', 320, ST_SetSRID(ST_MakePoint(11.5418, 48.1549), 4326))
ON CONFLICT DO NOTHING;

-- Insert sample posts
INSERT INTO posts (id, author_id, title, description, tag, location, address, due_date, female_only, status)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222', 'Need help moving furniture', 'Moving to a new apartment this weekend. Need 2-3 people to help with furniture.', 'MOVING_HELP', ST_SetSRID(ST_MakePoint(11.5418, 48.1549), 4326), 'Maxvorstadt, Munich', NOW() + INTERVAL '7 days', false, 'OPEN'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'Cat sitter needed', 'Going on vacation for 2 weeks. Need someone to feed my cat twice daily.', 'PET_SITTING', ST_SetSRID(ST_MakePoint(11.5754, 48.1371), 4326), 'Haidhausen, Munich', NOW() + INTERVAL '14 days', true, 'OPEN')
ON CONFLICT DO NOTHING;

-- Insert sample post images
INSERT INTO post_images (post_id, image_url, display_order)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c', 0),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba', 0)
ON CONFLICT DO NOTHING;

-- Insert sample map locations
INSERT INTO map_locations (name, description, category, location, address, is_verified)
VALUES
    ('Wertstoffhof Giesing', 'Recycling center for glass, paper, and electronics', 'RECYCLING', ST_SetSRID(ST_MakePoint(11.5761, 48.1078), 4326), 'Tegernseer Landstraße 186, 81539 Munich', true),
    ('MVG Bike Station Marienplatz', 'Bike rental station in city center', 'BIKE_RENTAL', ST_SetSRID(ST_MakePoint(11.5755, 48.1372), 4326), 'Marienplatz, 80331 Munich', true),
    ('Bio Company Schwabing', 'Organic and sustainable grocery store', 'SUSTAINABLE_SHOP', ST_SetSRID(ST_MakePoint(11.5808, 48.1642), 4326), 'Leopoldstraße 82, 80802 Munich', true),
    ('Charging Station Sendlinger Tor', 'Electric vehicle charging station', 'CHARGING_STATION', ST_SetSRID(ST_MakePoint(11.5666, 48.1341), 4326), 'Sendlinger-Tor-Platz, 80336 Munich', true)
ON CONFLICT DO NOTHING;

-- Insert sample carbon actions
INSERT INTO carbon_actions (user_id, action_type, description, carbon_saved_kg, points_earned, action_date)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'BIKE_RIDE', 'Biked to work instead of driving', 2.5, 10, CURRENT_DATE),
    ('11111111-1111-1111-1111-111111111111', 'PUBLIC_TRANSPORT', 'Used U-Bahn instead of car', 1.8, 8, CURRENT_DATE - 1),
    ('22222222-2222-2222-2222-222222222222', 'RECYCLING', 'Recycled glass and paper', 0.5, 5, CURRENT_DATE),
    ('33333333-3333-3333-3333-333333333333', 'LOCAL_SHOPPING', 'Shopped at local farmers market', 1.2, 12, CURRENT_DATE)
ON CONFLICT DO NOTHING;

-- Insert sample local reports
INSERT INTO local_reports (reporter_id, title, description, category, location, address, status, priority)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Broken bike lane', 'Bike lane has large pothole on Leopoldstraße', 'BROKEN_INFRASTRUCTURE', ST_SetSRID(ST_MakePoint(11.5808, 48.1600), 4326), 'Leopoldstraße, 80802 Munich', 'OPEN', 'HIGH'),
    ('22222222-2222-2222-2222-222222222222', 'Sustainability fair this weekend', 'Community event at Marienplatz promoting sustainable living', 'COMMUNITY_EVENT', ST_SetSRID(ST_MakePoint(11.5755, 48.1372), 4326), 'Marienplatz, 80331 Munich', 'OPEN', 'LOW')
ON CONFLICT DO NOTHING;

-- Update user sustainability scores based on carbon actions
UPDATE users u SET sustainability_score = u.sustainability_score + COALESCE(
    (SELECT SUM(points_earned) FROM carbon_actions WHERE user_id = u.id), 0
);
