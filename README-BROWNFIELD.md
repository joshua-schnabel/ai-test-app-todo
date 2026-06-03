# Brownfield AI Test App

A multi-user Todo web app — used as a **practical brownfield project** for testing AI-assisted development on an existing codebase.

---

## Setup

### Requirements

- `npm` (Node.js 20+)
- `mvnw` / Maven (Java 21)
- `docker` + Docker Compose

### Start the project

```bash
docker compose up --build
```

| Service | URL |
|---|---|
| Frontend | http://localhost:4200 |
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |

---

## Task 1 — Get Familiar with the Codebase

**Goal:** Understand the application well enough to safely extend it.  
Technologies, architecture, and structure should be clear after this task.

> This task uses **Claude Code** (`claude` CLI by Anthropic).  
> Run `claude` in the project root to start an interactive session.

---

### Step-by-step with Claude Code

#### 1. Start Claude Code in the project root

```bash
claude
```

---

#### 2. Start with `/plan` — get an overview

`/plan` tells Claude Code to create an exploration plan before reading any files. This prevents aimless browsing through the codebase.

> **EN:**
> ```
> /plan Explore this codebase. Read README.md and all markdown files you find.
> Then explore the folder structure of backend/ and frontend/.
> Summarize: the tech stack, the architecture, how backend and frontend
> communicate, and how data flows from database to UI.
> ```

> **DE:**
> ```
> /plan Mach dich mit dieser Codebase vertraut. Lies README.md und alle Markdown-Dateien.
> Erkunde dann die Ordnerstruktur von backend/ und frontend/.
> Fasse zusammen: den Tech-Stack, die Architektur, wie Backend und Frontend
> kommunizieren und wie Daten von der Datenbank bis zur UI fließen.
> ```

---

#### 3. Understand the backend architecture

> **EN:**
> ```
> Explain the backend package structure in detail.
> What are the three main layers and what is each layer responsible for?
> Show me one concrete example of how a single use case (e.g. creating a todo)
> flows through all layers — from the REST controller down to the database.
> ```

> **DE:**
> ```
> Erkläre die Backend-Paketstruktur im Detail.
> Welche drei Hauptschichten gibt es und wofür ist jede zuständig?
> Zeige mir ein konkretes Beispiel, wie ein Use Case (z.B. Todo erstellen)
> durch alle Schichten fließt – vom REST-Controller bis zur Datenbank.
> ```

---

#### 4. Understand the frontend structure

> **EN:**
> ```
> Explore the Angular frontend under frontend/src/app/.
> Explain the folder structure, how routing works, how the frontend authenticates
> with the backend, and how a todo list is loaded and displayed.
> ```

> **DE:**
> ```
> Erkunde das Angular-Frontend unter frontend/src/app/.
> Erkläre die Ordnerstruktur, wie das Routing funktioniert, wie sich das Frontend
> beim Backend authentifiziert und wie eine Todo-Liste geladen und angezeigt wird.
> ```

---

#### 5. Understand the data model

> **EN:**
> ```
> Show me the full data model of this application.
> What entities exist, what fields do they have, and how are they related?
> Also explain how the domain model differs from the JPA entity and the REST response.
> ```

> **DE:**
> ```
> Zeige mir das vollständige Datenmodell dieser Anwendung.
> Welche Entitäten gibt es, welche Felder haben sie und wie hängen sie zusammen?
> Erkläre auch, wie sich das Domain-Modell von der JPA-Entity und der REST-Response unterscheidet.
> ```

---

#### 6. Verify your understanding

Final self-test — if Claude answers this precisely, the goal is reached:

> **EN:**
> ```
> /plan If I wanted to add a new optional field "estimatedMinutes" (integer) to a Todo,
> list every file I would need to change, in the correct order,
> following the architecture rules of this project. Do not implement anything yet.
> ```

> **DE:**
> ```
> /plan Wenn ich ein neues optionales Feld "estimatedMinutes" (Integer) zu einem Todo
> hinzufügen wollte: liste alle Dateien auf, die ich ändern müsste, in der richtigen
> Reihenfolge, gemäß den Architekturregeln dieses Projekts. Implementiere noch nichts.
> ```

If Claude correctly walks through all layers (Domain → Command → Service → JPA Entity → REST → Angular), Task 1 is complete.

---

### Acceptance Criteria

- [ ] You can name the three backend layers and their responsibilities
- [ ] You can describe how data flows from database to Angular UI
- [ ] You can explain how JWT authentication works in this app
- [ ] You can list the files that need to change when adding a new field to a Todo

---

## Task 2 — Add Tags to Todos

**Goal:** Extend the application with a tag feature — end-to-end, from database to UI.

**Requirements:**
- Todos can have multiple tags (e.g. `work`, `urgent`, `shopping`)
- Tags are shared per user (not per list)
- Each tag has a name and a randomly assigned display color (stored in the database)
- Todos can be filtered by tag within a list
- Tags that are no longer used by any todo are automatically deleted

> This task uses **Claude Code** (`claude` CLI).  
> Run `claude` in the project root. Use `/plan` before any implementation step.

---

### Step-by-step with Claude Code

#### 1. Plan the implementation first

Before writing any code, have Claude create a detailed plan:

> **EN:**
> ```
> /plan I want to add a tag feature to this Todo app. Requirements:
> - Todos can have multiple tags
> - Tags are per user (not per list)
> - Each tag has a name and a random hex color (stored in DB)
> - Todos can be filtered by tag
> - Tags with no todos attached are automatically deleted
>
> Analyze the existing codebase and create a detailed implementation plan:
> which files need to be created or changed, in which order, following
> the hexagonal architecture of this project. Do not implement yet.
> ```

> **DE:**
> ```
> /plan Ich möchte ein Tag-Feature zu dieser Todo-App hinzufügen. Anforderungen:
> - Todos können mehrere Tags haben
> - Tags gehören zum Nutzer (nicht zur Liste)
> - Jeder Tag hat einen Namen und eine zufällige Hex-Farbe (in der DB gespeichert)
> - Todos können nach Tag gefiltert werden
> - Tags ohne zugeordnete Todos werden automatisch gelöscht
>
> Analysiere die bestehende Codebase und erstelle einen detaillierten Umsetzungsplan:
> welche Dateien erstellt oder geändert werden müssen, in welcher Reihenfolge,
> gemäß der hexagonalen Architektur dieses Projekts. Noch nichts implementieren.
> ```

---

#### 2. Database migration

> **EN:**
> ```
> Implement the Flyway database migration for the tag feature.
> Create a new migration file V4 that adds:
> - A `tags` table (id, owner_id, name, color, created_at)
> - A `todo_tags` join table (todo_id, tag_id)
> Follow the existing migration style in db/migration/.
> ```

> **DE:**
> ```
> Implementiere die Flyway-Datenbankmigration für das Tag-Feature.
> Erstelle eine neue Migrationsdatei V4 mit:
> - Einer Tabelle `tags` (id, owner_id, name, color, created_at)
> - Einer Join-Tabelle `todo_tags` (todo_id, tag_id)
> Halte dich an den Stil der bestehenden Migrationen in db/migration/.
> ```

---

#### 3. Backend — domain and application layer

> **EN:**
> ```
> Implement the Tag feature in the domain and application layers.
> - Add a Tag domain model with id, ownerId, name, color
> - Add use case interfaces: CreateTagUseCase, GetTagsUseCase, DeleteUnusedTagsUseCase
> - Add commands: CreateTagCommand
> - Add application service implementations
> Do not touch infrastructure yet. Follow the existing patterns exactly.
> ```

> **DE:**
> ```
> Implementiere das Tag-Feature in der Domain- und Application-Schicht.
> - Füge ein Tag-Domain-Modell hinzu: id, ownerId, name, color
> - Füge Use-Case-Interfaces hinzu: CreateTagUseCase, GetTagsUseCase, DeleteUnusedTagsUseCase
> - Füge Commands hinzu: CreateTagCommand
> - Implementiere die Application-Service-Klassen
> Noch keine Infrastruktur anfassen. Halte dich genau an die bestehenden Muster.
> ```

---

#### 4. Backend — persistence and REST

> **EN:**
> ```
> Implement the infrastructure layer for tags:
> - JPA entity TagJpaEntity and TodoTagJpaEntity (join table)
> - Spring Data repositories
> - Persistence adapter implementing the out-ports
> - REST: extend TodoResponse to include a list of tags (id, name, color)
> - REST: extend CreateTodoRequest and UpdateTodoRequest to accept a list of tag names
> - REST: add GET /tags endpoint to list all tags of the current user
> - Auto-delete unused tags after a todo is updated or deleted
> ```

> **DE:**
> ```
> Implementiere die Infrastrukturschicht für Tags:
> - JPA-Entity TagJpaEntity und TodoTagJpaEntity (Join-Tabelle)
> - Spring-Data-Repositories
> - Persistence-Adapter, der die Out-Ports implementiert
> - REST: TodoResponse um eine Tag-Liste erweitern (id, name, color)
> - REST: CreateTodoRequest und UpdateTodoRequest um Tag-Namen erweitern
> - REST: GET /tags Endpunkt für alle Tags des aktuellen Nutzers hinzufügen
> - Nicht genutzte Tags automatisch löschen, wenn ein Todo geändert oder gelöscht wird
> ```

---

#### 5. Frontend — model, service, form, filter

> **EN:**
> ```
> Implement the tag feature in the Angular frontend:
> - Add Tag model (id, name, color)
> - Add TagService with GET /tags
> - Extend the Todo model with a tags array
> - Extend TodoFormComponent: tag input with autocomplete from existing tags,
>   new tags are created on-the-fly, tags shown as colored chips
> - Extend TodoFilterComponent: add a tag filter dropdown
> - Extend TodoItemComponent: show tags as colored badges on each todo
> - The color of a new tag is randomly generated in the frontend and sent to the backend
> ```

> **DE:**
> ```
> Implementiere das Tag-Feature im Angular-Frontend:
> - Tag-Modell hinzufügen (id, name, color)
> - TagService mit GET /tags hinzufügen
> - Todo-Modell um ein tags-Array erweitern
> - TodoFormComponent erweitern: Tag-Eingabe mit Autocomplete aus bestehenden Tags,
>   neue Tags werden on-the-fly erstellt, Tags als farbige Chips anzeigen
> - TodoFilterComponent erweitern: Tag-Filter-Dropdown hinzufügen
> - TodoItemComponent erweitern: Tags als farbige Badges auf jedem Todo anzeigen
> - Die Farbe eines neuen Tags wird im Frontend zufällig generiert und ans Backend gesendet
> ```

---

#### 6. Run the tests

> **EN:**
> ```
> Run all backend tests and fix any failures:
> cd backend && ./mvnw test -Dspring.profiles.active=test
>
> Then run all frontend tests and fix any failures:
> cd frontend && npm test
> ```

> **DE:**
> ```
> Führe alle Backend-Tests aus und behebe Fehler:
> cd backend && ./mvnw test -Dspring.profiles.active=test
>
> Dann alle Frontend-Tests ausführen und Fehler beheben:
> cd frontend && npm test
> ```

---

### Acceptance Criteria

- [ ] Flyway migration V4 exists and creates `tags` and `todo_tags` tables
- [ ] `GET /lists/{listId}/todos` response includes a `tags` array per todo
- [ ] `GET /tags` returns all tags of the current user
- [ ] Tags with no todos are automatically removed from the database
- [ ] Todos can be filtered by tag in the UI
- [ ] Tags appear as colored chips/badges on todo items
- [ ] Tag color is randomly assigned when a new tag is created
- [ ] Backend tests pass: `cd backend && ./mvnw test -Dspring.profiles.active=test`
- [ ] Frontend tests pass: `cd frontend && npm test`
