# Docker PostgreSQL Setup for SusCommunity

This document provides quick-start instructions for running the PostgreSQL database locally using Docker.

## Quick Start

### 1. Prerequisites

- Docker Desktop installed (Windows/Mac) or Docker Engine (Linux)
- Docker Compose v2.x

### 2. Setup

```bash
# Copy environment template
cp .env.example .env

# Edit .env and set a secure password
# POSTGRES_PASSWORD=your_secure_password
```

### 3. Start Database

```bash
# Start PostgreSQL container
docker-compose up -d postgres

# View logs
docker-compose logs -f postgres
```

### 4. Verify Setup

```bash
# Check running containers
docker ps

# You should see: suscommunity-postgres running on port 5432
```

### 5. Connect to Database

#### Using psql (command line)

```bash
docker exec -it suscommunity-postgres psql -U suscommunity_user -d suscommunity
```

#### Using pgAdmin (Web UI)

```bash
# Start with pgAdmin
docker-compose --profile tools up -d

# Access at: http://localhost:5050
# Email: admin@suscommunity.local
# Password: admin (or what you set in .env)
```

Add server in pgAdmin:
- **Host**: postgres
- **Port**: 5432
- **Database**: suscommunity
- **Username**: suscommunity_user
- **Password**: (from .env)

---

## Database Schema

The database includes tables for:

### Core Tables
- **users** - User accounts (NewMuenchers and OldMuenchers)
- **posts** - Gig & Volunteering Board posts
- **post_images** - Images attached to posts
- **post_responses** - Volunteer responses to posts

### Map & Location Data
- **map_locations** - Sustainability map points (recycling, bike rentals, etc.)
- **local_reports** - User-submitted infrastructure reports
- **report_images** - Photos of reported issues

### Gamification
- **carbon_actions** - User sustainable actions for carbon tracking

### Features
- ✅ PostGIS for geospatial queries
- ✅ Full-text search with pg_trgm
- ✅ UUID primary keys
- ✅ Auto-updating timestamps
- ✅ Proper indexes for performance

---

## Common Commands

### Start/Stop

```bash
# Start
docker-compose up -d postgres

# Stop
docker-compose down

# Stop and remove data
docker-compose down -v
```

### Database Operations

```bash
# Run SQL file
docker exec -i suscommunity-postgres psql -U suscommunity_user -d suscommunity < your_file.sql

# Backup database
docker exec suscommunity-postgres pg_dump -U suscommunity_user suscommunity > backup.sql

# Restore database
docker exec -i suscommunity-postgres psql -U suscommunity_user -d suscommunity < backup.sql

# View active connections
docker exec -it suscommunity-postgres psql -U suscommunity_user -d suscommunity -c "SELECT * FROM pg_stat_activity;"
```

### Logs and Debugging

```bash
# View logs
docker-compose logs postgres

# Follow logs in real-time
docker-compose logs -f postgres

# Shell into container
docker exec -it suscommunity-postgres bash
```

---

## Sample Queries

### Find posts near a location

```sql
-- Find posts within 5km of a point
SELECT
    id,
    title,
    ST_Distance(location, ST_SetSRID(ST_MakePoint(11.5820, 48.1351), 4326)) / 1000 AS distance_km
FROM posts
WHERE ST_DWithin(
    location,
    ST_SetSRID(ST_MakePoint(11.5820, 48.1351), 4326),
    5000  -- 5km in meters
)
ORDER BY distance_km;
```

### Find sustainable shops nearby

```sql
SELECT
    name,
    category,
    address,
    ST_Distance(location, ST_SetSRID(ST_MakePoint(11.5820, 48.1351), 4326)) / 1000 AS distance_km
FROM map_locations
WHERE category = 'SUSTAINABLE_SHOP'
  AND ST_DWithin(location, ST_SetSRID(ST_MakePoint(11.5820, 48.1351), 4326), 3000)
ORDER BY distance_km;
```

### User leaderboard by sustainability score

```sql
SELECT
    username,
    full_name,
    sustainability_score,
    carbon_footprint_score
FROM users
WHERE is_active = true
ORDER BY sustainability_score DESC
LIMIT 10;
```

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `POSTGRES_PASSWORD` | change_me_in_production | Database password |
| `POSTGRES_PORT` | 5432 | PostgreSQL port |
| `PGADMIN_EMAIL` | admin@suscommunity.local | pgAdmin login email |
| `PGADMIN_PASSWORD` | admin | pgAdmin password |
| `PGADMIN_PORT` | 5050 | pgAdmin web UI port |

---

## Troubleshooting

### Port already in use

```bash
# Check what's using port 5432
netstat -an | grep 5432

# Change port in .env
POSTGRES_PORT=5433

# Restart
docker-compose down && docker-compose up -d
```

### Database won't start

```bash
# Check logs
docker-compose logs postgres

# Remove old volume and restart fresh
docker-compose down -v
docker-compose up -d postgres
```

### Cannot connect from application

```bash
# Ensure container is running
docker ps

# Test connection
docker exec -it suscommunity-postgres pg_isready -U suscommunity_user

# Check if port is exposed
docker port suscommunity-postgres
```

---

## Production Deployment

For production deployment to Google Compute Engine, see [DEPLOYMENT.md](./DEPLOYMENT.md).

**Important for Production:**
- Change default passwords
- Use SSL/TLS connections
- Enable connection pooling
- Set up automated backups
- Configure monitoring and alerts
- Use Cloud SQL or managed PostgreSQL for high availability
