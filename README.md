# Todo-App

Eine Multi-User-Todo-Webapp mit Angular-Frontend und Spring Boot-Backend.

## Technologie-Stack

| Komponente | Technologie |
|---|---|
| Frontend | Angular 19, SCSS, Reactive Forms |
| Backend | Spring Boot 3.5, Java 21, Hexagonale Architektur |
| Datenbank (Prod) | PostgreSQL 16 |
| Datenbank (Test) | H2 In-Memory |
| Migration | Flyway |
| Authentifizierung | JWT (HS256) + BCrypt |
| Betrieb | Docker + Docker Compose |

---

## Lokale Ausführung mit Docker Compose

```bash
cd todo-app
docker compose up --build
```

| Service | URL |
|---|---|
| Frontend | http://localhost:4200 |
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| PostgreSQL | localhost:5432 |

### Umgebungsvariablen (optional, `.env` Datei)

```env
POSTGRES_DB=todo
POSTGRES_USER=todo
POSTGRES_PASSWORD=todo
JWT_SECRET=dein-langes-zufaelliges-secret
JWT_EXPIRATION_MS=86400000
```

---

## Lokale Entwicklung ohne Docker

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Voraussetzungen: Java 21, PostgreSQL läuft lokal (oder H2 mit Profil `test`)

### Frontend

```bash
cd frontend
npm install
npm start
```

Voraussetzungen: Node.js 20+

---

## Profile und Datenbanken

| Profil | Datenbank | Zweck |
|---|---|---|
| `default` | PostgreSQL | Produktion / lokale Entwicklung |
| `test` | H2 (In-Memory) | Automatisierte Tests |

---

## Wichtigste API-Endpunkte

```
POST /api/auth/register   – Registrierung
POST /api/auth/login      – Login → JWT
POST /api/auth/logout     – Logout
GET  /api/auth/me         – Eigenes Profil

GET    /api/lists                              – Alle eigenen Listen
POST   /api/lists                              – Liste erstellen
PUT    /api/lists/{listId}                     – Liste umbenennen
DELETE /api/lists/{listId}                     – Liste löschen

GET    /api/lists/{listId}/todos               – Aufgaben (Filter/Sort)
POST   /api/lists/{listId}/todos               – Aufgabe erstellen
PUT    /api/lists/{listId}/todos/{todoId}      – Aufgabe bearbeiten
DELETE /api/lists/{listId}/todos/{todoId}      – Aufgabe löschen
PATCH  /api/lists/{listId}/todos/{todoId}/complete  – Als erledigt markieren
PATCH  /api/lists/{listId}/todos/{todoId}/reopen    – Wieder öffnen
```

---

## Tests ausführen

```bash
# Backend-Tests
cd backend
./mvnw test

# Frontend-Tests
cd frontend
npm test
```

---

## Projektstruktur

```
todo-app/
├── backend/          Spring Boot Backend
├── frontend/         Angular Frontend
├── docker-compose.yml
└── README.md
```

### Backend Package-Struktur

```
de.joshuaschnabel.todo
├── domain            Fachmodell (kein Spring, kein JPA)
│   ├── model
│   ├── valueobject
│   ├── service
│   └── exception
├── application       Use Cases & Ports
│   ├── usecase
│   ├── port/in|out
│   ├── command
│   ├── query
│   └── service
└── infrastruktur     Framework-Adapter
    ├── presentation/rest
    ├── persistence/db
    ├── security
    └── config
```

---

## Bekannte Einschränkungen Version 1.0

- Kein Token-Refresh (nach 24h erneuter Login erforderlich)
- Keine gemeinsamen Listen oder Zusammenarbeit zwischen Nutzern
- Kein Passwort-zurücksetzen
- Keine E-Mail-Verifikation
- Kein Admin-Bereich
