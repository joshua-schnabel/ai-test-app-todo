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
