# Copilot Instructions - Todo App

This file provides full project context for GitHub Copilot.  
It is loaded automatically on any machine where this repository is checked out.

---

## Project Overview

**Todo Web App Version 1.0** - multi-user app for managing personal todo lists.

- Multiple users with strict data isolation (users can only access their own lists and todos)
- Register, login, and logout via JWT
- Todo lists and todos: create, update, delete, complete, reopen
- Filtering and sorting of todos
- Mobile-first UI (starting at 360 px)

---

## Technology Stack

| Layer | Technology |
|---|---|
| Frontend | Angular 21, Standalone Components, SCSS, Reactive Forms |
| Backend | Spring Boot 3.5, Java 21 |
| Backend Architecture | Hexagonal Architecture (Ports & Adapters) |
| Authentication | JWT (HS256, 24h), BCrypt |
| Production DB | PostgreSQL 16 |
| Test DB | H2 In-Memory (`test` profile) |
| DB Migration | Flyway |
| Runtime | Docker + Docker Compose |
| API Docs | OpenAPI / Swagger (`/swagger-ui.html`) |

---

## Repository Structure

```
Projekt K/                        <- Git root
|- .github/
|  `- copilot-instructions.md    <- this file
|- .plan/
|  `- plan.md                    <- technical implementation plan
|- backend/                      <- Spring Boot backend
|  |- src/main/java/de/joshuaschnabel/todo/
|  |- src/main/resources/
|  |  |- application.properties
|  |  `- db/migration/           <- Flyway scripts (V1-V3)
|  |- src/test/
|  |- Dockerfile
|  `- pom.xml
|- frontend/                     <- Angular frontend
|  |- src/app/
|  |  |- core/                   <- services, guards, interceptors, models
|  |  |- features/               <- auth/, lists/, todos/
|  |  `- shared/                 <- reusable components
|  |- Dockerfile
|  |- nginx.conf
|  |- proxy.conf.json
|  `- package.json
|- docker-compose.yml
|- .gitignore
`- README.md
```

---

## Backend Package Structure

Base package: `de.joshuaschnabel.todo`

```
de.joshuaschnabel.todo
|- domain                         <- core domain, no Spring, no JPA
|  |- model/                      User, TodoList, Todo
|  |- valueobject/                EmailAddress, TodoTitle, TodoListName, TodoStatus, TodoPriority
|  `- exception/                  DomainException, ValidationException
|
|- application                    <- use cases and ports
|  |- usecase/                    interfaces (RegisterUserUseCase, CreateTodoUseCase, ...)
|  |- port/
|  |  |- in/                      use case interfaces
|  |  `- out/                     persistence/security ports
|  |- command/                    RegisterUserCommand, CreateTodoCommand, ...
|  |- query/                      GetTodosQuery
|  |- service/                    application service implementations
|  `- exception/                  EmailAlreadyExistsException, ForbiddenException, ...
|
`- infrastruktur                  <- framework adapters
   |- presentation/rest/
   |  |- controller/              AuthController, TodoListController, TodoController
   |  |- request/                 RegisterUserRequest, LoginRequest, CreateTodoRequest, ...
   |  |- response/                AuthResponse, TodoListResponse, TodoResponse, UserResponse
   |  |- mapper/                  rest mappers
   |  `- error/                   RestExceptionHandler, ErrorResponse
   |- persistence/db/
   |  |- entity/                  UserJpaEntity, TodoListJpaEntity, TodoJpaEntity
   |  |- repository/              Spring Data repositories
   |  |- adapter/                 persistence adapters implementing out-ports
   |  `- mapper/                  persistence mappers
   |- security/
   |  |- jwt/                     JwtProperties
   |  |- filter/                  JwtAuthenticationFilter
   |  |- adapter/                 JwtTokenServiceAdapter, BCryptPasswordHashingAdapter, ...
   |  `- config/                  SecurityConfig
   `- config/                     OpenApiConfig, ApplicationBeanConfig
```

### Architecture Rules (mandatory)

- Dependencies must only point inward: `infrastruktur -> application -> domain`
- `domain` must not depend on Spring, JPA, REST, or infrastructure frameworks
- `application` must not use controllers, JPA repositories, or JPA entities
- REST DTOs (request/response) are only allowed in `infrastruktur.presentation.rest`
- JPA entities are only allowed in `infrastruktur.persistence.db.entity`
- Persistence adapters implement application out-ports

---

## Frontend Structure

```
src/app/
|- core/
|  |- auth/           auth.service.ts
|  |- guards/         auth.guard.ts
|  |- interceptors/   jwt.interceptor.ts
|  |- models/         user.model.ts, todo-list.model.ts, todo.model.ts
|  `- services/       todo-list.service.ts, todo.service.ts
|- features/
|  |- auth/           login/, register/
|  |- lists/          list-overview/, list-form/
|  `- todos/          todo-list-view/, todo-item/, todo-form/, todo-filter/, todo-sort/
`- shared/
   `- components/     empty-state/, loading-spinner/, error-message/
```

### Frontend Rules

- Use Standalone Components only (no NgModules)
- Use Reactive Forms for forms
- JWT is attached automatically by `jwtInterceptor` (HttpInterceptorFn)
- `authGuard` protects all routes except `/login` and `/register`
- API base URL for development: `http://localhost:8080` via `proxy.conf.json`
- Environments: `src/environments/environment.ts` and `environment.development.ts`

---

## Data Model

| Entity | Fields |
|---|---|
| User | id (UUID), email, passwordHash, createdAt, updatedAt |
| TodoList | id (UUID), ownerId (-> User), name (max 80), createdAt, updatedAt |
| Todo | id (UUID), listId (-> TodoList), ownerId (-> User), title (max 120), description? (max 1000), status (OPEN/DONE), priority (LOW/MEDIUM/HIGH), dueDate?, createdAt, updatedAt |

---

## REST API

Base path: `/api`

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | /auth/register | - | Register |
| POST | /auth/login | - | Login -> JWT |
| POST | /auth/logout | JWT | Logout |
| GET | /auth/me | JWT | Current user profile |
| GET | /lists | JWT | Get all user lists |
| POST | /lists | JWT | Create list |
| PUT | /lists/{listId} | JWT | Rename list |
| DELETE | /lists/{listId} | JWT | Delete list |
| GET | /lists/{listId}/todos | JWT | Get todos (+ filter/sort) |
| POST | /lists/{listId}/todos | JWT | Create todo |
| PUT | /lists/{listId}/todos/{todoId} | JWT | Update todo |
| DELETE | /lists/{listId}/todos/{todoId} | JWT | Delete todo |
| PATCH | /lists/{listId}/todos/{todoId}/complete | JWT | Mark done |
| PATCH | /lists/{listId}/todos/{todoId}/reopen | JWT | Reopen |

### Query Parameters (`GET /lists/{listId}/todos`)

```
?status=open|done
?due=today|overdue
?sort=dueDate,asc|priority,desc|createdAt,desc|status,asc
```

### Error Format

```json
{ "code": "VALIDATION_ERROR", "field": "title", "message": "Title is required" }
```

---

## Validation Rules

| Field | Rule | Error message |
|---|---|---|
| Todo title | required | "Title is required" |
| Todo title | max 120 chars | "Title must be at most 120 characters" |
| Todo description | max 1000 chars | "Description must be at most 1000 characters" |
| List name | required | "List name is required" |
| List name | max 80 chars | "List name must be at most 80 characters" |
| Password | min 8 chars | "Password must be at least 8 characters" |

---

## Authentication Concept

- JWT HS256 with 24h lifetime (`JWT_EXPIRATION_MS`)
- Secret via `JWT_SECRET` environment variable (never hardcode)
- Logout is client-side token removal (no server blacklist in v1.0)
- No token refresh in v1.0

---

## Database and Migration

- Flyway scripts in `backend/src/main/resources/db/migration/`:
  - `V1__create_users_table.sql`
  - `V2__create_todo_lists_table.sql`
  - `V3__create_todos_table.sql`
- Production: `spring.jpa.hibernate.ddl-auto=validate`
- Tests: H2 In-Memory with `test` profile (`src/test/resources/application-test.properties`)

---

## Docker

```bash
# Start from repository root
docker compose up --build

# Ports:
# Frontend: http://localhost:4200
# Backend:  http://localhost:8080
# Swagger:  http://localhost:8080/swagger-ui.html
# DB:       localhost:5432
```

Environment variables (`.env` in root):

```env
POSTGRES_DB=todo
POSTGRES_USER=todo
POSTGRES_PASSWORD=todo
JWT_SECRET=your-long-random-secret-at-least-32-characters
JWT_EXPIRATION_MS=86400000
```

---

## Running Tests

```bash
# Backend
cd backend
.\mvnw.cmd test "-Dspring.profiles.active=test"

# Backend coverage report -> backend/target/site/jacoco/index.html
.\mvnw.cmd verify "-Dspring.profiles.active=test"

# Frontend
cd frontend
npm test
npm run test:coverage   # -> frontend/coverage/frontend/index.html
```

---

## Project Conventions and Notes

- Backend package uses German spelling: `infrastruktur` (with `k`), not `infrastructure`
- Application services are wired in `ApplicationBeanConfig` as `@Bean` (no `@Service` in application layer)
- Default todo sorting: OPEN first -> earliest due date -> highest priority
- A default list named `"Meine Aufgaben"` is created automatically on user registration
- `ownerId` is stored redundantly on `Todo` (in addition to `TodoList`) for efficient access control
- Frontend uses Angular 21 and Vitest (not Karma/Jasmine)

---

## Open Points in Version 1.0

- No token refresh (re-login required after 24h)
- No password reset
- No admin panel
- `communication` package exists as structure only and is currently empty

---

## Copilot Guidance for New Features

When adding features, follow this order:

1. Start in `domain/` (model/value objects/rules)
2. Continue in `application/` (use case interfaces + command/query + service)
3. Implement adapters in `infrastruktur/` (persistence + REST)
4. Add tests using `@SpringBootTest @ActiveProfiles("test")` and H2
5. Keep architecture rules strict
