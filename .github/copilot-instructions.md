# Copilot Instructions – Todo-App

Diese Datei gibt GitHub Copilot den vollständigen Projektkontext.
Sie wird automatisch auf jedem Rechner geladen, auf dem dieses Repository ausgecheckt ist.

---

## Projekt-Überblick

**Todo-Webapp Version 1.0** – Multi-User-App zur Verwaltung von Aufgabenlisten.

- Mehrere Nutzer, strikt getrennte Daten (jeder Nutzer sieht nur seine eigenen Listen/Aufgaben)
- Registrierung, Login, Logout per JWT
- Todo-Listen mit Aufgaben: erstellen, bearbeiten, löschen, erledigen
- Filtern und Sortieren von Aufgaben
- Mobile First (ab 360 px)

---

## Technologie-Stack

| Schicht | Technologie |
|---|---|
| Frontend | Angular 21, Standalone Components, SCSS, Reactive Forms |
| Backend | Spring Boot 3.5, Java 21 |
| Backend-Architektur | Hexagonale Architektur (Ports & Adapters) |
| Authentifizierung | JWT (HS256, 24h), BCrypt (Passwort-Hashing) |
| Datenbank Produktion | PostgreSQL 16 |
| Datenbank Tests | H2 In-Memory (Profil: `test`) |
| DB-Migration | Flyway |
| Betrieb | Docker + Docker Compose |
| API-Dokumentation | OpenAPI / Swagger (`/swagger-ui.html`) |

---

## Repository-Struktur

```
Projekt K/                        ← Git-Root
├── .github/
│   └── copilot-instructions.md  ← Diese Datei
├── .plan/
│   └── plan.md                  ← Technischer Umsetzungsplan
├── backend/                     ← Spring Boot Backend
│   ├── src/main/java/de/joshuaschnabel/todo/
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── db/migration/        ← Flyway-Skripte (V1–V3)
│   ├── src/test/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                    ← Angular Frontend
│   ├── src/app/
│   │   ├── core/               ← Services, Guards, Interceptors, Models
│   │   ├── features/           ← auth/, lists/, todos/
│   │   └── shared/             ← Wiederverwendbare Komponenten
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── proxy.conf.json
│   └── package.json
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## Backend Package-Struktur

Basis-Package: `de.joshuaschnabel.todo`

```
de.joshuaschnabel.todo
├── domain                       ← Fachkern – KEIN Spring, KEIN JPA
│   ├── model/                   User, TodoList, Todo
│   ├── valueobject/             EmailAddress, TodoTitle, TodoListName, TodoStatus, TodoPriority
│   └── exception/               DomainException, ValidationException
│
├── application                  ← Use Cases & Ports
│   ├── usecase/                 Interfaces (RegisterUserUseCase, CreateTodoUseCase, ...)
│   ├── port/
│   │   ├── in/                  (= Use Case Interfaces)
│   │   └── out/                 LoadUserPort, SaveUserPort, PasswordHashingPort, TokenServicePort, ...
│   ├── command/                 RegisterUserCommand, CreateTodoCommand, ... (Records)
│   ├── query/                   GetTodosQuery (Record)
│   ├── service/                 UserApplicationService, TodoListApplicationService, TodoApplicationService
│   └── exception/               EmailAlreadyExistsException, ForbiddenException, NotFoundException, InvalidCredentialsException
│
└── infrastruktur                ← Framework-Adapter
    ├── presentation/rest/
    │   ├── controller/          AuthController, TodoListController, TodoController
    │   ├── request/             RegisterUserRequest, LoginRequest, CreateTodoRequest, ...
    │   ├── response/            AuthResponse, TodoListResponse, TodoResponse, UserResponse
    │   ├── mapper/              TodoListRestMapper, TodoRestMapper
    │   └── error/               RestExceptionHandler, ErrorResponse
    ├── persistence/db/
    │   ├── entity/              UserJpaEntity, TodoListJpaEntity, TodoJpaEntity
    │   ├── repository/          SpringDataUserRepository, SpringDataTodoListRepository, SpringDataTodoRepository
    │   ├── adapter/             UserPersistenceAdapter, TodoListPersistenceAdapter, TodoPersistenceAdapter
    │   └── mapper/              UserPersistenceMapper, TodoListPersistenceMapper, TodoPersistenceMapper
    ├── security/
    │   ├── jwt/                 JwtProperties, JwtUtil
    │   ├── filter/              JwtAuthenticationFilter
    │   ├── adapter/             JwtTokenServiceAdapter, BCryptPasswordHashingAdapter, CurrentUserProviderAdapter
    │   └── config/              SecurityConfig
    └── config/                  OpenApiConfig, CorsConfig, ClockConfig, ApplicationBeanConfig
```

### Architektur-Regeln (verbindlich)

- Abhängigkeiten zeigen **nur** von außen nach innen: `Infrastruktur → Application → Domain`
- `Domain` hat **keine** Spring-, JPA- oder REST-Abhängigkeiten
- `Application` kennt **keine** Controller, JPA-Repositories oder JPA-Entities
- REST-DTOs (Request/Response) **niemals** in Domain oder Application verwenden
- JPA-Entities **nur** in `infrastruktur.persistence.db.entity`
- Persistence Adapter **implementieren** Application Out-Ports

---

## Frontend-Struktur

```
src/app/
├── core/
│   ├── auth/           auth.service.ts
│   ├── guards/         auth.guard.ts
│   ├── interceptors/   jwt.interceptor.ts
│   ├── models/         user.model.ts, todo-list.model.ts, todo.model.ts
│   └── services/       todo-list.service.ts, todo.service.ts
├── features/
│   ├── auth/           login/, register/
│   ├── lists/          list-overview/, list-form/
│   └── todos/          todo-list-view/, todo-item/, todo-form/, todo-filter/, todo-sort/
└── shared/
    └── components/     empty-state/, loading-spinner/, error-message/
```

### Frontend-Regeln

- Alle Komponenten sind **Standalone Components** (keine NgModules)
- Formulare verwenden **Reactive Forms** (`ReactiveFormsModule`)
- JWT wird automatisch via `jwtInterceptor` (HttpInterceptorFn) gesendet
- `authGuard` schützt alle Routen außer `/login` und `/register`
- API-Base-URL: `http://localhost:8080` (via `proxy.conf.json` im Dev-Modus)
- Environments: `src/environments/environment.ts` und `environment.development.ts`

---

## Datenmodell

| Entity | Felder |
|---|---|
| User | id (UUID), email, passwordHash, createdAt, updatedAt |
| TodoList | id (UUID), ownerId (→ User), name (max 80), createdAt, updatedAt |
| Todo | id (UUID), listId (→ TodoList), ownerId (→ User), title (max 120), description? (max 1000), status (OPEN/DONE), priority (LOW/MEDIUM/HIGH), dueDate?, createdAt, updatedAt |

---

## REST-API

Basis: `/api`

| Methode | Pfad | Auth | Beschreibung |
|---|---|---|---|
| POST | /auth/register | – | Registrierung |
| POST | /auth/login | – | Login → JWT |
| POST | /auth/logout | JWT | Logout |
| GET | /auth/me | JWT | Eigenes Profil |
| GET | /lists | JWT | Alle eigenen Listen |
| POST | /lists | JWT | Liste erstellen |
| PUT | /lists/{listId} | JWT | Liste umbenennen |
| DELETE | /lists/{listId} | JWT | Liste löschen |
| GET | /lists/{listId}/todos | JWT | Aufgaben (+ Filter/Sort) |
| POST | /lists/{listId}/todos | JWT | Aufgabe erstellen |
| PUT | /lists/{listId}/todos/{todoId} | JWT | Aufgabe bearbeiten |
| DELETE | /lists/{listId}/todos/{todoId} | JWT | Aufgabe löschen |
| PATCH | /lists/{listId}/todos/{todoId}/complete | JWT | Als erledigt markieren |
| PATCH | /lists/{listId}/todos/{todoId}/reopen | JWT | Wieder öffnen |

### Filter-Parameter (`GET /lists/{listId}/todos`)
```
?status=open|done
?due=today|overdue
?sort=dueDate,asc|priority,desc|createdAt,desc|status,asc
```

### Fehlerformat
```json
{ "code": "VALIDATION_ERROR", "field": "title", "message": "Titel ist erforderlich" }
```

---

## Validierungsregeln

| Feld | Regel | Fehlermeldung |
|---|---|---|
| Aufgabe Titel | Pflicht | „Titel ist erforderlich" |
| Aufgabe Titel | max. 120 Zeichen | „Titel darf maximal 120 Zeichen lang sein" |
| Aufgabe Beschreibung | max. 1000 Zeichen | „Beschreibung darf maximal 1000 Zeichen lang sein" |
| Listenname | Pflicht | „Listenname ist erforderlich" |
| Listenname | max. 80 Zeichen | „Listenname darf maximal 80 Zeichen lang sein" |
| Passwort | min. 8 Zeichen | „Passwort muss mindestens 8 Zeichen lang sein" |

---

## Authentifizierungskonzept

- **JWT HS256**, Lebensdauer 24h (konfigurierbar via `JWT_EXPIRATION_MS`)
- Geheimnis via Umgebungsvariable `JWT_SECRET` (nie hardcoden!)
- Logout ist client-seitig (Token verwerfen) – kein Server-seitiger Blacklist
- Kein Token-Refresh in V1.0

---

## Datenbank & Migration

- **Flyway**-Migrations unter `backend/src/main/resources/db/migration/`:
  - `V1__create_users_table.sql`
  - `V2__create_todo_lists_table.sql`
  - `V3__create_todos_table.sql`
- Produktion: `spring.jpa.hibernate.ddl-auto=validate`
- Tests: H2 In-Memory mit Profil `test` (`src/test/resources/application-test.properties`)

---

## Docker

```bash
# Starten (aus Projekt-Root)
docker compose up --build

# Ports:
# Frontend: http://localhost:4200
# Backend:  http://localhost:8080
# Swagger:  http://localhost:8080/swagger-ui.html
# DB:       localhost:5432
```

Umgebungsvariablen (`.env` Datei im Root):
```env
POSTGRES_DB=todo
POSTGRES_USER=todo
POSTGRES_PASSWORD=todo
JWT_SECRET=dein-langes-zufaelliges-geheimnis-min-32-zeichen
JWT_EXPIRATION_MS=86400000
```

---

## Tests ausführen

```bash
# Backend (Java 21 + Maven erforderlich)
cd backend
.\mvnw.cmd test "-Dspring.profiles.active=test"

# Backend mit Coverage-Report (→ backend/target/site/jacoco/index.html)
.\mvnw.cmd verify "-Dspring.profiles.active=test"

# Frontend
cd frontend
npm test                  # ohne Coverage
npm run test:coverage     # mit Coverage (→ frontend/coverage/frontend/index.html)
```

---

## Bekannte Besonderheiten & Konventionen

- Das Backend-Package heißt `infrastruktur` (deutsch, mit k) – nicht `infrastructure`
- Alle Application-Services werden in `ApplicationBeanConfig` als `@Bean` verdrahtet (kein `@Service` in Application-Schicht)
- Standard-Sortierung Aufgaben: OPEN zuerst → frühestes dueDate → höchste Priorität
- Bei Registrierung wird automatisch eine Liste „Meine Aufgaben" erstellt
- `ownerId` steht redundant auch an `Todo` (nicht nur an `TodoList`) für effizienteren Zugriffsschutz
- Frontend: Angular 21 – Standalone Components, Vitest als Test-Runner (nicht Karma/Jasmine)

---

## Offene Punkte Version 1.0

- Kein Token-Refresh (nach 24h erneuter Login)
- Kein Passwort-zurücksetzen
- Kein Admin-Bereich
- `communication`-Package (http/mq) strukturell vorhanden aber leer

---

## Weiterentwicklung – Hinweise für Copilot

Wenn neue Features ergänzt werden:
1. Starte immer in `domain/` (Modell/Wertobjekte)
2. Dann `application/` (UseCase-Interface + Command/Query + Service)
3. Dann `infrastruktur/` (Persistence Adapter → REST Controller)
4. Tests: `@SpringBootTest @ActiveProfiles("test")` mit H2
5. Architektur-Regeln (s.o.) strikt einhalten
