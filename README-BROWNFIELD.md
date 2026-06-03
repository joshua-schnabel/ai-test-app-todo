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

#### 2. Give Claude an overview prompt first

Claude Code has no prior context about this project (the memory files were deleted). Start with:

> **Prompt:**
> ```
> Explore this codebase. Read README.md and all markdown files you find. 
> Then explore the folder structure of backend/ and frontend/. 
> Give me a summary of: the technology stack, the architecture, how backend and 
> frontend communicate, and how the data flows from database to UI.
> ```

---

#### 3. Dive into the backend architecture

> **Prompt:**
> ```
> Explain the backend package structure in detail. 
> What are the three main layers and what is each layer responsible for? 
> Show me one concrete example of how a single use case (e.g. creating a todo) 
> flows through all layers — from the REST controller down to the database.
> ```

---

#### 4. Understand the frontend structure

> **Prompt:**
> ```
> Explore the Angular frontend under frontend/src/app/. 
> Explain the folder structure, how routing works, how the frontend authenticates 
> with the backend, and how a todo list is loaded and displayed.
> ```

---

#### 5. Understand the data model

> **Prompt:**
> ```
> Show me the full data model of this application. 
> What entities exist, what fields do they have, and how are they related? 
> Also show me how the domain model differs from the JPA entity and the REST response.
> ```

---

#### 6. Verify your understanding

After the exploration, test yourself with this prompt:

> **Prompt:**
> ```
> If I wanted to add a new optional field "estimatedMinutes" (integer) to a Todo, 
> list every single file I would need to change, in the correct order, 
> following the architecture rules of this project. Do not implement it yet.
> ```

If Claude can answer this precisely, you are ready for the next task.

---

### Acceptance Criteria

- [ ] You can name the three backend layers and their responsibilities
- [ ] You can describe how data flows from database to Angular UI
- [ ] You can explain how JWT authentication works in this app
- [ ] You can list the files that need to change when adding a new field to a Todo
