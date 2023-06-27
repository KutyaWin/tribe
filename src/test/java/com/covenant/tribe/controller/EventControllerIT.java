package com.covenant.tribe.controller;

import com.covenant.tribe.AbstractTestcontainers;
import com.covenant.tribe.configuration.PathConfiguration;
import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.dto.auth.TokensDTO;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.dto.event.UpdateEventDto;
import com.covenant.tribe.dto.user.UserFavoriteEventDTO;
import com.covenant.tribe.repository.EventRepository;
import com.covenant.tribe.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import org.apache.tika.Tika;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Autowired
    private PathConfiguration pathConfiguration;

    @BeforeEach
    void init() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void getAllEventByFilter_shouldReturnEventWithRequiredEventTypeId() throws Exception {
        //given
        var requestBuilder = get("/api/v1/events/search?eventTypeId=1001");

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.content[*].eventId", hasItem(1001))
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
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + obtainAccessToken("test1@gmail.com", "string"))
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
    @Test
    void deleteEvent() throws Exception {
        var requestBuilder = delete("/api/v1/events/delete/{organizer_Id}/{event_Id}", 1000, 1000)
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + obtainAccessToken("test1@gmail.com", "string"));

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isAccepted());
    }

    @Test
    void getEventById() throws Exception {
        var requestBuilder = get("/api/v1/events/{event_id}", 1001);

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.event_name", equalTo("eventname2"))
        );
    }


    @Test
    void getEventWithVerificationPendingStatus() throws Exception {
        var requestBuilder = get("/api/v1/events/verification");

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.[*]", hasSize(1)),
                jsonPath("$.[*].event_address.city", hasItem("city1"))
        );
    }

    @Test
    void updateEventStatusToPublished() throws Exception {
        var requestBuilder = patch("/api/v1/events/verification/confirm/{event_id}", 1000)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"));

        this.mockMvc.perform(requestBuilder).andExpect(
                status().isOk()
        );
    }

    @Test
    void updateEventStatusToSendToRework() throws Exception {
        var requestBuilder = patch("/api/v1/events/verification/rework/{event_id}", 1000)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"));

        this.mockMvc.perform(requestBuilder).andExpect(
                status().isOk()
        );
    }

    @Test
    void findEventsByOrganizerId() throws Exception{
        var requestBuilder = get("/api/v1/events/organisation/{organizer_id}", 1001)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test2@gmail.com", "string"));

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.[*].city", hasItem("city2"))
        );
    }

    @Test
    void findEventsByUserIdWhichUserIsInvited() throws Exception{
        var requestBuilder = get("/api/v1/events/invitation/{user_id}", 1001)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test2@gmail.com", "string"));

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.[*]", hasSize(1))
        );
    }

    @Test
    void confirmInvitationToEvent() throws Exception{
        var requestBuilder = patch("/api/v1/events/invitation/confirm/{event_id}/{user_id}",
                1001, 1001)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test2@gmail.com", "string"));

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isOk()
        );
    }

    @Test
    void declineInvitationToEvent() throws Exception{
        var requestBuilder = patch("/api/v1/events/invitation/decline/{event_id}/{user_id}",
                1001, 1001)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test2@gmail.com", "string"));

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isOk()
        );
    }

    @Test
    void addEventAvatarToTempDirectory() throws Exception{

        String imagef = FAKER.avatar().image();
        Tika tika = new Tika();
        String contentType = tika.detect(imagef);
        byte[] image = imagef.getBytes();

        ImageDto imageDto = new ImageDto(contentType, image);

        var requestBuilder = post("/api/v1/events/avatars")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(imageDto));

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    void getEventAvatar() throws Exception{

        StringBuilder pathToNewFolder = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append("/")
                .append(pathConfiguration.getImage()).append("/")
                .append(pathConfiguration.getEvent()).append("/")
                .append(pathConfiguration.getAvatar()).append("/")
                .append("2023-06-21").append("/");

        Files.createDirectories(Path.of(pathToNewFolder.toString()));

        byte[] image = FAKER.avatar().image().getBytes();

        String filePath = pathToNewFolder
                .append("/")
                .append("c1b00948-d59a-4fef-8c99-d6f59e611545.jpg").toString();

        Files.write(Path.of(filePath), image);

        var requestBuilder = get("/api/v1/events/avatars/{added_date}/{avatar_file_name}",
                "2023-06-21", "c1b00948-d59a-4fef-8c99-d6f59e611545.jpg");

            this.mockMvc.perform(requestBuilder).andExpect(
                    status().isOk()
            );

          Files.deleteIfExists(Path.of(filePath));
    }


    @Test
    void saveEventToFavorites() throws Exception{
        UserFavoriteEventDTO userFavoriteEventDTO = new UserFavoriteEventDTO("1000", 1000L);
        var requestBuilder = post("/api/v1/events/favorite")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userFavoriteEventDTO));


        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isAccepted()
        );

    }

    @Test
    void deleteEventFromFavorites() throws Exception{
        var requestBuilder = delete("/api/v1/events/favorite/{user_id}/{event_id}", 1001, 1001)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test2@gmail.com", "string"));

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isOk()
        );
    }

    @Test
    void getAllFavoritesByUserId() throws Exception{
        var requestBuilder = get("/api/v1/events/favorite/{user_id}", 1001)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test2@gmail.com", "string"));

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.[*]", hasSize(1))
        );
    }

    @Test
    void updateEvent() throws Exception{
        String eventLongitude = FAKER.address().longitude().replace(",",".");
        String eventLatitude = FAKER.address().latitude().replace(",",".");
        String city = FAKER.address().city();
        String region = FAKER.elderScrolls().region();
        String street = FAKER.address().streetName();
        String district  = FAKER.address().state();
        String building = FAKER.address().buildingNumber();
        String houseNumber = Integer.toString(FAKER.number().numberBetween(1, 10));
        String floor = Integer.toString(FAKER.number().numberBetween(1, 10));
        String name = FAKER.funnyName().name();
        String description = FAKER.lorem().sentence();

        EventAddressDTO eventAddressDTO = EventAddressDTO.builder()
                .eventLongitude(Double.parseDouble(eventLongitude))
                .eventLatitude(Double.parseDouble(eventLatitude))
                .city(city)
                .region(region)
                .street(street)
                .district(district)
                .building(building)
                .houseNumber(houseNumber)
                .floor(floor)
                .build();

        UpdateEventDto updateEventDto = UpdateEventDto.builder()
                .eventId(1001L)
                .organizerId("1001")
                .eventTypeId(1001L)
                .avatarsForDeleting(new ArrayList<>())
                .avatarsForAdding(new ArrayList<>())
                .name(name)
                .addressDTO(eventAddressDTO)
                .startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now().plus(FAKER.number().randomDigitNotZero(), ChronoUnit.HOURS))
                .tagIdsForDeleting(new ArrayList<>())
                .tagIdsForAdding(new ArrayList<>())
                .newTags(new HashSet<>())
                .description(description)
                .participantIdsForAdding(new ArrayList<>())
                .participantIdsForDeleting(new ArrayList<>())
                .isPrivate(FAKER.random().nextBoolean())
                .isShowInSearch(FAKER.random().nextBoolean())
                .isSendByInterests(FAKER.random().nextBoolean())
                .isEighteenYearLimit(FAKER.random().nextBoolean())
                .build();

        var requestBuilder = patch("/api/v1/events/update")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test2@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateEventDto));

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON)
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