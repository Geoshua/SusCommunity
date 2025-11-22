-- Seed data for development and testing
-- This file is only run in development environments

-- Insert sample users (MVP: simplified username-only, no passwords)
INSERT INTO users (username, display_name, role, age, gender, has_pets, pet_types, sustainability_score, green_title, goodwill_points, bio)
VALUES
    ('anna_oldmuencher', 'Anna Schmidt', 'OLD_MUENCHER', 55, 'FEMALE', TRUE, ARRAY['dog'], 250, 'GREEN_WARRIOR', 180, 'Local Münchner, happy to help newcomers! Love sustainable living and have a friendly golden retriever.'),
    ('mark_newmuencher', 'Mark Johnson', 'NEW_MUENCHER', 28, 'MALE', FALSE, ARRAY[]::TEXT[], 50, 'BEGINNER', 10, 'Just moved to Munich from the US. Excited to explore the city and meet new people!'),
    ('lisa_oldmuencher', 'Lisa Weber', 'OLD_MUENCHER', 42, 'FEMALE', TRUE, ARRAY['cat', 'cat'], 320, 'SUSTAINABILITY_HERO', 250, 'Passionate about helping newcomers feel at home. I have two adorable cats and love eco-friendly living.'),
    ('elderly_helper', 'Hans Müller', 'OLD_MUENCHER', 72, 'MALE', FALSE, ARRAY[]::TEXT[], 180, 'ECO_CONSCIOUS', 420, 'Retired teacher, love helping young people settle in Munich. 50 years in this beautiful city!'),
    ('pet_lover_sarah', 'Sarah Chen', 'NEW_MUENCHER', 31, 'FEMALE', TRUE, ARRAY['dog', 'bird'], 90, 'BEGINNER', 25, 'Animal lover seeking pet sitting help when traveling. Happy to help other pet owners too!')
ON CONFLICT DO NOTHING;

-- Insert sample posts
INSERT INTO posts (id, author_id, title, description, tag, location, address, due_date, female_only, status)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'mark_newmuencher', 'Need help moving furniture', 'Moving to a new apartment this weekend. Need 2-3 people to help with furniture.', 'MOVING_HELP', ST_SetSRID(ST_MakePoint(11.5418, 48.1549), 4326), 'Maxvorstadt, Munich', NOW() + INTERVAL '7 days', false, 'OPEN'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'mark_newmuencher', 'Cat sitter needed', 'Going on vacation for 2 weeks. Need someone to feed my cat twice daily.', 'PET_SITTING', ST_SetSRID(ST_MakePoint(11.5754, 48.1371), 4326), 'Haidhausen, Munich', NOW() + INTERVAL '14 days', true, 'OPEN')
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
    ('anna_oldmuencher', 'BIKE_RIDE', 'Biked to work instead of driving', 2.5, 10, CURRENT_DATE),
    ('anna_oldmuencher', 'PUBLIC_TRANSPORT', 'Used U-Bahn instead of car', 1.8, 8, CURRENT_DATE - 1),
    ('mark_newmuencher', 'RECYCLING', 'Recycled glass and paper', 0.5, 5, CURRENT_DATE),
    ('lisa_oldmuencher', 'LOCAL_SHOPPING', 'Shopped at local farmers market', 1.2, 12, CURRENT_DATE)
ON CONFLICT DO NOTHING;

-- Insert sample local reports
INSERT INTO local_reports (reporter_id, title, description, category, location, address, status, priority)
VALUES
    ('anna_oldmuencher', 'Broken bike lane', 'Bike lane has large pothole on Leopoldstraße', 'BROKEN_INFRASTRUCTURE', ST_SetSRID(ST_MakePoint(11.5808, 48.1600), 4326), 'Leopoldstraße, 80802 Munich', 'OPEN', 'HIGH'),
    ('mark_newmuencher', 'Sustainability fair this weekend', 'Community event at Marienplatz promoting sustainable living', 'COMMUNITY_EVENT', ST_SetSRID(ST_MakePoint(11.5755, 48.1372), 4326), 'Marienplatz, 80331 Munich', 'OPEN', 'LOW')
ON CONFLICT DO NOTHING;

-- Update user sustainability scores based on carbon actions
UPDATE users u SET sustainability_score = u.sustainability_score + COALESCE(
    (SELECT SUM(points_earned) FROM carbon_actions WHERE user_id = u.username), 0
);
