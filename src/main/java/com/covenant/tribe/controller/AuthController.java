package com.covenant.tribe.controller;

import com.covenant.tribe.dto.auth.TokensDTO;
import com.covenant.tribe.dto.user.UserForSignInUpDTO;
import com.covenant.tribe.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    AuthService authService;

    @Operation(
            tags = "Auth",
            description = "Login screen. Register or login user who came from a social network access and refresh token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = TokensDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/social-login")
    public ResponseEntity<?> signInUpUser(
            @RequestHeader(name = "Type")  String tokenType,
            @RequestHeader(name = "Authorization") String token,
            @RequestBody UserForSignInUpDTO userLoginDTO
    ) throws JsonProcessingException {
        log.info("[CONTROLLER] start endpoint signInUpUser with tokenType: {} and token: {} ", tokenType, token);

        TokensDTO tokensDTO = authService.getTokensToUserFromSocialNetworks(token, tokenType, userLoginDTO);

        log.info("[CONTROLLER] end endpoint signInUpUser with ResponseBody: {}", tokensDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokensDTO);

    }

}
