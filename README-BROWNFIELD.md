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

## How to work with Claude Code

Start Claude Code in the project root:

```bash
claude
```

**Tips:**
- Use `/plan` before any non-trivial task — Claude will create an exploration or implementation plan before touching files. This prevents errors and keeps the architecture clean.
- Be specific about what you want to achieve, not just what to do. Claude makes better decisions when it understands the goal.
- If Claude gets stuck or goes in the wrong direction, describe the expected result and ask it to reconsider.
- Prompts below are **hints for when you get stuck** — try to formulate your own instructions first.

---

## Task 1 — Get Familiar with the Codebase

**Your goal:** Understand the application well enough to safely extend it in Task 2.

After this task you should be able to answer:
- What is the tech stack (frontend, backend, database, auth)?
- What are the three backend layers and what does each one do?
- How does a request flow from the Angular UI through to the database and back?
- How does authentication work?
- If you had to add a new field to a Todo — which files would you need to touch, and in what order?

**How to approach it:** Start Claude Code and give it free rein to explore the codebase. Ask it to explain things to you. Read the code it references. The goal is *your* understanding, not just Claude's.

**Done when:** You can confidently answer the questions above without looking at the code.

---

<details>
<summary>💡 Hints — stuck? Expand for example prompts</summary>

**Overview (EN):**
```
/plan Explore this codebase. Read README.md and all markdown files you find.
Then explore the folder structure of backend/ and frontend/.
Summarize: the tech stack, the architecture, how backend and frontend
communicate, and how data flows from database to UI.
```
**Overview (DE):**
```
/plan Mach dich mit dieser Codebase vertraut. Lies README.md und alle Markdown-Dateien.
Erkunde dann die Ordnerstruktur von backend/ und frontend/.
Fasse zusammen: den Tech-Stack, die Architektur, wie Backend und Frontend
kommunizieren und wie Daten von der Datenbank bis zur UI fließen.
```

---

**Backend architecture (EN):**
```
Explain the backend package structure in detail.
What are the three main layers and what is each layer responsible for?
Show me one concrete example of how a single use case (e.g. creating a todo)
flows through all layers — from the REST controller down to the database.
```
**Backend architecture (DE):**
```
Erkläre die Backend-Paketstruktur im Detail.
Welche drei Hauptschichten gibt es und wofür ist jede zuständig?
Zeige mir ein konkretes Beispiel, wie ein Use Case (z.B. Todo erstellen)
durch alle Schichten fließt – vom REST-Controller bis zur Datenbank.
```

---

**Self-test (EN):**
```
/plan If I wanted to add a new optional field "estimatedMinutes" (integer) to a Todo,
list every file I would need to change, in the correct order,
following the architecture rules of this project. Do not implement anything yet.
```
**Self-test (DE):**
```
/plan Wenn ich ein neues optionales Feld "estimatedMinutes" (Integer) zu einem Todo
hinzufügen wollte: liste alle Dateien auf, die ich ändern müsste, in der richtigen
Reihenfolge, gemäß den Architekturregeln dieses Projekts. Implementiere noch nichts.
```

</details>

---

### Acceptance Criteria

- [ ] You can name the three backend layers and their responsibilities
- [ ] You can describe how data flows from database to Angular UI
- [ ] You can explain how JWT authentication works in this app
- [ ] You can list the files that need to change when adding a new field to a Todo

---

## Task 2 — Add Tags to Todos

**Your goal:** Extend the application with a tag feature — end-to-end, from the database to the UI.

**What the feature should do:**
- Todos can have multiple tags (e.g. `work`, `urgent`, `shopping`)
- Tags belong to the user, not to a specific list
- Each tag has a name and a color — the color is randomly chosen when the tag is first created
- The todo list can be filtered by tag
- Tags that are no longer attached to any todo are automatically cleaned up

**How to approach it:** Use Claude Code to implement this feature. Start with `/plan` — let Claude analyze the codebase and propose an implementation plan before writing any code. Review the plan, push back if something looks wrong, then let it implement step by step.

Pay attention to the architecture: the backend uses hexagonal architecture with strict layer separation. Claude should follow the same patterns already used in the codebase.

**Done when:** All acceptance criteria below are met and both test suites pass.

---

<details>
<summary>💡 Hints — stuck? Expand for example prompts</summary>

**Planning (EN):**
```
/plan I want to add a tag feature to this Todo app. Requirements:
- Todos can have multiple tags
- Tags are per user (not per list)
- Each tag has a name and a random hex color (stored in DB)
- Todos can be filtered by tag
- Tags with no todos attached are automatically deleted

Analyze the existing codebase and create a detailed implementation plan:
which files need to be created or changed, in which order, following
the hexagonal architecture of this project. Do not implement yet.
```
**Planning (DE):**
```
/plan Ich möchte ein Tag-Feature zu dieser Todo-App hinzufügen. Anforderungen:
- Todos können mehrere Tags haben
- Tags gehören zum Nutzer (nicht zur Liste)
- Jeder Tag hat einen Namen und eine zufällige Hex-Farbe (in der DB gespeichert)
- Todos können nach Tag gefiltert werden
- Tags ohne zugeordnete Todos werden automatisch gelöscht

Analysiere die bestehende Codebase und erstelle einen detaillierten Umsetzungsplan:
welche Dateien erstellt oder geändert werden müssen, in welcher Reihenfolge,
gemäß der hexagonalen Architektur dieses Projekts. Noch nichts implementieren.
```

---

**Database migration (EN):**
```
Implement the Flyway database migration for the tag feature.
Create migration file V4 that adds a `tags` table (id, owner_id, name, color, created_at)
and a `todo_tags` join table (todo_id, tag_id). Follow the existing migration style.
```
**Database migration (DE):**
```
Implementiere die Flyway-Datenbankmigration für das Tag-Feature.
Erstelle Migrationsdatei V4 mit Tabelle `tags` (id, owner_id, name, color, created_at)
und Join-Tabelle `todo_tags` (todo_id, tag_id). Halte dich an den bestehenden Stil.
```

---

**Backend domain & application (EN):**
```
Implement the Tag feature in the domain and application layers only.
Add a Tag domain model, use case interfaces, commands, and service implementations.
Do not touch infrastructure yet. Follow the existing patterns exactly.
```
**Backend domain & application (DE):**
```
Implementiere das Tag-Feature nur in der Domain- und Application-Schicht.
Füge ein Tag-Domain-Modell, Use-Case-Interfaces, Commands und Service-Implementierungen hinzu.
Noch keine Infrastruktur anfassen. Halte dich genau an die bestehenden Muster.
```

---

**Backend persistence & REST (EN):**
```
Implement the infrastructure layer for tags: JPA entities, repositories, persistence adapter.
Extend TodoResponse to include tags (id, name, color). Extend Create/UpdateTodoRequest
to accept tag names. Add GET /tags endpoint. Auto-delete unused tags on todo update/delete.
```
**Backend persistence & REST (DE):**
```
Implementiere die Infrastrukturschicht für Tags: JPA-Entities, Repositories, Persistence-Adapter.
Erweitere TodoResponse um Tags (id, name, color). Erweitere Create/UpdateTodoRequest um Tag-Namen.
Füge GET /tags hinzu. Lösche nicht genutzte Tags automatisch bei Todo-Update/-Delete.
```

---

**Frontend (EN):**
```
Implement the tag feature in the Angular frontend:
- Add Tag model and TagService
- Extend TodoFormComponent with a tag input (autocomplete, colored chips, new tags on-the-fly)
- Extend TodoFilterComponent with a tag filter
- Extend TodoItemComponent to show tags as colored badges
- Random tag color is generated in the frontend and sent to the backend
```
**Frontend (DE):**
```
Implementiere das Tag-Feature im Angular-Frontend:
- Tag-Modell und TagService hinzufügen
- TodoFormComponent: Tag-Eingabe mit Autocomplete, farbige Chips, neue Tags on-the-fly
- TodoFilterComponent: Tag-Filter hinzufügen
- TodoItemComponent: Tags als farbige Badges anzeigen
- Zufällige Tag-Farbe wird im Frontend generiert und ans Backend gesendet
```

---

**Tests (EN):**
```
Run all backend tests and fix any failures:
cd backend && ./mvnw test -Dspring.profiles.active=test

Then run all frontend tests and fix any failures:
cd frontend && npm test
```
**Tests (DE):**
```
Führe alle Backend-Tests aus und behebe Fehler:
cd backend && ./mvnw test -Dspring.profiles.active=test

Dann alle Frontend-Tests ausführen und Fehler beheben:
cd frontend && npm test
```

</details>

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
