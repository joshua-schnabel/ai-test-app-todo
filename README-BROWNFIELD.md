# Brownfield AI Test App

This repository is a **test app for brownfield AI use cases**.  
Goal: work on an existing codebase, implement changes safely, and keep project context synced across machines via Git.

## Setup

### 1. Requirements

The following tools must be installed and available in your PATH:

- `npm`
- `maven` (or use project wrapper `mvnw`)
- `docker` (including Docker Compose)

### 2. Reset memory files (optional for a clean test run)

If you want to start with neutral context, delete these files before the next run:

- `.github/copilot-instructions.md`
- `.plan/plan.md`

Example (PowerShell):

```powershell
Remove-Item ".github\copilot-instructions.md" -ErrorAction SilentlyContinue
Remove-Item ".plan\plan.md" -ErrorAction SilentlyContinue
```

### 3. Start the project

```powershell
docker compose up --build
```

After startup:

- Frontend: `http://localhost:4200`
- Backend: `http://localhost:8080/api`

## Short Description

This app is a multi-user Todo web app with an Angular frontend and a Spring Boot backend (hexagonal architecture), JWT authentication, and PostgreSQL.  
It is intended as a practical brownfield project to test AI-assisted changes, refactorings, and feature extensions in a reproducible way.
