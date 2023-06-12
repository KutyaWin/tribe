package com.covenant.tribe.controller;

import com.covenant.tribe.AbstractTestcontainers;
import com.covenant.tribe.dto.auth.TokensDTO;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.repository.EventRepository;
import com.covenant.tribe.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql(value = {"/sql/init_data_for_eventcontroller.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/delete_data_for_eventcontroller.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@SpringBootTest
public class EventControllerIT extends AbstractTestcontainers {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private static Faker FAKER = new Faker();

    @BeforeEach
    void init() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void getAllEventByFilter_shouldReturnEventWithRequiredEventTypeId() throws Exception {
        //given
        var requestBuilder = get("/api/v1/events/search?eventTypeId=1000");

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content[*].eventId", hasItem(1000))
                );
    }

    @Test
    void createEvent_shouldCreateNewEventWithPassedEventNameAndReturnIsCreatedStatusWithBody() throws Exception {
        //given
        var nameForEvent = FAKER.funnyName().name();
        RequestTemplateForCreatingEventDTO requestEvent = RequestTemplateForCreatingEventDTO.builder()
                .eventTypeId(1000L)
                .eventName(nameForEvent)
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now().plus(5, ChronoUnit.HOURS))
                .showEventInSearch(true)
                .isPrivate(false)
                .sendToAllUsersByInterests(false)
                .isEighteenYearLimit(false)
                .organizerId(1000L)
                .build();
        var requestBuilder = post("/api/v1/events")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestEvent));

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.event_id").exists(),
                        jsonPath("$.event_name", equalTo(nameForEvent))
                );
    }

    private String obtainAccessToken(String email, String password) throws Exception {
        AuthRequestDto preparedRequest = new AuthRequestDto(email, password);

        var requestBuilder = post("/api/v1/auth/login/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preparedRequest));

        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        TokensDTO token = objectMapper.readValue(result.getResponse().getContentAsString(), TokensDTO.class);
        return token.getAccessToken();
    }

}