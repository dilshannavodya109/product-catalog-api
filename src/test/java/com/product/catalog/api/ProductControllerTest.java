package com.product.catalog.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.catalog.api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ProductRepository repository;

    @BeforeEach
    void beforeEach() {
        repository.deleteAll();
    }

    @Test
    void create_returnsCreated_and_location_and_body() throws Exception {
        var req = mapper.createObjectNode();
        req.put("name", "Plan A");
        req.put("price", new BigDecimal("99.00"));
        req.put("currency", "SEK");

        MvcResult r = mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/products/")))
                .andExpect(jsonPath("$.name").value("Plan A"))
                .andExpect(jsonPath("$.currency").value("SEK"))
                .andReturn();

        JsonNode body = mapper.readTree(r.getResponse().getContentAsString());

        assert body.get("id") != null && body.get("id").asLong() > 0;
    }

    @Test
    void duplicate_name_should_conflict_case_insensitive() throws Exception {
        var req = mapper.createObjectNode();
        req.put("name", "Plan B");
        req.put("price", new BigDecimal("10"));
        req.put("currency", "SEK");

        mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        // second create with different case
        req.put("name", "plan b");

        mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void get_missing_returnsNotFound() throws Exception {
        mvc.perform(get("/api/v1/products/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void update_and_delete_flow() throws Exception {
        var create = mapper.createObjectNode();
        create.put("name", "Plan C");
        create.put("price", new BigDecimal("50"));
        create.put("currency", "SEK");

        MvcResult r = mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = mapper.readTree(r.getResponse().getContentAsString());
        long id = created.get("id").asLong();

        var update = mapper.createObjectNode();
        update.put("name", "Plan C+");
        update.put("price", new BigDecimal("60"));
        update.put("currency", "SEK");
        update.put("active", true);

        mvc.perform(put("/api/v1/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Plan C+"))
                .andExpect(jsonPath("$.price").value(60));

        mvc.perform(delete("/api/v1/products/" + id))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/products/" + id))
                .andExpect(status().isNotFound());
    }
}
