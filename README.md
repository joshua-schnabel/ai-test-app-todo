# Todo App

A multi-user Todo web app with an Angular frontend and a Spring Boot backend.

## Technology Stack

| Component | Technology |
|---|---|
| Frontend | Angular 21, SCSS, Reactive Forms |
| Backend | Spring Boot 3.5, Java 21, Hexagonal Architecture |
| Database (Prod) | PostgreSQL 16 |
| Database (Test) | H2 In-Memory |
| Migration | Flyway |
| Authentication | JWT (HS256) + BCrypt |
| Runtime | Docker + Docker Compose |

---

## Run Locally with Docker Compose

```bash
docker compose up --build
```

| Service | URL |
|---|---|
| Frontend | http://localhost:4200 |
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| PostgreSQL | localhost:5432 |

### Environment Variables (optional `.env` file)

```env
POSTGRES_DB=todo
POSTGRES_USER=todo
POSTGRES_PASSWORD=todo
JWT_SECRET=your-long-random-secret
JWT_EXPIRATION_MS=86400000
```

---

## Local Development Without Docker

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Requirements: Java 21, local PostgreSQL (or H2 with profile `test`).

### Frontend

```bash
cd frontend
npm install
npm start
```

Requirements: Node.js 20+.

---

## Profiles and Databases

| Profile | Database | Purpose |
|---|---|---|
| `default` | PostgreSQL | Production / local development |
| `test` | H2 (In-Memory) | Automated tests |

---

## Main API Endpoints

```
POST /api/auth/register   - Register
POST /api/auth/login      - Login -> JWT
POST /api/auth/logout     - Logout
GET  /api/auth/me         - Current user profile

GET    /api/lists                              - All lists for current user
POST   /api/lists                              - Create list
PUT    /api/lists/{listId}                     - Rename list
DELETE /api/lists/{listId}                     - Delete list

GET    /api/lists/{listId}/todos               - Get todos (filter/sort)
POST   /api/lists/{listId}/todos               - Create todo
PUT    /api/lists/{listId}/todos/{todoId}      - Update todo
DELETE /api/lists/{listId}/todos/{todoId}      - Delete todo
PATCH  /api/lists/{listId}/todos/{todoId}/complete  - Mark as done
PATCH  /api/lists/{listId}/todos/{todoId}/reopen    - Reopen todo
```

---

## Running Tests

```bash
# Backend tests
cd backend
./mvnw test

# Frontend tests
cd frontend
npm test
```

---

## Project Structure

```
Projekt K/
|- backend/          Spring Boot backend
|- frontend/         Angular frontend
|- docker-compose.yml
|- README.md
`- .plan/plan.md
```

### Backend Package Structure

```
de.joshuaschnabel.todo
|- domain            Core domain model (no Spring, no JPA)
|  |- model
|  |- valueobject
|  `- exception
|- application       Use cases and ports
|  |- usecase
|  |- port/in|out
|  |- command
|  |- query
|  `- service
`- infrastruktur     Framework adapters
   |- presentation/rest
   |- persistence/db
   |- security
   `- config
```

---

## Known Limitations (Version 1.0)

- No token refresh (user must log in again after 24h)
- No shared lists or collaboration between users
- No password reset
- No email verification
- No admin panel
