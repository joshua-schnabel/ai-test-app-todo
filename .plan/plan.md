# Technical Implementation Plan - Todo Web App v1.0

## 1. Target Architecture

The solution is split into two deployable applications:

- **Frontend**: Angular SPA (mobile-first, responsive from 360 px)
- **Backend**: Spring Boot REST API

Data is persisted in PostgreSQL, with H2 for test runs.

Architecture style:

- Backend: **Hexagonal architecture** (Domain, Application, Infrastructure)
- Frontend: **Feature-first modular structure** with shared core services

---

## 2. Backend Structure (Hexagonal)

### Layers

1. **Domain layer**
   - Business entities and rules
   - No framework dependencies
2. **Application layer**
   - Use case interfaces
   - Command/query models
   - Ports for outbound dependencies
3. **Infrastructure layer (`infrastruktur`)**
   - REST controllers
   - JPA persistence adapters
   - Security, configuration, OpenAPI

### Dependency rule

Only inward dependencies are allowed:

`infrastruktur -> application -> domain`

---

## 3. Binding Backend Package Structure

Base package: `de.joshuaschnabel.todo`

```
de.joshuaschnabel.todo
|- domain
|  |- model
|  |- valueobject
|  `- exception
|- application
|  |- usecase
|  |- port/in
|  |- port/out
|  |- command
|  |- query
|  |- service
|  `- exception
`- infrastruktur
   |- presentation/rest
   |  |- controller
   |  |- request
   |  |- response
   |  |- mapper
   |  `- error
   |- persistence/db
   |  |- entity
   |  |- repository
   |  |- adapter
   |  `- mapper
   |- security
   |  |- config
   |  |- filter
   |  |- jwt
   |  `- adapter
   `- config
```

---

## 4. Frontend Structure (Angular)

```
frontend/src/app/
|- core/
|  |- auth/
|  |- guards/
|  |- interceptors/
|  |- models/
|  `- services/
|- features/
|  |- auth/
|  |  |- login/
|  |  `- register/
|  |- lists/
|  |  |- list-overview/
|  |  `- list-form/
|  `- todos/
|     |- todo-list-view/
|     |- todo-item/
|     |- todo-form/
|     |- todo-filter/
|     `- todo-sort/
`- shared/
   `- components/
      |- empty-state/
      |- loading-spinner/
      `- error-message/
```

Frontend principles:

- Standalone components
- Reactive forms
- JWT interceptor
- Route guard for authenticated areas

---

## 5. Data Model

### User

- `id` (UUID)
- `email` (unique)
- `password_hash`
- `created_at`
- `updated_at`

### TodoList

- `id` (UUID)
- `owner_id` (FK -> User)
- `name` (required, max 80)
- `created_at`
- `updated_at`

### Todo

- `id` (UUID)
- `list_id` (FK -> TodoList)
- `owner_id` (FK -> User, redundant for quick authorization checks)
- `title` (required, max 120)
- `description` (optional, max 1000)
- `status` (`OPEN` | `DONE`)
- `priority` (`LOW` | `MEDIUM` | `HIGH`)
- `due_date` (optional)
- `created_at`
- `updated_at`

---

## 6. REST API Design

Base path: `/api`

### Auth

- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/logout`
- `GET /auth/me`

### Lists

- `GET /lists`
- `POST /lists`
- `PUT /lists/{listId}`
- `DELETE /lists/{listId}`

### Todos

- `GET /lists/{listId}/todos`
- `POST /lists/{listId}/todos`
- `PUT /lists/{listId}/todos/{todoId}`
- `DELETE /lists/{listId}/todos/{todoId}`
- `PATCH /lists/{listId}/todos/{todoId}/complete`
- `PATCH /lists/{listId}/todos/{todoId}/reopen`

### Todo filtering and sorting

- `status=open|done`
- `due=today|overdue`
- `sort=dueDate,asc|priority,desc|createdAt,desc|status,asc`

---

## 7. Authentication and Authorization Concept

Authentication:

- Email + password login
- Passwords hashed with BCrypt
- JWT (HS256) returned after login/register

Authorization:

- Every protected endpoint requires valid JWT
- Current user ID is extracted from JWT
- Access is always constrained to resources owned by current user
- Ownership checks on both list and todo operations

Logout:

- Client-side token removal in v1.0 (no token blacklist)

---

## 8. Database and Migration Concept

Production:

- PostgreSQL 16
- Flyway for schema migrations
- `ddl-auto=validate`

Testing:

- H2 in-memory DB
- Test profile with dedicated config

Initial migrations:

1. `V1__create_users_table.sql`
2. `V2__create_todo_lists_table.sql`
3. `V3__create_todos_table.sql`

---

## 9. Docker Concept

`docker-compose.yml` defines:

1. `postgres` service
2. `backend` service
3. `frontend` service

Ports:

- Frontend: `4200`
- Backend: `8080`
- DB: `5432`

Configuration via environment variables (`.env`):

- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MS`

---

## 10. Test Strategy

Backend:

- Unit tests for application services and domain rules
- Integration tests for REST endpoints
- Security and authorization tests
- Coverage report via JaCoCo

Frontend:

- Unit/component tests with Vitest
- Service and guard tests
- Coverage report via V8 coverage provider

End-to-end:

- Optional for v1.0 baseline; can be added in later iterations

---

## 11. Implementation Steps

1. Create project skeleton and base tooling
2. Implement backend domain and application layers
3. Implement persistence adapters and migrations
4. Implement JWT security and user auth flows
5. Implement REST controllers and validation handling
6. Implement Angular core (auth, guards, interceptors, services)
7. Implement Angular features (auth, lists, todos, filters, sorting)
8. Add mobile-first styling and UX states
9. Add and configure tests + coverage reports
10. Final integration via Docker Compose

---

## 12. Known Risks and Open Points

Risks:

- Token handling edge cases in frontend state
- Date and timezone behavior for "today" and "overdue" filters
- Validation consistency between frontend and backend

Open points:

- No token refresh flow in v1.0
- No collaboration/shared lists in v1.0
- No password reset in v1.0

---

## Repository Structure (Current)

```
Projekt K/                        <- Git root
|- .github/
|  `- copilot-instructions.md
|- .plan/
|  `- plan.md
|- backend/
|  |- src/
|  |- Dockerfile
|  `- pom.xml
|- frontend/
|  |- src/
|  |- Dockerfile
|  |- nginx.conf
|  `- package.json
|- docker-compose.yml
|- .gitignore
`- README.md
```
