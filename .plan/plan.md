# Technischer Umsetzungsplan – Todo-Webapp Version 1.0

## Problem & Ansatz

Entwicklung einer Multi-User-Todo-Webapp mit Angular-Frontend, Spring-Boot-Backend (hexagonale Architektur), PostgreSQL als produktive Datenbank und H2 für Tests. Betrieb über Docker Compose. Jeder Nutzer sieht ausschließlich seine eigenen Daten.

---

## 1. Zielarchitektur

```
Browser (Angular SPA)
        │  HTTPS / REST + JWT
        ▼
Spring Boot REST API
        │
┌───────┴────────┐
│  Hexagonal     │
│  Backend       │
│  ─────────     │
│  Presentation  │  ← REST-Controller (Infrastruktur)
│  Application   │  ← Use Cases, Ports
│  Domain        │  ← Fachlogik, Wertobjekte
│  Infrastruktur │  ← Persistence, Security, Config
└───────┬────────┘
        │  JPA / JDBC
        ▼
   PostgreSQL (Prod) / H2 (Test)
```

**Repository-Struktur (Git-Root: `Projekt K/`):**
```
Projekt K/                        ← Git-Root
├── .github/
│   └── copilot-instructions.md  ← Copilot-Kontext für alle Rechner
├── .plan/
│   └── plan.md                  ← Dieser Plan
├── backend/                     ← Spring Boot Backend
│   ├── src/main/java/de/joshuaschnabel/todo/
│   ├── src/main/resources/db/migration/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                    ← Angular 21 Frontend
│   ├── src/app/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## 2. Backend-Struktur – Hexagonale Architektur

Drei konzentrische Ringe:

| Ring | Inhalt | Abhängigkeit |
|---|---|---|
| Domain | Fachmodell, Wertobjekte, Domain-Services | Keine äußeren Abhängigkeiten |
| Application | Use Cases, Ports (in/out), Commands, Queries | Nur Domain |
| Infrastruktur | REST, JPA, Security, Config | Application + Domain |

**Abhängigkeitsregel:** `Infrastruktur → Application → Domain` (niemals umgekehrt)

---

## 3. Verbindliche Package-Struktur Backend

Basis-Package: `de.joshuaschnabel.todo`

```
de.joshuaschnabel.todo
├── domain
│   ├── model
│   │   ├── User.java
│   │   ├── TodoList.java
│   │   └── Todo.java
│   ├── valueobject
│   │   ├── EmailAddress.java
│   │   ├── TodoTitle.java
│   │   ├── TodoListName.java
│   │   ├── TodoStatus.java      (enum: OPEN, DONE)
│   │   └── TodoPriority.java    (enum: LOW, MEDIUM, HIGH)
│   ├── service
│   │   └── TodoDomainService.java
│   ├── exception
│   │   ├── DomainException.java
│   │   └── ValidationException.java
│   └── event
│       └── (optional: UserRegisteredEvent, etc.)
│
├── application
│   ├── usecase
│   │   ├── RegisterUserUseCase.java       (interface)
│   │   ├── LoginUserUseCase.java          (interface)
│   │   ├── CreateTodoListUseCase.java     (interface)
│   │   ├── RenameTodoListUseCase.java     (interface)
│   │   ├── DeleteTodoListUseCase.java     (interface)
│   │   ├── CreateTodoUseCase.java         (interface)
│   │   ├── UpdateTodoUseCase.java         (interface)
│   │   ├── DeleteTodoUseCase.java         (interface)
│   │   ├── CompleteTodoUseCase.java       (interface)
│   │   ├── ReopenTodoUseCase.java         (interface)
│   │   └── FindTodosUseCase.java          (interface)
│   ├── port
│   │   ├── in
│   │   │   └── (Use Case Interfaces, s.o.)
│   │   └── out
│   │       ├── LoadUserPort.java
│   │       ├── SaveUserPort.java
│   │       ├── LoadTodoListPort.java
│   │       ├── SaveTodoListPort.java
│   │       ├── DeleteTodoListPort.java
│   │       ├── LoadTodoPort.java
│   │       ├── SaveTodoPort.java
│   │       ├── DeleteTodoPort.java
│   │       ├── PasswordHashingPort.java
│   │       └── TokenServicePort.java
│   ├── command
│   │   ├── RegisterUserCommand.java
│   │   ├── CreateTodoListCommand.java
│   │   ├── CreateTodoCommand.java
│   │   ├── UpdateTodoCommand.java
│   │   └── ...
│   ├── query
│   │   ├── FindTodosQuery.java
│   │   └── ...
│   └── service
│       ├── UserApplicationService.java
│       ├── TodoListApplicationService.java
│       └── TodoApplicationService.java
│
└── infrastruktur
    ├── presentation
    │   └── rest
    │       ├── controller
    │       │   ├── AuthController.java
    │       │   ├── TodoListController.java
    │       │   └── TodoController.java
    │       ├── request
    │       │   ├── RegisterUserRequest.java
    │       │   ├── LoginRequest.java
    │       │   ├── CreateTodoListRequest.java
    │       │   ├── CreateTodoRequest.java
    │       │   └── UpdateTodoRequest.java
    │       ├── response
    │       │   ├── AuthResponse.java
    │       │   ├── TodoListResponse.java
    │       │   └── TodoResponse.java
    │       ├── mapper
    │       │   ├── TodoListRestMapper.java
    │       │   └── TodoRestMapper.java
    │       └── error
    │           ├── RestExceptionHandler.java
    │           └── ErrorResponse.java
    ├── persistence
    │   └── db
    │       ├── entity
    │       │   ├── UserJpaEntity.java
    │       │   ├── TodoListJpaEntity.java
    │       │   └── TodoJpaEntity.java
    │       ├── repository
    │       │   ├── SpringDataUserRepository.java
    │       │   ├── SpringDataTodoListRepository.java
    │       │   └── SpringDataTodoRepository.java
    │       ├── adapter
    │       │   ├── UserPersistenceAdapter.java
    │       │   ├── TodoListPersistenceAdapter.java
    │       │   └── TodoPersistenceAdapter.java
    │       └── mapper
    │           ├── UserPersistenceMapper.java
    │           ├── TodoListPersistenceMapper.java
    │           └── TodoPersistenceMapper.java
    ├── communication
    │   ├── http
    │   │   └── (Platzhalter – V1.0 optional)
    │   └── mq
    │       └── (Platzhalter – V1.0 optional)
    ├── security
    │   ├── jwt
    │   │   ├── JwtProperties.java
    │   │   └── JwtUtil.java
    │   ├── filter
    │   │   └── JwtAuthenticationFilter.java
    │   ├── config
    │   │   └── SecurityConfig.java
    │   └── adapter
    │       ├── JwtTokenServiceAdapter.java
    │       ├── BCryptPasswordHashingAdapter.java
    │       └── CurrentUserProviderAdapter.java
    └── config
        ├── OpenApiConfig.java
        ├── CorsConfig.java
        ├── ClockConfig.java
        └── ApplicationBeanConfig.java
```

---

## 4. Frontend-Struktur – Angular

```
frontend/src/app/
├── core/
│   ├── auth/
│   │   ├── auth.service.ts
│   │   └── auth.model.ts
│   ├── services/
│   │   ├── todo-list.service.ts
│   │   └── todo.service.ts
│   ├── models/
│   │   ├── todo-list.model.ts
│   │   └── todo.model.ts
│   ├── guards/
│   │   └── auth.guard.ts
│   └── interceptors/
│       └── jwt.interceptor.ts
├── features/
│   ├── auth/
│   │   ├── login/
│   │   │   ├── login.component.ts
│   │   │   └── login.component.html
│   │   └── register/
│   │       ├── register.component.ts
│   │       └── register.component.html
│   ├── lists/
│   │   ├── list-overview/
│   │   ├── list-form/
│   │   └── list-item/
│   └── todos/
│       ├── todo-list-view/
│       ├── todo-form/
│       ├── todo-filter/
│       ├── todo-sort/
│       └── todo-item/
└── shared/
    └── components/
        ├── empty-state/
        ├── loading-spinner/
        └── error-message/
```

**Technische Eckpunkte:**
- Angular 17+ (Standalone Components)
- Reactive Forms für alle Formulare
- JWT wird über HttpInterceptor automatisch mitgesendet
- Auth Guard schützt alle Routen außer `/login` und `/register`
- Mobile First mit CSS Grid/Flexbox (min-width: 360px)

---

## 5. Datenmodell

### 5.1 User
| Feld | Typ | Constraints |
|---|---|---|
| id | UUID | PK |
| email | VARCHAR(255) | UNIQUE, NOT NULL |
| password_hash | VARCHAR(255) | NOT NULL |
| created_at | TIMESTAMP WITH TZ | NOT NULL |
| updated_at | TIMESTAMP WITH TZ | NOT NULL |

### 5.2 TodoList
| Feld | Typ | Constraints |
|---|---|---|
| id | UUID | PK |
| owner_id | UUID | FK → User(id), NOT NULL |
| name | VARCHAR(80) | NOT NULL |
| created_at | TIMESTAMP WITH TZ | NOT NULL |
| updated_at | TIMESTAMP WITH TZ | NOT NULL |

### 5.3 Todo
| Feld | Typ | Constraints |
|---|---|---|
| id | UUID | PK |
| list_id | UUID | FK → TodoList(id) CASCADE DELETE, NOT NULL |
| owner_id | UUID | FK → User(id), NOT NULL |
| title | VARCHAR(120) | NOT NULL |
| description | TEXT | NULL |
| status | VARCHAR(10) | 'OPEN'/'DONE', NOT NULL, DEFAULT 'OPEN' |
| priority | VARCHAR(10) | 'LOW'/'MEDIUM'/'HIGH', NOT NULL, DEFAULT 'MEDIUM' |
| due_date | DATE | NULL |
| created_at | TIMESTAMP WITH TZ | NOT NULL |
| updated_at | TIMESTAMP WITH TZ | NOT NULL |

---

## 6. REST-API-Design

**Basis-URL:** `/api`

### 6.1 Authentifizierung
| Methode | Pfad | Auth | Beschreibung |
|---|---|---|---|
| POST | /auth/register | – | Registrierung |
| POST | /auth/login | – | Login → JWT |
| POST | /auth/logout | JWT | Logout (Client-seitig) |
| GET | /auth/me | JWT | Eigenes Profil |

**Register Request:**
```json
{ "email": "user@example.com", "password": "sicher123" }
```
**Login Response:**
```json
{ "token": "eyJ...", "expiresAt": "2026-06-03T00:00:00Z" }
```

### 6.2 Todo-Listen
| Methode | Pfad | Auth | Beschreibung |
|---|---|---|---|
| GET | /lists | JWT | Alle eigenen Listen |
| POST | /lists | JWT | Liste erstellen |
| GET | /lists/{listId} | JWT | Einzelne Liste |
| PUT | /lists/{listId} | JWT | Liste umbenennen |
| DELETE | /lists/{listId} | JWT | Liste löschen |

### 6.3 Aufgaben
| Methode | Pfad | Auth | Beschreibung |
|---|---|---|---|
| GET | /lists/{listId}/todos | JWT | Aufgaben (mit Filter/Sort) |
| POST | /lists/{listId}/todos | JWT | Aufgabe erstellen |
| GET | /lists/{listId}/todos/{todoId} | JWT | Einzelne Aufgabe |
| PUT | /lists/{listId}/todos/{todoId} | JWT | Aufgabe bearbeiten |
| DELETE | /lists/{listId}/todos/{todoId} | JWT | Aufgabe löschen |
| PATCH | /lists/{listId}/todos/{todoId}/complete | JWT | Als erledigt markieren |
| PATCH | /lists/{listId}/todos/{todoId}/reopen | JWT | Wieder öffnen |

### 6.4 Filter & Sortierung (Query-Parameter)
```
?status=open|done
?due=today|overdue
?sort=dueDate,asc|priority,desc|createdAt,desc|status,asc
```

### 6.5 Fehlerantworten
```json
{
  "code": "VALIDATION_ERROR",
  "field": "title",
  "message": "Titel ist erforderlich"
}
```

| HTTP-Status | Code | Wann |
|---|---|---|
| 400 | VALIDATION_ERROR | Ungültige Eingabe |
| 401 | UNAUTHORIZED | Nicht angemeldet |
| 403 | FORBIDDEN | Fremde Ressource |
| 404 | NOT_FOUND | Ressource nicht gefunden |
| 409 | EMAIL_ALREADY_EXISTS | E-Mail bereits registriert |
| 500 | INTERNAL_ERROR | Technischer Fehler |

---

## 7. Authentifizierungs- und Autorisierungskonzept

- **Protokoll:** JWT Bearer Token (stateless)
- **Algorithmus:** HS256 oder RS256
- **Token-Lebensdauer:** 24h (konfigurierbar per Umgebungsvariable)
- **Passwort-Hashing:** BCrypt (Strength 12)
- **Ablauf:**
  1. POST /auth/login → Backend prüft E-Mail + Passwort (BCrypt verify)
  2. Backend erzeugt JWT mit `sub=userId`
  3. Client speichert JWT im localStorage
  4. Alle geschützten Requests: `Authorization: Bearer <token>`
  5. `JwtAuthenticationFilter` validiert Token, legt `SecurityContext` fest
- **Autorisierung:** Jeder Use Case prüft `ownerId == currentUserId` → sonst 403
- **Passwort nie im Klartext** in DB, Logs oder API-Responses
- Logout ist client-seitig (Token verwerfen); kein Server-seitiger Token-Blacklist in V1.0

---

## 8. Datenbank- und Migrationskonzept

### 8.1 Produktionsdatenbank (PostgreSQL)
- Läuft als Docker-Container
- Zugangsdaten über Umgebungsvariablen: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `spring.jpa.hibernate.ddl-auto=validate` in Produktion
- Schema-Migration über **Flyway**
- Migrations-Skripte liegen unter `backend/src/main/resources/db/migration/`
- Datenpersistenz über Docker Volume
- Verbindungspool: HikariCP (Spring Boot Default)

### 8.2 Flyway-Migrations (Versionierung)
```
V1__create_users_table.sql
V2__create_todo_lists_table.sql
V3__create_todos_table.sql
```

### 8.3 Testdatenbank (H2)
- Spring Profile `test` aktiviert H2 (In-Memory)
- `spring.jpa.hibernate.ddl-auto=create-drop` für Tests
- Flyway läuft auch gegen H2 (kompatible SQL-Syntax beachten)
- Testdaten werden per `@BeforeEach` / `@Sql` kontrolliert erzeugt
- Tests laufen ohne PostgreSQL-Instanz

---

## 9. Docker-Konzept

### 9.1 docker-compose.yml
```yaml
services:
  postgres:
    image: postgres:16
    volumes: [postgres-data:/var/lib/postgresql/data]
    environment: [POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD]
    ports: ["5432:5432"]

  backend:
    build: ./backend
    depends_on: [postgres]
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/todo
      DB_USERNAME: ...
      DB_PASSWORD: ...
      JWT_SECRET: ...
    ports: ["8080:8080"]

  frontend:
    build: ./frontend
    depends_on: [backend]
    ports: ["4200:80"]

volumes:
  postgres-data:
```

### 9.2 Backend Dockerfile (Multi-Stage)
```dockerfile
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 9.3 Frontend Dockerfile (Multi-Stage)
```dockerfile
FROM node:20 AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
```

**Ports nach `docker compose up --build`:**
- Frontend: http://localhost:4200
- Backend: http://localhost:8080
- PostgreSQL: localhost:5432

---

## 10. Teststrategie

### 10.1 Backend-Tests

| Ebene | Technologie | Testfälle |
|---|---|---|
| Unit | JUnit 5 + Mockito | Domain-Objekte, Use-Case-Services, Mapper |
| Integration | Spring Boot Test + H2 | Repository-Tests, Use-Cases mit DB |
| REST (Slice) | MockMvc / WebMvcTest | Controller, Request/Response, Validierung |
| Security | Spring Security Test | 401/403-Fälle, Token-Validierung |

**Pflicht-Testfälle:**
- Registrierung (Erfolg, doppelte E-Mail → 409)
- Login (Erfolg, falsches Passwort → 401)
- Standardliste bei Registrierung erstellt
- CRUD Listen (Erstellen, Umbenennen, Löschen, Kaskadenlöschung)
- CRUD Aufgaben (Erstellen, Bearbeiten, Löschen)
- Statuswechsel offen ↔ erledigt
- Filter (alle, offen, erledigt, heute, überfällig)
- Sortierung (Fälligkeitsdatum, Priorität, Erstellungsdatum, Status)
- Zugriffsschutz (Nutzer A kann nicht auf Nutzer B's Listen/Aufgaben zugreifen → 403)
- Validierungsfehler (leerer Titel, zu langer Titel, etc.)
- 404 für nicht vorhandene Ressourcen

### 10.2 Frontend-Tests

| Ebene | Technologie | Testfälle |
|---|---|---|
| Unit | Jest / Karma + Jasmine | Services, Guards, Interceptors |
| Komponenten | Angular Testing Library | Formulare, Filter, Sortierung |

**Pflicht-Testfälle:**
- Login-Formular (Validierung, Submit, Fehlermeldung)
- Registrierungsformular (Validierung, Submit)
- Listen anzeigen / erstellen / bearbeiten / löschen
- Aufgaben anzeigen / erstellen / bearbeiten / löschen
- Aufgabe erledigen / wieder öffnen
- Filter und Sortierung
- Fehleranzeige bei API-Fehlern
- Route Guard schützt geschützte Routen

---

## 11. Umsetzungsschritte

### Phase 1 – Projektgerüst
1. Repository-Struktur anlegen (`todo-app/backend/`, `todo-app/frontend/`)
2. Spring Boot-Projekt initialisieren (Maven, Java 21)
3. Angular-Projekt initialisieren (Angular CLI)
4. Docker-Grundgerüst (Dockerfiles, docker-compose.yml)
5. README.md Grundstruktur

### Phase 2 – Backend Domain & Application
6. Domain-Modell (User, TodoList, Todo, Wertobjekte, Enums)
7. Application Ports (in/out Interfaces)
8. Application Services / Use Cases implementieren
9. Commands und Queries definieren

### Phase 3 – Backend Infrastruktur – Persistence
10. JPA-Entities (User, TodoList, Todo)
11. Spring Data Repositories
12. Flyway-Migrations-Skripte (V1–V3)
13. Persistence Adapter + Mapper

### Phase 4 – Backend Infrastruktur – Security
14. BCryptPasswordHashingAdapter
15. JwtUtil + JwtTokenServiceAdapter
16. JwtAuthenticationFilter
17. SecurityConfig (CORS, stateless Session, öffentliche Endpunkte)
18. CurrentUserProviderAdapter

### Phase 5 – Backend Infrastruktur – REST
19. AuthController (register, login, me)
20. TodoListController
21. TodoController
22. Request/Response-DTOs
23. REST-Mapper
24. RestExceptionHandler (globale Fehlerbehandlung)

### Phase 6 – Backend Konfiguration & Docs
25. OpenAPI / Swagger-Konfiguration
26. CorsConfig, ClockConfig
27. application.properties (Profiles: default, test)
28. Backend-Tests (alle Pflichtfälle)

### Phase 7 – Frontend Core
29. Angular-Projektstruktur anlegen
30. Core-Models (TodoList, Todo, Auth)
31. AuthService (login, register, logout, JWT-Storage)
32. JwtInterceptor (automatisches Anhängen des Tokens)
33. AuthGuard
34. TodoListService + TodoService (API-Calls)

### Phase 8 – Frontend Features
35. Login-Seite (Reactive Form, Validierung, Fehlermeldung)
36. Registrierungsseite
37. Todo-Übersicht (Listen-Sidebar + Aufgabenliste)
38. Listenverwaltung (Erstellen, Umbenennen, Löschen)
39. Aufgabenformular (Erstellen, Bearbeiten)
40. Filter-Komponente
41. Sortier-Komponente
42. Shared-Komponenten (EmptyState, LoadingSpinner, ErrorMessage)

### Phase 9 – Mobile First & UX
43. CSS Mobile First (min-width: 360px, Touch-Targets, einspaltige Formulare)
44. Desktop-Erweiterung (zwei Spalten: Listen + Aufgaben)
45. Leerzustände (keine Listen, keine Aufgaben, kein Treffer)
46. Ladezustände
47. Fehlerzustände

### Phase 10 – Integration & Abschluss
48. Frontend-Tests (alle Pflichtfälle)
49. End-to-End-Smoke-Test mit `docker compose up --build`
50. README vervollständigen
51. Definition of Done prüfen

---

## 12. Bekannte Risiken und offene Punkte

### Risiken
| Risiko | Wahrscheinlichkeit | Maßnahme |
|---|---|---|
| Flyway H2-Kompatibilität | Mittel | PostgreSQL-spezifische SQL-Syntax vermeiden (UUID-Typ prüfen) |
| CORS-Konfiguration falsch | Mittel | Früh testen; CORS in SecurityConfig korrekt setzen |
| JWT-Secret-Management | Hoch | Secret als Umgebungsvariable, nie hardcoden |
| Angular Build-Pfad in Nginx | Niedrig | `dist`-Pfad im Dockerfile prüfen |
| Package-Abhängigkeitsregeln verletzt | Mittel | ArchUnit-Tests erwägen für automatische Architekturprüfung |

### Offene Punkte
- JWT-Algorithmus: HS256 (einfacher) vs. RS256 (sicherer, asymmetrisch) → V1.0: HS256
- Token-Refresh: Nicht in V1.0 (Token-Ablauf nach 24h → erneuter Login)
- Angular-Version: 17+ (Standalone Components) oder klassisches Modul-System → Standalone bevorzugt
- Test-Framework Frontend: Karma/Jasmine (Angular-Standard) oder Jest → Standard beibehalten
- ArchUnit im Backend für automatische Architekturprüfung (sinnvoll, optional)

---

## Prüfkriterien Package-Struktur (Checkliste)

- [ ] Domain enthält keine Spring-Annotationen
- [ ] Domain enthält keine JPA-Annotationen
- [ ] Domain enthält keine REST-DTOs
- [ ] Application enthält keine Controller
- [ ] Application enthält keine JPA-Repositories
- [ ] REST-Controller liegen unter `infrastruktur.presentation.rest`
- [ ] JPA-Entities liegen unter `infrastruktur.persistence.db.entity`
- [ ] Spring-Data-Repositories liegen unter `infrastruktur.persistence.db.repository`
- [ ] Persistence Adapter implementieren Application-Out-Ports
- [ ] Security-Implementierungen liegen unter `infrastruktur.security`
- [ ] Abhängigkeiten zeigen nur von Infrastruktur → Application → Domain
- [ ] Kein innerer Ring hängt von einem äußeren Ring ab
