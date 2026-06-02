package de.joshuaschnabel.todo.infrastruktur.presentation.rest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.joshuaschnabel.todo.infrastruktur.persistence.db.repository.SpringDataTodoListRepository;
import de.joshuaschnabel.todo.infrastruktur.persistence.db.repository.SpringDataTodoRepository;
import de.joshuaschnabel.todo.infrastruktur.persistence.db.repository.SpringDataUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SpringDataTodoRepository todoRepository;
    @Autowired
    private SpringDataTodoListRepository todoListRepository;
    @Autowired
    private SpringDataUserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        todoRepository.deleteAll();
        todoListRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void postCreatesTodo() throws Exception {
        register("alice@example.com", "password123");
        String token = login("alice@example.com", "password123");
        UUID listId = createList(token, "Arbeit");

        mockMvc.perform(post("/api/lists/{listId}/todos", listId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Aufgabe 1", "description", "Test", "priority", "HIGH"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Aufgabe 1"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void getTodosOfList() throws Exception {
        register("bob@example.com", "password123");
        String token = login("bob@example.com", "password123");
        UUID listId = createList(token, "Liste");
        createTodo(token, listId, Map.of("title", "A", "priority", "MEDIUM"));
        createTodo(token, listId, Map.of("title", "B", "priority", "LOW"));

        mockMvc.perform(get("/api/lists/{listId}/todos", listId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void putUpdatesTodo() throws Exception {
        register("carol@example.com", "password123");
        String token = login("carol@example.com", "password123");
        UUID listId = createList(token, "Liste");
        UUID todoId = createTodo(token, listId, Map.of("title", "Alt", "priority", "LOW"));

        mockMvc.perform(put("/api/lists/{listId}/todos/{todoId}", listId, todoId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Neu", "description", "Aktualisiert", "priority", "HIGH"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Neu")))
                .andExpect(jsonPath("$.priority", is("HIGH")));
    }

    @Test
    void deleteRemovesTodo() throws Exception {
        register("dave@example.com", "password123");
        String token = login("dave@example.com", "password123");
        UUID listId = createList(token, "Liste");
        UUID todoId = createTodo(token, listId, Map.of("title", "Löschen", "priority", "MEDIUM"));

        mockMvc.perform(delete("/api/lists/{listId}/todos/{todoId}", listId, todoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void patchCompleteSetsDone() throws Exception {
        register("erin@example.com", "password123");
        String token = login("erin@example.com", "password123");
        UUID listId = createList(token, "Liste");
        UUID todoId = createTodo(token, listId, Map.of("title", "Erledigen", "priority", "MEDIUM"));

        mockMvc.perform(patch("/api/lists/{listId}/todos/{todoId}/complete", listId, todoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DONE")));
    }

    @Test
    void patchReopenSetsOpen() throws Exception {
        register("frank@example.com", "password123");
        String token = login("frank@example.com", "password123");
        UUID listId = createList(token, "Liste");
        UUID todoId = createTodo(token, listId, Map.of("title", "Wieder öffnen", "priority", "MEDIUM"));
        mockMvc.perform(patch("/api/lists/{listId}/todos/{todoId}/complete", listId, todoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/lists/{listId}/todos/{todoId}/reopen", listId, todoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("OPEN")));
    }

    @Test
    void accessToForeignTodoReturnsForbidden() throws Exception {
        register("gina@example.com", "password123");
        String ownerToken = login("gina@example.com", "password123");
        UUID listId = createList(ownerToken, "Privat");
        UUID todoId = createTodo(ownerToken, listId, Map.of("title", "Geheim", "priority", "HIGH"));

        register("hans@example.com", "password123");
        String attackerToken = login("hans@example.com", "password123");

        mockMvc.perform(get("/api/lists/{listId}/todos/{todoId}", listId, todoId)
                        .header("Authorization", "Bearer " + attackerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void filterStatusOpen() throws Exception {
        register("ida@example.com", "password123");
        String token = login("ida@example.com", "password123");
        UUID listId = createList(token, "Liste");
        UUID openId = createTodo(token, listId, Map.of("title", "Offen", "priority", "MEDIUM"));
        UUID doneId = createTodo(token, listId, Map.of("title", "Fertig", "priority", "MEDIUM"));
        mockMvc.perform(patch("/api/lists/{listId}/todos/{todoId}/complete", listId, doneId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/lists/{listId}/todos", listId)
                        .param("status", "open")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(openId.toString())));
    }

    @Test
    void filterDueOverdue() throws Exception {
        register("julia@example.com", "password123");
        String token = login("julia@example.com", "password123");
        UUID listId = createList(token, "Liste");
        UUID overdueId = createTodo(token, listId, Map.of("title", "Überfällig", "priority", "HIGH", "dueDate", LocalDate.now().minusDays(1).toString()));
        UUID todayId = createTodo(token, listId, Map.of("title", "Heute", "priority", "MEDIUM", "dueDate", LocalDate.now().toString()));
        UUID doneOverdueId = createTodo(token, listId, Map.of("title", "Erledigt", "priority", "LOW", "dueDate", LocalDate.now().minusDays(2).toString()));
        mockMvc.perform(patch("/api/lists/{listId}/todos/{todoId}/complete", listId, doneOverdueId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/lists/{listId}/todos", listId)
                        .param("due", "overdue")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(overdueId.toString())));
    }

    private void register(String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
                .andExpect(status().isCreated());
    }

    private String login(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    private UUID createList(String token, String name) throws Exception {
        String response = mockMvc.perform(post("/api/lists")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", name))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return UUID.fromString(objectMapper.readTree(response).get("id").asText());
    }

    private UUID createTodo(String token, UUID listId, Map<String, String> body) throws Exception {
        String response = mockMvc.perform(post("/api/lists/{listId}/todos", listId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        return UUID.fromString(jsonNode.get("id").asText());
    }
}
