package com.covenant.tribe.controller;

import com.covenant.tribe.TribeApplication;
import com.covenant.tribe.dto.auth.TokensDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;



@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TribeApplication.class})
@Sql(value = {"/sql/users/init_users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/users/delete_users.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EventControllerLiveTest {

    public static String host = "http://localhost:8083";
    public static String apiUrl = "/api/v1/";
    public static String eventsUrl = "events";
    public static String pass ="string";
    public static String nick ="a";

    public static String mail ="test1@gmail.com";

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void whenContextLoads_thenNoException() {

    }

    @Test
    public void whenLogged_thenNoException() throws UnirestException, JsonProcessingException {
        TestAuthenticator testAuthenticator = new TestAuthenticatorImpl();
        testAuthenticator.givenAuthenticated(mail, pass);
    }

    @Test
    public void whenEventCreated_thenItIsAbtainable() throws UnirestException, JsonProcessingException {
        TestAuthenticator testAuthenticator = new TestAuthenticatorImpl();
        TokensDTO tokenForUser = testAuthenticator.getTokenForUser(mail, pass);
        RequestSpecification requestSpecification = testAuthenticator.givenAuthenticated(tokenForUser);
        RequestTemplateForCreatingEventDTO abc = newEvent("abc", tokenForUser.getUserId());
        String s = objectMapper.writeValueAsString(abc);
        Response post = requestSpecification.body(s).post(host+apiUrl+eventsUrl);
        ResponseBody body = post.getBody();
        body.print();
        int statusCode = post.getStatusCode();
        System.out.println(statusCode);
    }

    private RequestTemplateForCreatingEventDTO newEvent(String newForEvent, Long id) {
        return RequestTemplateForCreatingEventDTO.builder()
                .eventTypeId(1000L)
                .eventName(newForEvent)
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now().plus(5, ChronoUnit.HOURS))
                .showEventInSearch(true)
                .isPrivate(false)
                .sendToAllUsersByInterests(false)
                .isEighteenYearLimit(false)
                .organizerId(id)
                .build();
    }
}
