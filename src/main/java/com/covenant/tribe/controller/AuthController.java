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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Tag(name = "Auth")
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    AuthService authService;

    @Operation(
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
            @Valid @RequestBody UserForSignInUpDTO userLoginDTO
    ) throws JsonProcessingException {
        log.info("[CONTROLLER] start endpoint signInUpUser with tokenType: {} and token: {} ", tokenType, token);

        TokensDTO tokensDTO = authService.getTokensToUserFromSocialNetworks(token, tokenType, userLoginDTO);

        log.info("[CONTROLLER] end endpoint signInUpUser");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokensDTO);
    }

    @Operation(
            description = "Категория: Вход/Регистрация. Экран: Вход по номеру, ввод номера. Кнопка: Вход" +
                    " Действие: Получение кода подтверждения номера телефона с использованием WhatsApp",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            }
    )
    @PostMapping("/login/phone/whatsapp/code")
    public ResponseEntity<?> getCodeForLoginWithWhatsApp(
            @Valid @RequestBody PhoneNumberDto phoneNumberDto
    ) {
        log.info("[CONTROLLER] start endpoint getCodeForLoginWithWhatsApp with phoneNumberDto");

        authService.getCodeForLoginWithWhatsApp(phoneNumberDto);

        log.info("[CONTROLLER] end endpoint getCodeForLoginWithWhatsApp");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            description = "Категория: Вход/Регистрация. Экран: Вход по номеру, проверка номера. Input: 4 цифры" +
                    " Действие: Ввод кода подтверждения номера телефона полученного в WhatsApp",
            responses = {
                    @ApiResponse(
                            responseCode = "202",
                            content = @Content(
                                    schema = @Schema(implementation = TokensDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/login/phone/code/confirm")
    public ResponseEntity<?> confirmCodeForLoginWithWhatsApp(
            @Valid @RequestBody PhoneConfirmCodeDto phoneConfirmCodeDto
    ) {
        log.info("[CONTROLLER] start endpoint confirmCodeForLoginWithWhatsApp with phoneConfirmCodeDto: {}", phoneConfirmCodeDto);

        TokensDTO tokenDTO = authService.confirmCodeForLoginWithWhatsApp(phoneConfirmCodeDto);

        log.info("[CONTROLLER] end endpoint confirmCodeForLoginWithWhatsApp");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(tokenDTO);
    }

    @Operation(
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

        log.info("[CONTROLLER] end endpoint loginUserWithEmail with ResponseBody:");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokensDTO);
    }

    @Operation(
            description = "Reset password screen. Send new password to email",
            responses = {
                    @ApiResponse(
                            responseCode = "205"
                    )
            }
    )
    @PutMapping("/email/password/reset/code")
    public ResponseEntity<?> sendResetCodeToEmail(
            @RequestBody @Valid ResetPasswordDTO resetPasswordDTO
    ) {
        log.info("[CONTROLLER] start endpoint resetPassword with resetPasswordDTO: {}", resetPasswordDTO);
        authService.sendResetCodeToEmail(resetPasswordDTO);
        log.info("[CONTROLLER] end endpoint resetPassword");
        return new ResponseEntity<>(HttpStatus.RESET_CONTENT);
    }

    @Operation(
            description = "Screen: Сброс, смена пароля. Confirm reset code",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = TokensDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/email/password/reset/code/confirm")
    public ResponseEntity<?> confirmResetCode(
            @RequestBody @Valid EmailConfirmCodeDto confirmResetCodeDTO
    ) {
        log.info("[CONTROLLER] start endpoint confirmResetCode with confirmResetCodeDTO: {}", confirmResetCodeDTO);

        TokensDTO tokensDTO = authService.confirmResetCode(confirmResetCodeDTO);

        log.info("[CONTROLLER] end endpoint confirmResetCode");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokensDTO);
    }

    @Operation(
            description = "Screen when user changed password. Change password",
            responses = {
                    @ApiResponse(
                            responseCode = "201"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#changePasswordDTO.userId.toString().equals(authentication.getName())")
    @PutMapping("/email/password/change")
    public ResponseEntity<?> changePassword(
            @RequestBody @Valid ChangePasswordDTO changePasswordDTO
    ) {
        log.info("[CONTROLLER] start endpoint changePassword with changePasswordDTO: {}", changePasswordDTO);
        authService.changePassword(changePasswordDTO);
        log.info("[CONTROLLER] end endpoint changePassword");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
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
        log.info("[CONTROLLER] start endpoint refreshTokens)");
        TokensDTO tokensDTO = authService.refreshTokens(token);
        log.info("[CONTROLLER] end endpoint refreshTokens");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tokensDTO);
    }

    @Operation(
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

        RegistrantResponseDTO registrantResponseDto = authService.addRegistrantWithEmail(registrantRequestDTO);

        log.info("[CONTROLLER] end endpoint add registrant with ResponseBody");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(registrantResponseDto);
    }

    @Operation(
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
            @RequestBody @Valid ConfirmRegistrationDTO confirmRegistrationDTO
    ) {
        log.info("[CONTROLLER] start endpoint confirm email with confirmRegistrationDTO {} ", confirmRegistrationDTO);

        TokensDTO tokensDTO = authService.confirmEmailRegistration(confirmRegistrationDTO);

        log.info("[CONTROLLER] end endpoint add registrant with ResponseBody");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tokensDTO);
    }
}
