# Deployment Guide - Google Compute Engine

This guide covers deploying the SusCommunity PostgreSQL database to Google Compute Engine (GCE).

## Prerequisites

- Google Cloud Platform account with billing enabled
- `gcloud` CLI installed and authenticated
- Docker and Docker Compose installed locally (for testing)
- GCP credits allocated

## Local Development Setup

### 1. Create Environment File

```bash
cp .env.example .env
```

Edit `.env` and update the values:
```bash
POSTGRES_PASSWORD=your_secure_password_here
DATABASE_URL=jdbc:postgresql://localhost:5432/suscommunity
DATABASE_USER=suscommunity_user
DATABASE_PASSWORD=your_secure_password_here
```

### 2. Start PostgreSQL Container Locally

```bash
# Start PostgreSQL only
docker-compose up -d postgres

# Or start with pgAdmin for management
docker-compose --profile tools up -d
```

### 3. Verify Database

```bash
# Check if container is running
docker ps

# Access PostgreSQL shell
docker exec -it suscommunity-postgres psql -U suscommunity_user -d suscommunity

# Or access pgAdmin at http://localhost:5050
```

### 4. Stop Containers

```bash
docker-compose down

# To remove volumes as well
docker-compose down -v
```

---

## Google Compute Engine Deployment

### Option 1: VM with Docker (Recommended)

#### 1. Create a GCE VM Instance

```bash
# Set your project ID
export GCP_PROJECT_ID="your-project-id"
export GCP_REGION="us-central1"
export GCP_ZONE="us-central1-a"

gcloud config set project $GCP_PROJECT_ID

# Create a VM with Container-Optimized OS
gcloud compute instances create suscommunity-db \
  --project=$GCP_PROJECT_ID \
  --zone=$GCP_ZONE \
  --machine-type=e2-medium \
  --image-family=cos-stable \
  --image-project=cos-cloud \
  --boot-disk-size=50GB \
  --boot-disk-type=pd-standard \
  --tags=postgres-server \
  --metadata-from-file=user-data=deploy/cloud-init.yml
```

#### 2. Configure Firewall Rules

```bash
# Allow PostgreSQL access (be restrictive in production!)
gcloud compute firewall-rules create allow-postgres \
  --project=$GCP_PROJECT_ID \
  --direction=INGRESS \
  --priority=1000 \
  --network=default \
  --action=ALLOW \
  --rules=tcp:5432 \
  --source-ranges=0.0.0.0/0 \
  --target-tags=postgres-server

# For production, replace 0.0.0.0/0 with your server's IP range
```

#### 3. SSH into the VM and Setup Docker

```bash
# SSH into the instance
gcloud compute ssh suscommunity-db --zone=$GCP_ZONE

# On the VM, install Docker Compose
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -SL https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-linux-x86_64 -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

# Clone your repository or upload docker-compose.yml
# For now, create the necessary files manually
```

#### 4. Upload Configuration Files

From your local machine:

```bash
# Upload docker-compose.yml
gcloud compute scp docker-compose.yml suscommunity-db:~ --zone=$GCP_ZONE

# Upload environment file (never commit this!)
gcloud compute scp .env suscommunity-db:~ --zone=$GCP_ZONE

# Upload initialization scripts
gcloud compute scp --recurse server/database/init/ suscommunity-db:~/server/database/init/ --zone=$GCP_ZONE
```

#### 5. Start the Database

On the VM:

```bash
# Start PostgreSQL
docker compose up -d postgres

# Check logs
docker compose logs -f postgres

# Verify it's running
docker ps
```

#### 6. Get the External IP

```bash
# Get the external IP address
gcloud compute instances describe suscommunity-db \
  --zone=$GCP_ZONE \
  --format='get(networkInterfaces[0].accessConfigs[0].natIP)'
```

Update your application's `DATABASE_URL` to use this IP:
```
jdbc:postgresql://<EXTERNAL_IP>:5432/suscommunity
```

---

### Option 2: Cloud SQL (Managed PostgreSQL)

For production, Cloud SQL is recommended as it provides managed backups, automatic updates, and better security.

#### 1. Create Cloud SQL Instance

```bash
gcloud sql instances create suscommunity-postgres \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=$GCP_REGION \
  --storage-type=SSD \
  --storage-size=10GB \
  --storage-auto-increase \
  --backup \
  --backup-start-time=03:00
```

#### 2. Create Database

```bash
gcloud sql databases create suscommunity \
  --instance=suscommunity-postgres
```

#### 3. Create User

```bash
gcloud sql users create suscommunity_user \
  --instance=suscommunity-postgres \
  --password=your_secure_password
```

#### 4. Enable Public IP (or use Cloud SQL Proxy for better security)

```bash
# Get the connection name
gcloud sql instances describe suscommunity-postgres \
  --format='get(connectionName)'

# Allow your IP
gcloud sql instances patch suscommunity-postgres \
  --authorized-networks=YOUR_IP_ADDRESS
```

#### 5. Initialize Schema

Connect using `psql` or Cloud Shell:

```bash
gcloud sql connect suscommunity-postgres --user=suscommunity_user --database=suscommunity
```

Then run the initialization scripts:
```sql
\i /path/to/01-create-extensions.sql
\i /path/to/02-create-schema.sql
\i /path/to/03-seed-data.sql  -- Only for development
```

---

## Security Best Practices

### 1. Use Cloud SQL Proxy (Production)

Instead of exposing PostgreSQL publicly:

```bash
# Download Cloud SQL Proxy
wget https://dl.google.com/cloudsql/cloud_sql_proxy.linux.amd64 -O cloud_sql_proxy
chmod +x cloud_sql_proxy

# Run proxy
./cloud_sql_proxy -instances=PROJECT:REGION:INSTANCE=tcp:5432
```

### 2. Secret Manager

Store database passwords in Google Secret Manager:

```bash
# Create secret
echo -n "your_password" | gcloud secrets create postgres-password --data-file=-

# Grant access to your service account
gcloud secrets add-iam-policy-binding postgres-password \
  --member="serviceAccount:YOUR_SERVICE_ACCOUNT" \
  --role="roles/secretmanager.secretAccessor"
```

### 3. Enable PostGIS Extension

After connecting to your Cloud SQL instance:

```sql
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
```

---

## Monitoring and Maintenance

### Database Backups

For VM-based deployment:

```bash
# Create backup script
cat > backup-db.sh << 'EOF'
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups"
mkdir -p $BACKUP_DIR

docker exec suscommunity-postgres pg_dump -U suscommunity_user suscommunity > \
  $BACKUP_DIR/suscommunity_$DATE.sql

# Upload to Cloud Storage
gsutil cp $BACKUP_DIR/suscommunity_$DATE.sql gs://your-backup-bucket/
EOF

chmod +x backup-db.sh

# Add to crontab (daily backups at 2 AM)
crontab -e
0 2 * * * /path/to/backup-db.sh
```

### Monitoring

Enable Stackdriver monitoring:

```bash
gcloud compute instances update suscommunity-db \
  --zone=$GCP_ZONE \
  --metadata=google-monitoring-enable=true
```

---

## Cost Estimation

### VM-based (e2-medium)
- Compute: ~$24/month
- Storage (50GB): ~$2/month
- Network egress: varies
- **Total: ~$26/month**

### Cloud SQL (db-f1-micro)
- Instance: ~$7/month
- Storage (10GB): ~$1.50/month
- Backups: minimal
- **Total: ~$9/month**

---

## Troubleshooting

### Cannot connect to database

```bash
# Check if PostgreSQL is running
docker ps

# Check logs
docker compose logs postgres

# Test connection from VM
docker exec -it suscommunity-postgres psql -U suscommunity_user -d suscommunity

# Check firewall rules
gcloud compute firewall-rules list
```

### Out of disk space

```bash
# Check disk usage
df -h

# Resize disk
gcloud compute disks resize suscommunity-db \
  --size=100GB \
  --zone=$GCP_ZONE

# Then resize partition from VM
sudo growpart /dev/sda 1
sudo resize2fs /dev/sda1
```

---

## Connecting from Ktor Server

Update your `application.conf` or environment variables:

```hocon
database {
    url = "jdbc:postgresql://EXTERNAL_IP:5432/suscommunity"
    driver = "org.postgresql.Driver"
    user = "suscommunity_user"
    password = ${DATABASE_PASSWORD}
    maximumPoolSize = 10
}
```

Or use environment variables:
```bash
DATABASE_URL=jdbc:postgresql://EXTERNAL_IP:5432/suscommunity
DATABASE_USER=suscommunity_user
DATABASE_PASSWORD=your_password
```
