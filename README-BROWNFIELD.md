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

## Chapter 0 — Reset AI Memory (Start Fresh)

Before starting the tasks, reset all AI memory files so Claude Code begins without any prior knowledge of this codebase. This simulates a realistic brownfield scenario where the AI has no context.

**Files to delete:**

| File | Purpose |
|---|---|
| `.github/copilot-instructions.md` | Persistent context loaded automatically by GitHub Copilot |
| `.plan/plan.md` | Technical implementation plan written during development |
| `README-BROWNFIELD.md` | This file — contains task descriptions and hint prompts |

**How to delete (PowerShell):**

```powershell
Remove-Item ".github\copilot-instructions.md" -ErrorAction SilentlyContinue
Remove-Item ".plan\plan.md" -ErrorAction SilentlyContinue
Remove-Item "README-BROWNFIELD.md" -ErrorAction SilentlyContinue
```

**How to delete (Bash/macOS/Linux):**

```bash
rm -f .github/copilot-instructions.md .plan/plan.md README-BROWNFIELD.md
```

> ⚠️ Do not commit these deletions. The files are intentionally kept in the repository so others can reset them too.  
> After the tasks, you can restore them with `git restore .github/copilot-instructions.md .plan/plan.md README-BROWNFIELD.md`.

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

---

## Task 3 — Find and Fix a Security Vulnerability

**Your goal:** Identify and fix a security vulnerability that has been deliberately introduced into the codebase.

The application has a bug where an authenticated user can **modify another user's data** without being the owner. This is a real-world class of vulnerability known as **IDOR — Insecure Direct Object Reference**.

You don't know which endpoint is affected or where in the code the check is missing. Your job is to find it.

**How to approach it:** Use Claude Code to conduct a security review of the backend. Think about what an attacker could do: they are authenticated, they know (or can guess) UUIDs, and they want to tamper with another user's data. Ask Claude to review the authorization logic — not just whether endpoints require a JWT, but whether each operation verifies that the current user actually *owns* the resource being modified.

Once you've found the vulnerability, fix it properly and verify with a test that the exploit no longer works.

**Done when:** The vulnerability is fixed, the fix is covered by a test, and all existing tests still pass.

---

<details>
<summary>💡 Hints — stuck? Expand for example prompts</summary>

**Security review (EN):**
```
/plan Conduct a security review of the backend authorization logic.
For every write operation (create, update, delete, complete, reopen) on todos and lists,
check whether the application correctly verifies that the current user owns the resource.
Look for any operation where the ownership check might be missing or incomplete.
Do not fix anything yet — just report your findings.
```
**Security review (DE):**
```
/plan Führe ein Security-Review der Backend-Autorisierungslogik durch.
Prüfe für jede Schreiboperation (create, update, delete, complete, reopen) auf Todos und Listen,
ob die Anwendung korrekt sicherstellt, dass der aktuelle Nutzer die Ressource besitzt.
Suche nach Operationen, bei denen der Ownership-Check fehlt oder unvollständig ist.
Noch nichts fixen – nur Befunde melden.
```

---

**Exploit test (EN):**
```
Write a Spring Boot integration test that proves the vulnerability exists:
- Create two users (User A and User B)
- User A creates a todo list and a todo
- User B tries to modify User A's todo using PUT /lists/{listId}/todos/{todoId}
- The test asserts that the response is 403 Forbidden
Run the test and confirm it currently FAILS (meaning the vulnerability is present).
```
**Exploit test (DE):**
```
Schreibe einen Spring Boot Integrationstest, der die Schwachstelle beweist:
- Erstelle zwei Nutzer (Nutzer A und Nutzer B)
- Nutzer A erstellt eine Liste und ein Todo
- Nutzer B versucht, das Todo von Nutzer A zu ändern via PUT /lists/{listId}/todos/{todoId}
- Der Test prüft, dass die Antwort 403 Forbidden ist
Führe den Test aus und bestätige, dass er aktuell FEHLSCHLÄGT (Schwachstelle vorhanden).
```

---

**Fix (EN):**
```
Fix the security vulnerability you found. Add the missing ownership check
following the exact same pattern used in the other methods of the same class.
Then re-run the test to confirm it now passes.
```
**Fix (DE):**
```
Behebe die gefundene Schwachstelle. Füge den fehlenden Ownership-Check hinzu,
genau nach dem Muster, das in den anderen Methoden derselben Klasse verwendet wird.
Führe dann den Test erneut aus und bestätige, dass er jetzt besteht.
```

</details>

---

### Acceptance Criteria

- [ ] The vulnerable endpoint is identified (hint: it's a write operation on todos)
- [ ] A test exists that proves the exploit attempt returns `403 Forbidden`
- [ ] The fix adds the missing ownership check in the correct layer (application service, not controller)
- [ ] All backend tests pass: `cd backend && ./mvnw test -Dspring.profiles.active=test`

---

## Task 4 — Refactor the Backend: CQRS Split + Sort Value Object

**Your goal:** Improve the structure of `TodoApplicationService` through two targeted refactorings, without changing any behavior.

**What needs to change:**

1. **CQRS split:** `TodoApplicationService` currently implements 6 use case interfaces and mixes read and write operations in a single class. Split it into:
   - `TodoCommandService` — handles write operations: create, update, delete, complete, reopen
   - `TodoQueryService` — handles read operations: getTodos (including filter and sort logic)

2. **Sort value object:** The `resolveComparator` method in the current service takes raw `String sortBy, String sortDir` parameters and contains complex, hard-to-follow branching logic. Extract this into a `TodoSortCriteria` value object in the domain or application layer that encapsulates the parsing and exposes a `toComparator()` method.

**How to approach it:** Start with a plan — Claude should map out exactly which classes will be created, which interfaces move where, and how `ApplicationBeanConfig` needs to change. The refactoring must be purely structural: no business logic changes. All existing tests must still pass after each step.

Do one refactoring at a time. Get the tests green between steps.

**Done when:** The code is split as described, the sort logic is encapsulated in a value object, and all tests pass.

---

<details>
<summary>💡 Hints — stuck? Expand for example prompts</summary>

**Plan (EN):**
```
/plan Refactor TodoApplicationService into two classes:
- TodoCommandService: create, update, delete, complete, reopen
- TodoQueryService: getTodos with filter and sort logic

Also extract the sort logic into a TodoSortCriteria value object
that takes sortBy and sortDir strings and exposes a toComparator() method.

Map out every file that needs to change, including ApplicationBeanConfig
where the beans are wired. Do not implement yet.
```
**Plan (DE):**
```
/plan Refactore TodoApplicationService in zwei Klassen:
- TodoCommandService: create, update, delete, complete, reopen
- TodoQueryService: getTodos mit Filter- und Sortierlogik

Extrahiere außerdem die Sortierlogik in ein TodoSortCriteria-Value-Object,
das sortBy- und sortDir-Strings entgegennimmt und eine toComparator()-Methode anbietet.

Liste alle Dateien auf, die geändert werden müssen, inkl. ApplicationBeanConfig.
Noch nichts implementieren.
```

---

**CQRS split (EN):**
```
Implement the CQRS split: create TodoCommandService and TodoQueryService.
Move the use case interfaces accordingly. Update ApplicationBeanConfig to wire both beans.
Keep TodoSortCriteria for the next step — leave sort logic in place for now.
Run the tests after this step: cd backend && ./mvnw test -Dspring.profiles.active=test
```
**CQRS split (DE):**
```
Implementiere den CQRS-Split: Erstelle TodoCommandService und TodoQueryService.
Verschiebe die Use-Case-Interfaces entsprechend. Aktualisiere ApplicationBeanConfig.
TodoSortCriteria kommt im nächsten Schritt – Sortierlogik vorerst stehen lassen.
Tests nach diesem Schritt ausführen: cd backend && ./mvnw test -Dspring.profiles.active=test
```

---

**Sort value object (EN):**
```
Extract the sort logic from TodoQueryService into a TodoSortCriteria value object.
It should accept sortBy and sortDir as constructor parameters and expose a toComparator() method
returning a Comparator<Todo>. Place it in the application or domain layer.
Run the tests again after this step.
```
**Sort value object (DE):**
```
Extrahiere die Sortierlogik aus TodoQueryService in ein TodoSortCriteria-Value-Object.
Es soll sortBy und sortDir als Konstruktorparameter entgegennehmen und eine toComparator()-Methode
zurückgeben (Comparator<Todo>). Platziere es in der Application- oder Domain-Schicht.
Tests danach ausführen.
```

</details>

---

### Acceptance Criteria

- [ ] `TodoApplicationService` no longer exists — replaced by `TodoCommandService` and `TodoQueryService`
- [ ] `TodoSortCriteria` is a dedicated class with a `toComparator()` method
- [ ] `ApplicationBeanConfig` wires both new service beans correctly
- [ ] No business logic has changed — only structure
- [ ] All backend tests pass: `cd backend && ./mvnw test -Dspring.profiles.active=test`

---

## Task 5 — Refactor the Frontend: Extract Todo State Service

**Your goal:** `TodoListViewComponent` currently manages too many responsibilities at once — HTTP calls, loading state, saving state, filter state, sort state, and UI logic all live in one component class. Extract the non-UI state into a dedicated Angular service.

**What needs to change:**

Create a `TodoStateService` that owns:
- The list of todos
- Loading and saving state (`isLoading`, `isSaving`)
- Current filter and sort criteria
- All HTTP interactions (delegating to `TodoService`)
- The `loadTodos()`, `saveTodo()`, `deleteTodo()`, `toggleTodo()` methods

`TodoListViewComponent` should become a thin UI component that:
- Injects `TodoStateService`
- Reacts to state changes via observables or signals
- Only handles UI decisions (show/hide form, confirm dialogs)

**How to approach it:** Start with a plan. The service should be `providedIn: 'root'` or scoped to the route — Claude should propose and justify the choice. The component must still work correctly after the refactoring, including the `ChangeDetectorRef.markForCheck()` calls where necessary.

**Done when:** `TodoListViewComponent` contains no direct HTTP logic, state is owned by `TodoStateService`, and all frontend tests pass.

---

<details>
<summary>💡 Hints — stuck? Expand for example prompts</summary>

**Plan (EN):**
```
/plan Refactor TodoListViewComponent in the Angular frontend.
The component currently handles HTTP, loading state, saving state, filter, and sort.
Extract all non-UI state and logic into a new TodoStateService.
The component should only handle UI decisions.
Map out exactly which properties and methods move to the service,
how the component will react to state changes, and whether the service
should be providedIn 'root' or scoped to the route. Do not implement yet.
```
**Plan (DE):**
```
/plan Refactore TodoListViewComponent im Angular-Frontend.
Die Komponente verwaltet aktuell HTTP, Loading-State, Saving-State, Filter und Sortierung.
Extrahiere den gesamten Nicht-UI-State in einen neuen TodoStateService.
Die Komponente soll nur noch UI-Entscheidungen treffen.
Liste genau auf, welche Properties und Methoden in den Service wandern,
wie die Komponente auf State-Änderungen reagiert und ob der Service
in 'root' oder route-scoped bereitgestellt werden soll. Noch nicht implementieren.
```

---

**Implementation (EN):**
```
Implement the TodoStateService refactoring.
Create the service with RxJS BehaviorSubjects or Angular signals for todos,
isLoading, isSaving, currentFilter, and currentSort.
Move all HTTP logic from TodoListViewComponent into the service.
Update the component to subscribe to the service's state.
Preserve the ChangeDetectorRef.markForCheck() calls where still needed.
```
**Implementation (DE):**
```
Implementiere das TodoStateService-Refactoring.
Erstelle den Service mit RxJS BehaviorSubjects oder Angular Signals für todos,
isLoading, isSaving, currentFilter und currentSort.
Verschiebe die gesamte HTTP-Logik aus TodoListViewComponent in den Service.
Aktualisiere die Komponente so, dass sie den State des Service abonniert.
Behalte ChangeDetectorRef.markForCheck()-Aufrufe wo nötig.
```

---

**Tests (EN):**
```
Run the frontend tests and fix any failures:
cd frontend && npm test
```
**Tests (DE):**
```
Frontend-Tests ausführen und Fehler beheben:
cd frontend && npm test
```

</details>

---

### Acceptance Criteria

- [ ] `TodoStateService` exists and owns all non-UI state
- [ ] `TodoListViewComponent` contains no direct calls to `TodoService` for todos (only via `TodoStateService`)
- [ ] State is exposed via observables or signals — no direct property mutation from outside the service
- [ ] All frontend tests pass: `cd frontend && npm test`

---

## ⭐ Bonus Task 6 — Shared Lists with Permissions

**Your goal:** Extend the application to allow users to share their todo lists with other users, with configurable access levels.

This is an open-ended architectural challenge. There is no single correct solution — the goal is to design and implement a sound, maintainable feature that integrates cleanly with the existing hexagonal architecture.

**What the feature should do:**

- A list owner can invite another user by email address to access their list
- Two permission levels exist: `VIEWER` (read-only) and `EDITOR` (can create, edit, delete, complete todos)
- Shared lists appear in the invited user's list view, visually distinct from their own lists
- The owner can revoke access at any time
- A user can only see and access lists they own or have been explicitly invited to
- The owner remains the only one who can rename or delete the list

**Key design questions Claude should address before implementing:**
- Where does the `list_members` table belong in the hexagonal model?
- How does the authorization logic change? The current `requireOwnedList` check must evolve to `requireListAccess(userId, listId, requiredRole)`
- How does `GET /lists` return both owned and shared lists?
- How is the invited user looked up — does the backend expose a user search endpoint, or does the frontend send an email directly?

**How to approach it:** This task requires more upfront design than the previous ones. Start with `/plan` and have Claude propose the full data model, API design, and authorization strategy before writing a single line of code. Review the plan carefully — shared data is where most multi-user bugs live.

**Done when:** All acceptance criteria are met, authorization is airtight (Viewer cannot write, non-members cannot access), and all tests pass.

---

<details>
<summary>💡 Hints — stuck? Expand for example prompts</summary>

**Design (EN):**
```
/plan Design the "shared lists" feature for this Todo app.
Requirements:
- List owner can share a list with another user by email (VIEWER or EDITOR role)
- Shared lists appear in the invited user's list view
- VIEWER can only read todos; EDITOR can create/edit/delete/complete todos
- Owner can revoke access; only owner can rename or delete the list

Propose: the database schema (new tables/columns), all new REST endpoints,
how the authorization logic changes in the application layer,
and how GET /lists changes to include shared lists.
Do not implement yet.
```
**Design (DE):**
```
/plan Entwirf das "Geteilte Listen"-Feature für diese Todo-App.
Anforderungen:
- Listenbesitzer kann eine Liste per E-Mail mit einem anderen Nutzer teilen (VIEWER oder EDITOR)
- Geteilte Listen erscheinen in der Listenansicht des eingeladenen Nutzers
- VIEWER kann nur lesen; EDITOR kann Todos erstellen/bearbeiten/löschen/erledigen
- Besitzer kann Zugriff entziehen; nur Besitzer kann Liste umbenennen oder löschen

Schlage vor: Datenbankschema (neue Tabellen/Spalten), alle neuen REST-Endpunkte,
wie sich die Autorisierungslogik in der Application-Schicht ändert
und wie GET /lists auch geteilte Listen zurückgibt.
Noch nichts implementieren.
```

---

**Database migration (EN):**
```
Implement Flyway migration V5 (or next version) for the shared lists feature.
Add a list_members table: list_id (FK), user_id (FK), role (VIEWER/EDITOR), invited_at.
The owner is NOT stored here — ownership stays on the lists table.
```
**Database migration (DE):**
```
Implementiere Flyway-Migration V5 (oder nächste Version) für das Geteilte-Listen-Feature.
Füge eine Tabelle list_members hinzu: list_id (FK), user_id (FK), role (VIEWER/EDITOR), invited_at.
Der Besitzer wird NICHT hier gespeichert – Eigentumsrecht bleibt in der lists-Tabelle.
```

---

**Backend authorization (EN):**
```
Refactor the authorization logic in the application layer.
Replace requireOwnedList() with two new checks:
- requireListAccess(userId, listId, Role.VIEWER) — for read operations
- requireListAccess(userId, listId, Role.EDITOR) — for write operations on todos
- requireListOwnership(userId, listId) — for rename and delete list operations

Add new use cases: ShareListUseCase, RevokeListAccessUseCase, GetListMembersUseCase.
```
**Backend authorization (DE):**
```
Refactore die Autorisierungslogik in der Application-Schicht.
Ersetze requireOwnedList() durch zwei neue Prüfungen:
- requireListAccess(userId, listId, Role.VIEWER) – für Leseoperationen
- requireListAccess(userId, listId, Role.EDITOR) – für Schreiboperationen auf Todos
- requireListOwnership(userId, listId) – für Liste umbenennen und löschen

Füge neue Use Cases hinzu: ShareListUseCase, RevokeListAccessUseCase, GetListMembersUseCase.
```

---

**Frontend (EN):**
```
Implement the frontend for shared lists:
- Extend GET /lists response to include a 'role' field (OWNER / EDITOR / VIEWER)
- Show shared lists with a visual indicator (e.g. a small icon or different border color)
- Add a share button on each owned list card that opens a form: enter email + select role
- Show current members of a list and allow the owner to revoke access
- Hide edit/delete todo buttons for VIEWER role
```
**Frontend (DE):**
```
Implementiere das Frontend für geteilte Listen:
- GET /lists Response um ein 'role'-Feld erweitern (OWNER / EDITOR / VIEWER)
- Geteilte Listen mit einem visuellen Hinweis anzeigen (z.B. Icon oder andere Randfarbe)
- Share-Button auf eigenen Listenkarten: Formular mit E-Mail-Eingabe + Rollenauswahl
- Aktuelle Mitglieder einer Liste anzeigen, Besitzer kann Zugriff entziehen
- Bearbeiten/Löschen-Buttons für VIEWER-Rolle ausblenden
```

---

**Security test (EN):**
```
Write integration tests that verify:
1. A VIEWER cannot create, update, delete, or complete todos → 403
2. A non-member cannot access the list at all → 403
3. An EDITOR can create and complete todos but cannot rename or delete the list → 403
4. The owner can do everything
Run all tests: cd backend && ./mvnw test -Dspring.profiles.active=test
```
**Security test (DE):**
```
Schreibe Integrationstests, die prüfen:
1. Ein VIEWER kann Todos nicht erstellen, bearbeiten, löschen oder erledigen → 403
2. Ein Nicht-Mitglied hat gar keinen Zugriff auf die Liste → 403
3. Ein EDITOR kann Todos erstellen und erledigen, aber die Liste nicht umbenennen/löschen → 403
4. Der Besitzer darf alles
Tests ausführen: cd backend && ./mvnw test -Dspring.profiles.active=test
```

</details>

---

### Acceptance Criteria

- [ ] A `list_members` table exists (Flyway migration)
- [ ] `GET /lists` returns both owned and shared lists, each with a `role` field
- [ ] `POST /lists/{listId}/members` allows the owner to share a list by email + role
- [ ] `DELETE /lists/{listId}/members/{userId}` allows the owner to revoke access
- [ ] VIEWER cannot write (create/update/delete/complete/reopen todos) → `403`
- [ ] Non-members cannot read or write the list → `403`
- [ ] Only the owner can rename or delete the list → `403` for members
- [ ] Shared lists are visually distinguishable in the UI
- [ ] All backend tests pass: `cd backend && ./mvnw test -Dspring.profiles.active=test`
- [ ] All frontend tests pass: `cd frontend && npm test`
