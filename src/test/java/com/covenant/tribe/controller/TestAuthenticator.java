package com.covenant.tribe.controller;

import com.covenant.tribe.dto.auth.TokensDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.restassured.specification.RequestSpecification;

public interface TestAuthenticator {
    RequestSpecification givenAuthenticated(TokensDTO tokensDTO) throws JsonProcessingException, UnirestException;
    RequestSpecification givenAuthenticated(String username, String password) throws JsonProcessingException, UnirestException;
    TokensDTO getTokenForUser(String defaultUser, String defaultUserPassword) throws JsonProcessingException, UnirestException;

}
