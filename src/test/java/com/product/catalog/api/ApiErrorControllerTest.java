package com.product.catalog.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiErrorControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;


    @Test
    void get_missing_returnsApiError_notFound_shape() throws Exception {
        mvc.perform(get("/api/v1/products/999999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/products/999999"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields", hasSize(0)));
    }

    @Test
    void duplicate_create_returnsApiError_conflict_shape() throws Exception {
        var req = mapper.createObjectNode();
        req.put("name", "ConflictPlan");
        req.put("price", new BigDecimal("10"));
        req.put("currency", "SEK");

        // first create should succeed
        mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        req.put("name", "conflictplan");

        mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Product already exists"))
                .andExpect(jsonPath("$.path", containsString("/api/v1/products")))
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields", hasSize(0)));
    }

    @Test
    void invalid_request_returnsApiError_badRequest_with_fields() throws Exception {
        var req = mapper.createObjectNode();
        req.put("name", "");
        req.put("price", -5);
        req.put("currency", "XX");

        mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields", not(empty())))
                .andExpect(jsonPath("$.fields[*].field", hasItems("name", "price", "currency")));
    }
}
