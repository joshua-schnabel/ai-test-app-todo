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

#### 2. Mit `/plan` starten — Übersicht verschaffen

`/plan` weist Claude Code an, zuerst einen Erkundungsplan zu erstellen, bevor es Dateien liest. Das verhindert zielloses Durchsuchen.

> **Prompt:**
> ```
> /plan Mach dich mit dieser Codebase vertraut. Lies README.md und alle Markdown-Dateien.
> Erkunde dann die Ordnerstruktur von backend/ und frontend/.
> Fasse zusammen: den Tech-Stack, die Architektur, wie Backend und Frontend 
> kommunizieren und wie Daten von der Datenbank bis zur UI fließen.
> ```

---

#### 3. Backend-Architektur verstehen

> **Prompt:**
> ```
> Erkläre die Backend-Paketstruktur im Detail.
> Welche drei Hauptschichten gibt es und wofür ist jede zuständig?
> Zeige mir ein konkretes Beispiel, wie ein Use Case (z.B. Todo erstellen)
> durch alle Schichten fließt – vom REST-Controller bis zur Datenbank.
> ```

---

#### 4. Frontend-Struktur verstehen

> **Prompt:**
> ```
> Erkunde das Angular-Frontend unter frontend/src/app/.
> Erkläre die Ordnerstruktur, wie das Routing funktioniert, wie sich das Frontend
> beim Backend authentifiziert und wie eine Todo-Liste geladen und angezeigt wird.
> ```

---

#### 5. Datenmodell verstehen

> **Prompt:**
> ```
> Zeige mir das vollständige Datenmodell dieser Anwendung.
> Welche Entitäten gibt es, welche Felder haben sie und wie hängen sie zusammen?
> Erkläre auch, wie sich das Domain-Modell von der JPA-Entity und der REST-Response unterscheidet.
> ```

---

#### 6. Verständnis prüfen

Abschluss-Test — wenn Claude das präzise beantwortet, ist das Ziel erreicht:

> **Prompt:**
> ```
> /plan Wenn ich ein neues optionales Feld "estimatedMinutes" (Integer) zu einem Todo
> hinzufügen wollte: liste alle Dateien auf, die ich ändern müsste, in der richtigen
> Reihenfolge, gemäß den Architekturregeln dieses Projekts. Implementiere noch nichts.
> ```

Wenn Claude alle Schichten korrekt durchläuft (Domain → Command → Service → JPA Entity → REST → Angular), ist Task 1 abgeschlossen.

---

### Acceptance Criteria

- [ ] You can name the three backend layers and their responsibilities
- [ ] You can describe how data flows from database to Angular UI
- [ ] You can explain how JWT authentication works in this app
- [ ] You can list the files that need to change when adding a new field to a Todo
