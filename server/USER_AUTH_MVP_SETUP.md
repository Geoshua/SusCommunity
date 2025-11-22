# User Authentication MVP Setup

## Overview

Simple username-based user system for MVP - no passwords, no authentication required.
Users have rich profiles with identifiable attributes for better matching and community building.

## Changes Made

### 1. User Model (`shared/src/commonMain/kotlin/com/sustech/sus_community/models/User.kt`)

Created a comprehensive User model with:

**Core Identity:**
- `username` - Acts as the unique identifier (primary key)
- `displayName` - Optional display name
- `role` - User role (NEW_MUENCHER or OLD_MUENCHER)
- `bio` - User biography

**Identifiable Attributes:**
- `age` - User's age (helps with elderly care matching)
- `gender` - MALE, FEMALE, NON_BINARY, PREFER_NOT_TO_SAY
- `hasPets` - Boolean flag
- `petTypes` - Array of pet types (e.g., ["dog", "cat", "bird"])

**Sustainability/Goodwill Tracking:**
- `sustainabilityScore` - Gamification score for eco-friendly actions
- `greenTitle` - Badge based on score:
  - BEGINNER (0-99 points)
  - ECO_CONSCIOUS (100-249 points)
  - GREEN_WARRIOR (250-499 points)
  - SUSTAINABILITY_HERO (500-999 points)
  - PLANET_CHAMPION (1000+ points)
- `goodwillPoints` - Points earned by helping others

**Helper Methods:**
- `calculateGreenTitle()` - Auto-calculate title from score
- `isElderly()` - Check if user is 65+ years old

### 2. Database Schema Updates

**Users Table** (`server/database/init/02-create-schema.sql`):
```sql
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(100) PRIMARY KEY,  -- Username is the unique identifier
    display_name VARCHAR(255),
    role VARCHAR(50) NOT NULL DEFAULT 'NEW_MUENCHER',
    sustainability_score INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Posts Table** - Updated to reference username instead of UUID:
```sql
author_id VARCHAR(100) NOT NULL REFERENCES users(username) ON DELETE CASCADE
```

**Other Tables** - Updated all foreign keys referencing users:
- `post_responses.volunteer_id` -> username
- `map_locations.submitted_by` -> username
- `local_reports.reporter_id` -> username
- `carbon_actions.user_id` -> username

### 3. Seed Data Updates (`server/database/init/03-seed-data.sql`)

Created diverse test users with realistic profiles:
- `anna_oldmuencher` - 55F, has dog, GREEN_WARRIOR (250pts)
- `mark_newmuencher` - 28M, no pets, BEGINNER (50pts)
- `lisa_oldmuencher` - 42F, has 2 cats, SUSTAINABILITY_HERO (320pts)
- `elderly_helper` - 72M, no pets, ECO_CONSCIOUS, high goodwill points (420pts)
- `pet_lover_sarah` - 31F, has dog and bird, BEGINNER

All posts now reference usernames instead of UUIDs.

## Next Steps

### Still To Do:

1. **Create UserRepository** - Database operations for users
2. **Create User Endpoints**:
   - `POST /users` - Create new user
   - `GET /users/{username}` - Get user by username
   - `GET /users` - List all users (optional)

3. **Update Post Routes**:
   - Accept username in request header or body
   - Link posts to username instead of hardcoded UUID

4. **Testing**:
   - Test user creation
   - Test post creation with username
   - Verify foreign key relationships

## API Design

### POST /users
```json
{
  "username": "john_doe",
  "displayName": "John Doe",
  "role": "NEW_MUENCHER",
  "age": 35,
  "gender": "MALE",
  "hasPets": true,
  "petTypes": ["dog"],
  "bio": "New to Munich, looking to meet people and explore the city!"
}
```

**Response:**
```json
{
  "user": {
    "username": "john_doe",
    "displayName": "John Doe",
    "role": "NEW_MUENCHER",
    "age": 35,
    "gender": "MALE",
    "hasPets": true,
    "petTypes": ["dog"],
    "sustainabilityScore": 0,
    "greenTitle": "BEGINNER",
    "goodwillPoints": 0,
    "bio": "New to Munich, looking to meet people and explore the city!",
    "createdAt": "2025-11-22T15:00:00Z"
  },
  "message": "User created successfully"
}
```

### POST /posts (updated)
```json
{
  "username": "mark_newmuencher",  // Add this field
  "title": "Need help moving furniture",
  "description": "...",
  "location": { ... },
  "tag": "MOVING_HELP",
  "dueDate": "2025-12-25T14:00:00Z",
  "femaleOnly": false,
  "images": []
}
```

## Database Status

- ✅ Schema updated
- ✅ PostgreSQL restarted with new schema
- ✅ Seed data ready
- ⏳ Need to test database initialization

## No Authentication Required

For this MVP:
- No passwords
- No JWT tokens
- No sessions
- Users simply provide their username
- No uniqueness validation (as requested)

## User Matching Use Cases

The rich user profiles enable smart matching for various scenarios:

### 1. Gender-Specific Posts
- Posts marked `femaleOnly: true` can match against user's `gender` field
- Example: Female-only tutoring sessions, women's support groups

### 2. Pet-Related Matching
- Pet sitting posts can be shown to users with `hasPets: true`
- Match specific `petTypes`: dog walkers see dog-sitting requests
- Pet owners can help each other

### 3. Elderly Care
- Users can filter for helpers willing to assist elderly (age 65+)
- `isElderly()` method identifies users who may need extra support
- Elderly users (`elderly_helper`) can mentor newcomers

### 4. Sustainability Community
- `greenTitle` badges create gamification and recognition
- High-score users (GREEN_WARRIOR, PLANET_CHAMPION) become community leaders
- `goodwillPoints` track helping behavior, encouraging community spirit
- Can match sustainability-focused users for eco-projects

### 5. Age-Appropriate Matching
- Tutoring: match student age ranges with tutors
- Elderly company: prioritize older volunteers
- Youth activities: filter by age groups

## Benefits

- **Better Matches**: Attributes help connect users with compatible helpers
- **Safety**: Gender and age info helps users make informed decisions
- **Community Building**: Green titles and goodwill points encourage positive behavior
- **Personalization**: Rich profiles make the community feel more connected
- **Scalability**: Easy to add more attributes as needs emerge
