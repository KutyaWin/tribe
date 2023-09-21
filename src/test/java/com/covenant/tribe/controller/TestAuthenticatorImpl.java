package com.covenant.tribe.controller;

import com.covenant.tribe.dto.auth.EmailLoginDTO;
import com.covenant.tribe.dto.auth.TokensDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;


public class TestAuthenticatorImpl implements TestAuthenticator{

    public static String host = "http://localhost:8083";
    public static String apiUrl = "/api/v1/";

    public static String codeUrl = "auth/registration/email/code";

    public static String regUrl = "email/confirm";

    @Override
    public RequestSpecification givenAuthenticated(String username, String password) throws JsonProcessingException, UnirestException {
        RequestSpecification s = givenAuthenticated(getTokenForUser(username, password));
        return s;
    }

    public RequestSpecification givenAuthenticated(TokensDTO tokenForUser) throws JsonProcessingException, UnirestException {
        RequestSpecification s = RestAssured.given().headers("Authorization",
                "Bearer " + tokenForUser.getAccessToken(),
                "Content-Type",
                ContentType.JSON,
                "Accept",
                ContentType.JSON);
        return s;
    }

    @Override
    public TokensDTO getTokenForUser(String defaultUser, String defaultUserPassword) throws JsonProcessingException, UnirestException {
        Unirest.setTimeouts(0, 0);
        EmailLoginDTO emailLoginDTO = new EmailLoginDTO();
        emailLoginDTO.setEmail(defaultUser);
        emailLoginDTO.setPassword(defaultUserPassword);
        ObjectMapper objectMapper = new ObjectMapper();
        Gson gson = new Gson();
        HttpResponse<String> stringHttpResponse = Unirest.post("http://localhost:8083/api/v1/auth/login/email")
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(emailLoginDTO))
                .asString();
        String body = stringHttpResponse.getBody();
        System.out.println(body);
        TokensDTO s= objectMapper.readValue(body, TokensDTO.class);
        return s;
    }


}
