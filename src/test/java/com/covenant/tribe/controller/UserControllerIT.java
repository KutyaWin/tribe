package com.covenant.tribe.controller;

import com.covenant.tribe.AbstractTestcontainers;
import com.covenant.tribe.configuration.PathConfiguration;
import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.dto.auth.TokensDTO;
import com.covenant.tribe.dto.user.EmailChangeDto;
import com.covenant.tribe.dto.user.SubscriptionDto;
import com.covenant.tribe.dto.user.UserEmailDto;
import com.covenant.tribe.dto.user.UserProfileUpdateDto;
import com.covenant.tribe.repository.FileStorageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import org.apache.tika.Tika;
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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@Sql(value = "/sql/users/init_users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/sql/users/delete_users.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@SpringBootTest
public class UserControllerIT extends AbstractTestcontainers {

// write в env SPRING_PARENT_DIRECTORY = домашняя директория

    @Autowired
    FileStorageRepository fileStorageRepository;

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
    void findUserByUsernameForSendInvite() throws Exception {

        var result = get("/api/v1/user/username/partial/{username}", "ala")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"));

            this.mockMvc.perform(result).andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.content[*]", hasSize(2))
            );
    }

    @Test
    void findAllSubscribersByUsername() throws Exception {
            var result = get("/api/v1/user/subscriber/partial/{subscriber_username}/{user_id}"
                    , "ala", 1000)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"));

            this.mockMvc.perform(result).andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.content[*]", hasSize(1)),
                    jsonPath("$.content[*].username", hasItem("alam"))
            );
    }

    @Test
    void findAllUnSubscribers() throws Exception {
        var result = get("/api/v1/user/unsubscriber/{user_id}", 1001)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                + obtainAccessToken("test2@gmail.com", "string"));

        this.mockMvc.perform(result).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.content[*]", hasSize(1)),
                jsonPath("$.content[*].username", hasItem("alak"))
        );
    }

    @Test
    void findAllUnSubscribersByUsername() throws Exception {
        var result = get("/api/v1/user/unsubscriber/partial/{unsubscriber_username}/{user_id}"
                , "ala", 1001)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                + obtainAccessToken("test2@gmail.com", "string"));

        this.mockMvc.perform(result).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.content[*]", hasSize(1)),
                jsonPath("$.content[*].username", hasItem("alak"))
        );

    }
    @Test
    void findAllSubscribersById() throws Exception {
            var result = get("/api/v1/user/subscriber/{user_id}", 1000)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "
                    + obtainAccessToken("test1@gmail.com", "string"));

            this.mockMvc.perform(result).andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.content[*]", hasSize(1)),
                    jsonPath("$.content[*].user_id", hasItem(1001))
            );
    }

    @Test
    void subscribeToUser() throws Exception {
        SubscriptionDto subscriptionDto = new SubscriptionDto(1000L, 1001L);

        var result = post("/api/v1/user/subscribe")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscriptionDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isAccepted()
        );
    }

    @Test
    void subscribeToUser_isException() throws Exception {

        SubscriptionDto subscriptionDto = new SubscriptionDto(1001L, 1000L);

        var result = post("/api/v1/user/subscribe")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test2@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscriptionDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_JSON)
                );

    }
    @Test
    void unsubscribeFromUser() throws Exception {

        SubscriptionDto subscriptionDto = new SubscriptionDto(1001L, 1000L);

         var result = put("/api/v1/user/unsubscribe")
                 .header(HttpHeaders.AUTHORIZATION, "Bearer "
                 + obtainAccessToken("test2@gmail.com", "string"))
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(subscriptionDto));

         this.mockMvc.perform(result).andExpectAll(
                 status().isAccepted()
         );
    }

    @Test
    void unsubscribeFromUser_isException() throws Exception {

        SubscriptionDto subscriptionDto = new SubscriptionDto(1000L, 1001L);

        var result = put("/api/v1/user/unsubscribe")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscriptionDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    void isEmailExistCheck() throws Exception {
        var result = get("/api/v1/user/email/check/{email}", "test1@gmail.com");

        this.mockMvc.perform(result).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$", equalTo(true))

        );
    }

    @Test
    void sendEmailConfirmationCode() throws Exception {
        UserEmailDto userEmailDto = UserEmailDto.builder()
                .userId("1000")
                .oldEmail("test1@gmail.com")
                .newEmail("test@gmail.com")
                .build();

        var result = post("/api/v1/user/email/change/code")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userEmailDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isOk()
        );
    }

    @Test
    void sendEmailConfirmationCode_isUserNotFoundException() throws Exception {
        UserEmailDto userEmailDto = UserEmailDto.builder()
                .userId("1000")
                .oldEmail("like@gmail.com")
                .newEmail("test@gmail.com")
                .build();

        var result = post("/api/v1/user/email/change/code")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userEmailDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    void sendEmailConfirmationCode_isUserAlreadyExistException() throws Exception {
        UserEmailDto userEmailDto = UserEmailDto.builder()
                .userId("1000")
                .oldEmail("test1@gmail.com")
                .newEmail("test2@gmail.com")
                .build();

        var result = post("/api/v1/user/email/change/code")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userEmailDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    void sendEmailConfirmationCode_isUnexpectedDataException_alienAddress() throws Exception {
        UserEmailDto userEmailDto = UserEmailDto.builder()
                .userId("1001")
                .oldEmail("test1@gmail.com")
                .newEmail("test@gmail.com")
                .build();

        var result = post("/api/v1/user/email/change/code")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test2@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userEmailDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    void sendEmailConfirmationCode_isUnexpectedDataExceptionWithSameEmail() throws Exception {
        UserEmailDto userEmailDto = UserEmailDto.builder()
                .userId("1000")
                .oldEmail("test1@gmail.com")
                .newEmail("test1@gmail.com")
                .build();

        var result = post("/api/v1/user/email/change/code")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userEmailDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_JSON)
        );
    }


    @Test
    void confirmEmailChange() throws Exception {
        EmailChangeDto emailChangeDto = EmailChangeDto.builder()
                .verificationCode(1111)
                .oldEmail("test1@gmail.com")
                .newEmail("test11@gmail.com")
                .build();

        var result = patch("/api/v1/user/email/change/confirm")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailChangeDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isOk()
        );
    }

    @Test
    void confirmEmailChange_isVerificationCodeNotFoundException() throws Exception {
        EmailChangeDto emailChangeDto = EmailChangeDto.builder()
                .verificationCode(1111)
                .oldEmail("test1@gmail.com")
                .newEmail("test@gmail.com")
                .build();

        var result = patch("/api/v1/user/email/change/confirm")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailChangeDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    void confirmEmailChange_isWrongCodeException() throws Exception {
        EmailChangeDto emailChangeDto = EmailChangeDto.builder()
                .verificationCode(1112)
                .oldEmail("test1@gmail.com")
                .newEmail("test11@gmail.com")
                .build();

        var result = patch("/api/v1/user/email/change/confirm")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailChangeDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    void confirmEmailChange_isExpiredCodeException() throws Exception {
        EmailChangeDto emailChangeDto = EmailChangeDto.builder()
                .verificationCode(2222)
                .oldEmail("test2@gmail.com")
                .newEmail("test22@gmail.com")
                .build();

        var result = patch("/api/v1/user/email/change/confirm")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test2@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailChangeDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    void confirmEmailChange_isUserNotFoundException() throws Exception {
        EmailChangeDto emailChangeDto = EmailChangeDto.builder()
                .verificationCode(1111)
                .oldEmail("test@gmail.com")
                .newEmail("test11@gmail.com")
                .build();

        var result = patch("/api/v1/user/email/change/confirm")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailChangeDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    void isUsernameExistCheck() throws Exception {
        var result = get("/api/v1/user/username/check/{username}", "alak");

        this.mockMvc.perform(result).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$", equalTo(true))
        );
    }

    @Test
    void getUser() throws Exception {
        var result = get("/api/v1/user/{userId}", "1000")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"));

        this.mockMvc.perform(result).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.username", equalTo("alak"))
        );
    }
    @Test
    void uploadAvatarToTempFolder() throws Exception {
        String image = FAKER.avatar().image();
        Tika tika = new Tika();
        String contentType = tika.detect(image);
        ImageDto imageDto = new ImageDto(contentType, image.getBytes());

         var result = post("/api/v1/user/avatar/{userId}", "1000")
                 .header(HttpHeaders.AUTHORIZATION, "Bearer "
                 + obtainAccessToken("test1@gmail.com", "string"))
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(imageDto));

         this.mockMvc.perform(result).andExpectAll(
                 status().isCreated(),
                 content().contentType(MediaType.APPLICATION_JSON),
                 jsonPath("$.unique_file_name", endsWith("jpeg"))
         );
    }

    @Test
    void getUserAvatar() throws Exception {

        StringBuilder pathToNewFolder = new StringBuilder(pathConfiguration.getHome())
                .append(pathConfiguration.getMain()).append(File.separator)
                .append(pathConfiguration.getImage()).append(File.separator)
                .append(pathConfiguration.getUser()).append(File.separator)
                .append(pathConfiguration.getAvatar()).append(File.separator)
                .append("2023-06-21");

        Files.createDirectories(Path.of(pathToNewFolder.toString()));
        byte[] image = FAKER.avatar().image().getBytes();

        String filePath = pathToNewFolder
                .append(File.separator)
                .append("c1b00948-d59a-4fef-8c99-d6f59e611545.jpg").toString();

        Files.write(Path.of(filePath), image);

        var result = get("/api/v1/user/avatar/{added_date}/{file_name}", "2023-06-21",
                "c1b00948-d59a-4fef-8c99-d6f59e611545.jpg");

        this.mockMvc.perform(result).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.TEXT_PLAIN),
                jsonPath("$", endsWith("jpg"))
        );

        Files.deleteIfExists(Path.of(filePath));
    }

    @Test
    void updateUserProfile() throws Exception {


        String image = FAKER.avatar().image();
        Tika tika = new Tika();
        String contentType = tika.detect(image);


        String userAvatar = fileStorageRepository.saveFileToTmpDir(contentType, image.getBytes());

        UserProfileUpdateDto userProfileUpdateDto = UserProfileUpdateDto.builder()
                .userId(1000L)
                .userAvatar(userAvatar)
                .avatarsFilenamesForDeleting(List.of(userAvatar))
                .username("nickname")
                .firstName("name")
                .lastName("lastName")
                .birthday(LocalDate.now().minus(5, ChronoUnit.DAYS))
                .interestingEventType(List.of(1000L))
                .professionIds(List.of(1001L, 1000L))
                .newProfessions(List.of("testProfessions1", "testProfessions2"))
                .isGeolocationAvailable(false)
                .build();

        var result = patch("/api/v1/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userProfileUpdateDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isAccepted()
        );


    }

    @Test
    void updateUserProfile_isFilesNotHandleException_fileNameForAdding() throws Exception {

        UserProfileUpdateDto userProfileUpdateDto = UserProfileUpdateDto.builder()
                .userId(1000L)
                .userAvatar("image.jpg")
                .username("nickname")
                .firstName("name")
                .lastName("lastName")
                .birthday(LocalDate.now().minus(5, ChronoUnit.DAYS))
                .interestingEventType(List.of(1000L))
                .professionIds(List.of(1001L))
                .newProfessions(List.of("testProfessions1", "testProfessions2"))
                .isGeolocationAvailable(false)
                .build();

        var result = patch("/api/v1/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userProfileUpdateDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isInternalServerError(),
                content().contentType(MediaType.APPLICATION_JSON)
        );

    }

    @Test
    void updateUserProfile_isUserAlreadyExistException() throws Exception {


        String image = FAKER.avatar().image();
        Tika tika = new Tika();
        String contentType = tika.detect(image);


        String userAvatar = fileStorageRepository.saveFileToTmpDir(contentType, image.getBytes());

        UserProfileUpdateDto userProfileUpdateDto = UserProfileUpdateDto.builder()
                .userId(1000L)
                .userAvatar(userAvatar)
                .avatarsFilenamesForDeleting(List.of(userAvatar))
                .username("alam")
                .firstName("name")
                .lastName("lastName")
                .birthday(LocalDate.now().minus(5, ChronoUnit.DAYS))
                .interestingEventType(List.of(1000L))
                .professionIds(List.of(1001L, 1000L))
                .newProfessions(List.of("testProfessions1", "testProfessions2"))
                .isGeolocationAvailable(false)
                .build();

        var result = patch("/api/v1/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userProfileUpdateDto));

        this.mockMvc.perform(result).andExpectAll(
                status().isConflict()
        );


    }


        @Test
        void getUserProfile() throws Exception {
            var result = get("/api/v1/user/profile/{userId}", "1000")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "
                            + obtainAccessToken("test1@gmail.com", "string"));

            this.mockMvc.perform(result).andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.username", equalTo("alak")),
                    jsonPath("$.age", equalTo(0))
            );
        }

        @Test
        void deleteUser() throws Exception {

            var result = delete("/api/v1/user/delete/{userId}", "1000")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "
                    + obtainAccessToken("test1@gmail.com", "string"));

            this.mockMvc.perform(result).andExpectAll(
                status().isOk()
            );
        }

    @Test
    void deleteUser_isUserNotFoundException() throws Exception {
        var result = delete("/api/v1/user/delete/{userId}", "1005")
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"));

        this.mockMvc.perform(result).andExpectAll(
                status().isForbidden()
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
