package com.product.catalog.api;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RequestIdLoggingTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    TestListAppender appender;
    Logger logger;

    static class TestListAppender extends AppenderBase<ILoggingEvent> {
        final List<ILoggingEvent> events = new ArrayList<>();
        @Override
        protected void append(ILoggingEvent eventObject) {
            events.add(eventObject);
        }
    }

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger("com.product.catalog.api");
        appender = new TestListAppender();
        appender.setContext(logger.getLoggerContext());
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
        appender.stop();
    }

    @Test
    void requestId_is_returned_and_present_in_logs() throws Exception {
        String reqId = "test-request-id-123";

        var req = mapper.createObjectNode();
        req.put("name", "Plan D");
        req.put("price", 10);
        req.put("currency", "SEK");

        mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req))
                        .header("X-Request-Id", reqId))
                .andExpect(status().isCreated())
                .andExpect(header().string("X-Request-Id", reqId));


        boolean found = appender.events.stream()
                .anyMatch(e -> reqId.equals(e.getMDCPropertyMap().get("requestId")));

        assertThat(found).isTrue();
    }
}
