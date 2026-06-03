# Brownfield AI Test App

This repository is a **test app for brownfield AI use cases**.  
Goal: work on an existing codebase, implement changes safely, and keep project context synced across machines via Git.

> **Tool:** These tasks are designed for **GitHub Copilot coding agent** (cloud agent).  
> Start tasks via the Copilot panel or with `gh copilot` — the agent works autonomously in its own environment.

---

## Setup

### 1. Requirements

The following tools must be installed and available in your PATH:

- `npm` (Node.js 20+)
- `mvnw` / `maven` (Java 21)
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
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## Short Description

This app is a multi-user Todo web app with an Angular frontend and a Spring Boot backend (hexagonal architecture), JWT authentication, and PostgreSQL.  
It is intended as a practical brownfield project to test AI-assisted changes, refactorings, and feature extensions in a reproducible way.

---

## Brownfield Tasks

The following tasks are designed to practice AI-assisted development on a real, existing codebase.  
Work through them in order — each task builds on the previous one.

---

### Task 1 — Get Familiar with the Codebase

**Goal:** Understand the application well enough to safely extend it in subsequent tasks.

**Instructions for the agent:**

Read and explore the existing codebase. Specifically:

1. Read `.github/copilot-instructions.md` — this is the main context file
2. Read `.plan/plan.md` — the technical implementation plan
3. Explore the backend package structure under `backend/src/main/java/de/joshuaschnabel/todo/`
4. Explore the frontend structure under `frontend/src/app/`
5. Read `docker-compose.yml` to understand the runtime setup
6. Summarize your findings as a short comment in `.plan/plan.md` under a new section `## Agent Notes`

**Acceptance criteria:**
- [ ] Agent can name the three backend layers and their responsibilities
- [ ] Agent can name all REST endpoints
- [ ] Agent can describe how a new field would flow from domain → application → REST → frontend
- [ ] `.plan/plan.md` contains an `## Agent Notes` section with the agent's findings

---

### Task 2 — Add an Estimated Duration Field to Todos

**Goal:** Extend the existing data model with a new optional field, following the hexagonal architecture strictly.

**New field:** `estimatedMinutes` (integer, optional) — how long the user expects the todo to take in minutes.

**Requirements:**

- The field is optional (nullable)
- It must be stored in the database (add a Flyway migration `V4__add_estimated_minutes_to_todos.sql`)
- It is returned in the `GET /lists/{listId}/todos` response
- It can be set and updated via `POST` and `PUT` on todos
- The frontend form must include an input for this field (number input, min: 1, max: 999)
- No validation error messages are required for this field beyond the min/max constraint on the form

**Architecture rules (must be followed):**
- Add the field to `Todo.java` (domain model)
- Extend `CreateTodoCommand` and `UpdateTodoCommand` (application layer)
- Extend `TodoJpaEntity` and the persistence mapper
- Extend `CreateTodoRequest`, `UpdateTodoRequest`, and `TodoResponse` (REST layer)
- Extend the Angular `Todo` model, `TodoService`, and `TodoFormComponent`

**Acceptance criteria:**
- [ ] Flyway migration `V4` exists and is valid SQL
- [ ] Backend tests pass: `cd backend && ./mvnw test -Dspring.profiles.active=test`
- [ ] Frontend tests pass: `cd frontend && npm test`
- [ ] The field appears in the todo form in the UI
- [ ] The field is returned in the API response

---

### Task 3 — Add a Todo Count Badge to Each List Card

**Goal:** Show how many open todos each list contains, directly on the list card in the sidebar.

**Requirements:**

- Each list card shows a badge with the count of open (`OPEN`) todos in that list
- The count must come from the backend — add a new field `openTodoCount` to the `GET /lists` response
- Do not make one extra HTTP request per list — the count must be computed server-side and included in the list response
- The badge should be visually distinct (e.g., a small pill/chip with a background color)

**Architecture rules:**
- Extend `TodoListResponse` with `openTodoCount: int`
- The persistence adapter must query the count efficiently (one query for all lists, not N+1)
- The Angular `TodoList` model must include `openTodoCount`
- The `ListOverviewComponent` must render the badge

**Acceptance criteria:**
- [ ] `GET /lists` response includes `openTodoCount` per list
- [ ] Backend tests pass
- [ ] Frontend tests pass
- [ ] Badge is visible in the UI on each list card
- [ ] No N+1 query problem (count is not fetched per-list in a loop)
