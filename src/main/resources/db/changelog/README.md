# Liquibase Database Changelog

## Overview
This directory contains Liquibase database migrations for the OrderOps application.

## Configuration
- **Liquibase**: Enabled in `application.yaml`
- **Master changelog**: `classpath:db/changelog/db.changelog-master.xml`
- **Migration format**: XML changelog files

## Directory Structure
```
db/changelog/
├── db.changelog-master.xml     # Master changelog file
└── changelog/
    └── 2026-02-09/
        └── 01-create-schema-tables.xml
```

## Adding New Changes
1. Create new XML files in dated directories: `changelog/YYYY-MM-DD/`
2. Include them in `db.changelog-master.xml`
3. Use changeSet format: `<changeSet id="XX" author="author">`

## Database Connection
- **URL**: `jdbc:postgresql://localhost:5432/orderdb`
- **Username**: `postgres`
- **Password**: `postgres`
- **Driver**: `org.postgresql.Driver`

## Current Changes
- `01-create-schema-tables.xml` - Complete database schema with all project tables:
  - `tenant` - Tenant information
  - `rule` - Business rules
  - `webhook_event` - Incoming webhook events
  - `action` - Actions to execute
  - `action_execution` - Action execution tracking
  - `processed_event` - Processed events tracking