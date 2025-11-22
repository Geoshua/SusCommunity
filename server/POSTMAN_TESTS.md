# Postman API Tests - UPDATE & DELETE

Base URL: `http://localhost:8081`

## 1. UPDATE Post - PUT /posts/{id}

### Endpoint
```
PUT http://localhost:8081/posts/{id}
```
Replace `{id}` with an actual post ID from your database.

### Headers
```
Content-Type: application/json
```

### Test JSON - Update German Tutor Post
```json
{
  "title": "UPDATED: German Language Tutor Needed for B2 Exam",
  "description": "I'm preparing for my B2 German language exam in 3 months and need an experienced tutor. Looking for someone who can meet 2-3 times per week near Marienplatz. Updated requirements!",
  "location": {
    "latitude": 48.1351,
    "longitude": 11.5820,
    "address": "Marienplatz, Munich, Germany"
  },
  "tag": "TUTORING",
  "dueDate": "2025-12-15T18:00:00Z",
  "femaleOnly": false,
  "images": [
    "https://example.com/images/german-textbook.jpg",
    "https://example.com/images/study-materials.jpg",
    "https://example.com/images/b2-exam-prep.jpg"
  ]
}
```

### Test JSON - Update Moving Help Post
```json
{
  "title": "URGENT: Moving Help Needed This Weekend",
  "description": "Moving to a new apartment in Schwabing this Saturday. Need 2-3 strong people to help with furniture. Pizza and drinks provided! Updated time: starting at 9 AM.",
  "location": {
    "latitude": 48.1642,
    "longitude": 11.5820,
    "address": "Schwabing, Munich, Germany"
  },
  "tag": "MOVING_HELP",
  "dueDate": "2025-11-25T09:00:00Z",
  "femaleOnly": false,
  "images": [
    "https://example.com/images/apartment-layout.jpg"
  ]
}
```

### Test JSON - Update Pet Sitting Post
```json
{
  "title": "Cat Sitter Needed - Updated Dates",
  "description": "Going on vacation and need someone to feed my 2 cats and clean their litter box. Dates have changed to Dec 20-27. Very friendly cats, easy to care for.",
  "location": {
    "latitude": 48.1466,
    "longitude": 11.5618,
    "address": "Sendling, Munich, Germany"
  },
  "tag": "PET_SITTING",
  "dueDate": "2025-12-20T10:00:00Z",
  "femaleOnly": false,
  "images": [
    "https://example.com/images/cat1.jpg",
    "https://example.com/images/cat2.jpg"
  ]
}
```

### Test JSON - Update Elderly Company Post
```json
{
  "title": "Updated: Weekly Visits for My Grandmother",
  "description": "Looking for a kind person to visit my grandmother (82 years old) twice a week for conversation and light activities. She speaks German and enjoys playing cards. New schedule: Tuesdays and Thursdays, 2-4 PM.",
  "location": {
    "latitude": 48.1549,
    "longitude": 11.5418,
    "address": "Neuhausen, Munich, Germany"
  },
  "tag": "ELDERLY_COMPANY",
  "dueDate": "2025-12-01T14:00:00Z",
  "femaleOnly": true,
  "images": []
}
```

### Expected Response (200 OK)
```json
{
  "post": {
    "id": "47574133-1cdf-4bbf-ad8f-9e4b6b42decc",
    "title": "UPDATED: German Language Tutor Needed for B2 Exam",
    "description": "I'm preparing for my B2 German language exam in 3 months...",
    "location": {
      "latitude": 48.1351,
      "longitude": 11.582,
      "address": "Marienplatz, Munich, Germany"
    },
    "tag": "TUTORING",
    "dueDate": "2025-12-15T18:00:00Z",
    "femaleOnly": false,
    "images": [
      "https://example.com/images/german-textbook.jpg",
      "https://example.com/images/study-materials.jpg",
      "https://example.com/images/b2-exam-prep.jpg"
    ],
    "authorId": "22222222-2222-2222-2222-222222222222",
    "createdAt": "2025-11-22T12:34:56Z",
    "status": "OPEN"
  }
}
```

### Error Responses

**404 Not Found** - Post doesn't exist:
```json
{
  "error": "Post not found"
}
```

**400 Bad Request** - Invalid data:
```json
{
  "error": "Title cannot be blank"
}
```

---

## 2. DELETE Post - DELETE /posts/{id}

### Endpoint
```
DELETE http://localhost:8081/posts/{id}
```
Replace `{id}` with an actual post ID to delete.

### Headers
```
None required (no body needed for DELETE)
```

### Example URLs
```
DELETE http://localhost:8081/posts/47574133-1cdf-4bbf-ad8f-9e4b6b42decc
DELETE http://localhost:8081/posts/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa
DELETE http://localhost:8081/posts/bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb
```

### Expected Response (200 OK)
```json
{
  "message": "Post deleted successfully",
  "id": "47574133-1cdf-4bbf-ad8f-9e4b6b42decc"
}
```

### Error Responses

**404 Not Found** - Post doesn't exist:
```json
{
  "error": "Post not found"
}
```

**400 Bad Request** - No ID provided:
```json
{
  "error": "Post ID is required"
}
```

---

## 3. GET Post by ID (to verify UPDATE/DELETE)

### Endpoint
```
GET http://localhost:8081/posts/{id}
```

### Expected Response (200 OK)
```json
{
  "id": "47574133-1cdf-4bbf-ad8f-9e4b6b42decc",
  "title": "Looking for German language tutor",
  "description": "...",
  "location": { ... },
  "tag": "TUTORING",
  "dueDate": "2025-12-15T18:00:00Z",
  "femaleOnly": false,
  "images": [...],
  "authorId": "22222222-2222-2222-2222-222222222222",
  "createdAt": "2025-11-22T12:34:56Z",
  "status": "OPEN"
}
```

### After DELETE - Expected Response (404 Not Found)
```json
{
  "error": "Post not found"
}
```

---

## 4. GET All Posts (to see current data)

### Endpoint
```
GET http://localhost:8081/posts
```

### Expected Response (200 OK)
```json
{
  "posts": [
    {
      "id": "47574133-1cdf-4bbf-ad8f-9e4b6b42decc",
      "title": "Looking for German language tutor",
      ...
    },
    {
      "id": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
      "title": "Dog walker needed for daily walks",
      ...
    }
  ],
  "count": 2
}
```

---

## Testing Workflow

### Workflow 1: Create, Update, Verify, Delete
```
1. POST /posts                          → Create new post, get ID
2. GET /posts/{id}                      → Verify creation
3. PUT /posts/{id}                      → Update the post
4. GET /posts/{id}                      → Verify update applied
5. DELETE /posts/{id}                   → Delete the post
6. GET /posts/{id}                      → Verify deletion (should 404)
```

### Workflow 2: Update Existing Post
```
1. GET /posts                           → Get list of posts, pick an ID
2. GET /posts/{id}                      → Get current data
3. PUT /posts/{id}                      → Update with modified JSON
4. GET /posts/{id}                      → Verify changes
```

---

## Available Post Tags
- `PET_SITTING`
- `TUTORING`
- `ELDERLY_COMPANY`
- `MOWING`
- `MOVING_HELP`

---

## Notes

1. **authorId, createdAt, status are preserved during UPDATE**
   - You cannot change who created the post
   - Creation timestamp stays the same
   - Status remains unchanged (will add status update endpoint later)

2. **Images are completely replaced during UPDATE**
   - The entire images array is replaced with your new array
   - To remove all images, send empty array: `"images": []`

3. **Location coordinates must be valid**
   - Latitude: -90 to 90
   - Longitude: -180 to 180

4. **Due date must be in ISO 8601 format**
   - Format: `YYYY-MM-DDTHH:MM:SSZ`
   - Example: `2025-12-25T14:30:00Z`

5. **Validation limits**
   - Title: max 200 characters
   - Description: max 2000 characters
   - Images: max 10 URLs
   - Image URLs must start with `http://` or `https://`
