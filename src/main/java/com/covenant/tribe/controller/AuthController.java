package com.covenant.tribe.controller;

import com.covenant.tribe.dto.auth.*;
import com.covenant.tribe.dto.user.UserForSignInUpDTO;
import com.covenant.tribe.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
            description = "Login screen. Register or login user who came from a social network",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = TokensDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/login/social")
    public ResponseEntity<?> signInUpUser(
            @RequestHeader(name = "Type") String tokenType,
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

    @Operation(
            tags = "Auth",
            description = "Login screen. Login user with email and password",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = TokensDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/login/email")
    public ResponseEntity<?> loginUserWithEmail(
            @RequestBody EmailLoginDTO emailLoginDTO
    ) {
        log.info("[CONTROLLER] start endpoint loginUserWithEmail with emailLoginDTO: {}", emailLoginDTO);

        TokensDTO tokensDTO = authService.loginUserWithEmail(emailLoginDTO);

        log.info("[CONTROLLER] end endpoint loginUserWithEmail with ResponseBody: {}", tokensDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokensDTO);
    }


    @Operation(
            tags = "Auth",
            description = "Every screens when token needed. Return new access and refresh token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = TokensDTO.class)
                            )

                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshTokens(@RequestHeader(name = "Authorization") String token) {
        TokensDTO tokensDTO = authService.refreshTokens(token);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokensDTO);
    }

    @Operation(
            tags = "Auth",
            description = "Email registration screen. Start registration flow and send verify_code to email",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = RegistrantResponseDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/registration/email/code")
    public ResponseEntity<?> addRegistrantWithEmail(
            @RequestBody RegistrantRequestDTO registrantRequestDTO
    ) {
        log.info("[CONTROLLER] start endpoint add registrant with registrantDTO {} ", registrantRequestDTO);

        Long registrantId = authService.addRegistrantWithEmail(registrantRequestDTO);
        RegistrantResponseDTO registrantResponseDTO = new RegistrantResponseDTO(registrantId);

        log.info("[CONTROLLER] end endpoint add registrant with ResponseBody: {}", registrantResponseDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(registrantResponseDTO);
    }

    @Operation(
            tags = "Auth",
            description = "Email confirm registration screen. Confirm email registration with verify_code;",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    schema = @Schema(implementation = TokensDTO.class)
                            )

                    )
            }
    )
    @PostMapping("/registration/email/confirm")
    public ResponseEntity<?> confirmEmailRegistration(
            @RequestBody ConfirmRegistrationDTO confirmRegistrationDTO
    ) {
        log.info("[CONTROLLER] start endpoint confirm email with confirmRegistrationDTO {} ", confirmRegistrationDTO);

        TokensDTO tokensDTO = authService.confirmEmailRegistration(confirmRegistrationDTO);

        log.info("[CONTROLLER] end endpoint add registrant with ResponseBody: {}", tokensDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tokensDTO);
    }
}
