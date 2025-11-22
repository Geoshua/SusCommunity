-- Enable PostGIS extension for geospatial data
-- This is essential for storing and querying location data efficiently

CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable pg_trgm for full-text search
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Enable btree_gin for better indexing
CREATE EXTENSION IF NOT EXISTS btree_gin;

COMMENT ON EXTENSION postgis IS 'PostGIS geometry and geography spatial types and functions';
COMMENT ON EXTENSION "uuid-ossp" IS 'UUID generation functions';
COMMENT ON EXTENSION pg_trgm IS 'Trigram matching for full-text search';
