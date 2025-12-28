package com.product.catalog.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.catalog.api.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    CategoryRepository repository;

    @BeforeEach
    void beforeEach() {
        repository.deleteAll();
    }

    @Test
    void create_returnsCreated_and_location_and_body() throws Exception {
        var req = mapper.createObjectNode();
        req.put("name", "Cat A");
        req.put("description", "Category A description");

        MvcResult r = mvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/categories/")))
                .andExpect(jsonPath("$.name").value("Cat A"))
                .andExpect(jsonPath("$.description").value("Category A description"))
                .andReturn();

        JsonNode body = mapper.readTree(r.getResponse().getContentAsString());
        assert body.get("id") != null && body.get("id").asLong() > 0;
    }

    @Test
    void duplicate_name_should_conflict_case_insensitive() throws Exception {
        var req = mapper.createObjectNode();
        req.put("name", "Cat B");
        req.put("description", "desc");

        mvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        req.put("name", "cat b");

        mvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void get_missing_returnsNotFound() throws Exception {
        mvc.perform(get("/api/v1/categories/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_and_delete_flow() throws Exception {
        var create = mapper.createObjectNode();
        create.put("name", "Cat C");
        create.put("description", "desc");

        MvcResult r = mvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = mapper.readTree(r.getResponse().getContentAsString());
        long id = created.get("id").asLong();

        var update = mapper.createObjectNode();
        update.put("name", "Cat C+");
        update.put("description", "new desc");
        update.put("active", true);

        mvc.perform(put("/api/v1/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cat C+"))
                .andExpect(jsonPath("$.description").value("new desc"));

        mvc.perform(delete("/api/v1/categories/" + id))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/categories/" + id))
                .andExpect(status().isNotFound());
    }
}
