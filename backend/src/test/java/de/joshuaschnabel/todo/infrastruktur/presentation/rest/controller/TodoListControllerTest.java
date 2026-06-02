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

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TodoListControllerTest {

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
    void getListsWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/lists"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postCreatesList() throws Exception {
        register("alice@example.com", "password123");
        String token = login("alice@example.com", "password123");

        mockMvc.perform(post("/api/lists")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Privat"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Privat"));
    }

    @Test
    void postWithoutNameReturnsBadRequest() throws Exception {
        register("bob@example.com", "password123");
        String token = login("bob@example.com", "password123");

        mockMvc.perform(post("/api/lists")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getListsShowsOnlyOwnLists() throws Exception {
        register("carol@example.com", "password123");
        String token1 = login("carol@example.com", "password123");
        createList(token1, "Arbeit");

        register("dave@example.com", "password123");
        String token2 = login("dave@example.com", "password123");
        createList(token2, "Fremd");

        mockMvc.perform(get("/api/lists").header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", hasItem("Meine Aufgaben")))
                .andExpect(jsonPath("$[*].name", hasItem("Arbeit")))
                .andExpect(jsonPath("$[*].name", not(hasItem("Fremd"))));
    }

    @Test
    void putRenamesList() throws Exception {
        register("erin@example.com", "password123");
        String token = login("erin@example.com", "password123");
        UUID listId = createList(token, "Alt");

        mockMvc.perform(put("/api/lists/{listId}", listId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Neu"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Neu")));
    }

    @Test
    void deleteRemovesList() throws Exception {
        register("frank@example.com", "password123");
        String token = login("frank@example.com", "password123");
        UUID listId = createList(token, "Zum Löschen");

        mockMvc.perform(delete("/api/lists/{listId}", listId).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteForeignListReturnsForbidden() throws Exception {
        register("gina@example.com", "password123");
        String ownerToken = login("gina@example.com", "password123");
        UUID foreignListId = createList(ownerToken, "Privat");

        register("hans@example.com", "password123");
        String attackerToken = login("hans@example.com", "password123");

        mockMvc.perform(delete("/api/lists/{listId}", foreignListId).header("Authorization", "Bearer " + attackerToken))
                .andExpect(status().isForbidden());
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
        JsonNode jsonNode = objectMapper.readTree(response);
        return UUID.fromString(jsonNode.get("id").asText());
    }
}
