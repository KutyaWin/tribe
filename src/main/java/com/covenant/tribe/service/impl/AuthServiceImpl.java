package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.dto.VkValidationErrorDTO;
import com.covenant.tribe.client.dto.VkValidationResponseDTO;
import com.covenant.tribe.client.vk.VkClient;
import com.covenant.tribe.client.vk.VkValidationParams;
import com.covenant.tribe.client.whatsapp.WhatsAppClient;
import com.covenant.tribe.client.whatsapp.dto.*;
import com.covenant.tribe.domain.auth.PhoneVerificationCode;
import com.covenant.tribe.domain.auth.EmailVerificationCode;
import com.covenant.tribe.domain.auth.SocialIdType;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.*;
import com.covenant.tribe.dto.auth.*;
import com.covenant.tribe.dto.user.UserForSignInUpDTO;
import com.covenant.tribe.exeption.auth.*;
import com.covenant.tribe.exeption.user.UserAlreadyExistException;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.repository.*;
import com.covenant.tribe.security.JwtProvider;
import com.covenant.tribe.service.AuthService;
import com.covenant.tribe.service.MailService;
import com.covenant.tribe.service.VerificationCodeService;
import com.covenant.tribe.util.mapper.RegistrantMapper;
import com.covenant.tribe.util.mapper.UserMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    private final String EMAIL_SUBJECT = "Регистрация в приложении Tribe";
    private final Integer CODE_EXPIRATION_TIME_IN_MIN = 5;

    RegistrantRepository registrantRepository;
    PhoneVerificationRepository phoneVerificationRepository;
    VerificationCodeService verificationCodeService;
    PasswordEncoder encoder;
    MailService mailService;
    VkClient vkClient;
    WhatsAppClient whatsAppClient;
    GoogleIdTokenVerifier googleIdTokenVerifier;
    JwtProvider jwtProvider;
    UserRepository userRepository;
    UserMapper userMapper;
    UnknownUserRepository unknownUserRepository;
    EmailVerificationRepository emailVerificationRepository;
    EventTypeRepository eventTypeRepository;
    RegistrantMapper registrantMapper;


    @Value(value = "${spring.cloud.openfeign.client.config.vk-client.client-id}")
    String clientId;

    @Value(value = "${spring.cloud.openfeign.client.config.vk-client.client-secret}")
    String clientSecret;

    @Value(value = "${spring.cloud.openfeign.client.config.vk-client.api-version}")
    String apiVersion;

    @Value("${spring.cloud.openfeign.client.config.whatsapp-client.api-version}")
    String whatsAppApiVersion;

    @Value("${spring.cloud.openfeign.client.config.whatsapp-client.access-token}")
    String whatsAppAccessToken;

    @Value("${spring.cloud.openfeign.client.config.whatsapp-client.phone-number-id}")
    String whatsAppPhoneNumberId;

    @Value("${verification.code.email.min}")
    int minCodeValue;

    @Value("${verification.code.email.max}")
    int maxCodeValue;

    @Autowired
    public AuthServiceImpl(WhatsAppClient whatsAppClient, VerificationCodeService verificationCodeService, RegistrantRepository registrantRepository, PhoneVerificationRepository phoneVerificationRepository, PasswordEncoder encoder, EmailVerificationRepository emailVerificationRepository, MailService mailService, VkClient vkClient, GoogleIdTokenVerifier googleIdTokenVerifier, JwtProvider jwtProvider, UserRepository userRepository, UserMapper userMapper, UnknownUserRepository unknownUserRepository, EventTypeRepository eventTypeRepository, RegistrantMapper registrantMapper) {
        this.registrantRepository = registrantRepository;
        this.verificationCodeService = verificationCodeService;
        this.whatsAppClient = whatsAppClient;
        this.phoneVerificationRepository = phoneVerificationRepository;
        this.encoder = encoder;
        this.mailService = mailService;
        this.vkClient = vkClient;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.unknownUserRepository = unknownUserRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.registrantMapper = registrantMapper;
        this.emailVerificationRepository = emailVerificationRepository;
    }

    private final String GOOGLE_TOKEN_TYPE = "google";
    private final String VK_TOKEN_TYPE = "vk";

    @Transactional
    @Override
    public TokensDTO getTokensToUserFromSocialNetworks(
            String token, String tokenType, UserForSignInUpDTO userForSignInUpDTO
    ) throws JsonProcessingException {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());
        String firebaseId = userForSignInUpDTO.getFirebaseId();

        if (tokenType.equals(GOOGLE_TOKEN_TYPE)) return getTokensForGoogleUser(token, userForSignInUpDTO);
        if (tokenType.equals(VK_TOKEN_TYPE)) return getTokenForVkUser(token, userForSignInUpDTO);

        log.info("[TRANSACTION] Close transaction in class: " + this.getClass().getName());

        throw new UnexpectedTokenTypeException(tokenType);
    }

    @Override
    public TokensDTO refreshTokens(String token) {
        try {
            Long userId = Long.parseLong(jwtProvider.getRefreshTokenClaims(token).getSubject());
            UserRole userRole = userRepository
                    .findById(userId)
                    .orElseThrow(() -> {
                        String message = String.format(
                                "User with id %s, not found", userId
                        );
                        log.error(message);
                        return new UserNotFoundException(message);
                    })
                    .getUserRole();
            TokensDTO tokensDTO = new TokensDTO();
            tokensDTO.setUserId(userId);
            tokensDTO.setAccessToken(jwtProvider.generateAccessToken(userId, userRole));
            tokensDTO.setRefreshToken(jwtProvider.generateRefreshToken(userId));
            return tokensDTO;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JwtDecoderException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public RegistrantResponseDTO addRegistrantWithEmail(RegistrantRequestDTO registrantRequestDTO) {

        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        int verificationCode = verificationCodeService.getVerificationCode(minCodeValue, maxCodeValue);
        Registrant registrant = registrantRepository.findByEmail(registrantRequestDTO.getEmail());
        String emailMessage = String.format("Код подтверждения: %s", verificationCode);
        if (registrant == null) {
            registrant = registrantMapper.mapToRegistrant(registrantRequestDTO, verificationCode);
            Registrant newRegistrant = registrantRepository.save(registrant);
            mailService.sendEmail(EMAIL_SUBJECT, emailMessage, registrantRequestDTO.getEmail());
            return new RegistrantResponseDTO(newRegistrant.getId(), verificationCode);
        } else {
            if (registrant.getStatus() == RegistrantStatus.CONFIRMED ||
                    userRepository.findUserByUserEmail(registrantRequestDTO.getEmail()).isPresent()
            ) {
                String message = String.format("User with email: %s already exists", registrantRequestDTO.getEmail());
                throw new UserAlreadyExistException(message);
            }
            registrant.setPassword(encoder.encode(registrantRequestDTO.getPassword()));
            registrant.setUsername(registrantRequestDTO.getUsername());
            mailService.sendEmail(EMAIL_SUBJECT, emailMessage, registrantRequestDTO.getEmail());
            registrant.setVerificationCode(verificationCode);
            registrant.setStatus(RegistrantStatus.AWAITED);
            registrant.setCreatedAt(OffsetDateTime.now());
            registrantRepository.save(registrant);

            log.info("[TRANSACTION] End transaction in class: " + this.getClass().getName());

            return new RegistrantResponseDTO(registrant.getId(), verificationCode);
        }
    }

    @Transactional
    @Override
    public TokensDTO confirmEmailRegistration(ConfirmRegistrationDTO confirmRegistrationDTO) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());
        String firebaseId = confirmRegistrationDTO.getFirebaseId();

        Registrant registrant = registrantRepository
                .findById(confirmRegistrationDTO.getRegistrantId())
                .orElseThrow(() -> new UserNotFoundException(
                                String.format(
                                        "Registrant with id: %s don't exist'", confirmRegistrationDTO.getRegistrantId()
                                )
                        )
                );
        OffsetDateTime codeCreated = registrant.getCreatedAt();
        if (codeCreated.plus(CODE_EXPIRATION_TIME_IN_MIN, ChronoUnit.MINUTES).isBefore(OffsetDateTime.now())) {
            registrant.setStatus(RegistrantStatus.EXPIRED);
            throw new ExpiredCodeException(confirmRegistrationDTO.getRegistrantId().toString());
        }
        if (registrant.getVerificationCode().intValue() != confirmRegistrationDTO.getVerificationCode().intValue()) {
            throw new WrongCodeException(confirmRegistrationDTO.getVerificationCode().toString());
        }
        UnknownUser unknownUser = unknownUserRepository
                .findUnknownUserByFirebaseId(confirmRegistrationDTO.getFirebaseId());
        Set<EventType> userInterests;
        if (unknownUser != null) {
            userInterests = new HashSet<>(unknownUser.getUserInterests());
        } else {
            userInterests = new HashSet<>(eventTypeRepository.findAll());
        }
        User newUser = userMapper.buildUserFromConfirmRegistrationDTORegistrantAndUserInterests(
                confirmRegistrationDTO, userInterests, registrant
        );
        User savedUser = saveUser(newUser);
        registrant.setStatus(RegistrantStatus.CONFIRMED);

        try {
            TokensDTO tokensDTO = getTokenDTO(savedUser.getId(), savedUser.getUserRole());
            log.info("[TRANSACTION] End transaction in class: " + this.getClass().getName());
            return tokensDTO;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new MakeTokenException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public TokensDTO loginUserWithEmail(@Valid EmailLoginDTO emailLoginDTO) {
        User user = userRepository
                .findUserByUserEmail(emailLoginDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with email: %s, does not exist", emailLoginDTO.getEmail())
                ));
        if (!encoder.matches(emailLoginDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Credentials is wrong");
        }
        try {
            return getTokenDTO(user.getId(), user.getUserRole());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new MakeTokenException(e.getMessage());
        }
    }

    @Override
    public void sendResetCodeToEmail(ResetPasswordDTO resetPasswordDTO) {
        User user = userRepository
                .findUserByUserEmail(resetPasswordDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with email: %s, does not exist", resetPasswordDTO.getEmail())
                ));
        if (!user.hasEmailAuthentication()) {
            String errMessage = "You cannot reset your password, because you don't have email authentication";
            log.error(errMessage);
            throw new BadCredentialsException(errMessage);
        }
        int resetConfirmationCode = verificationCodeService.getVerificationCode(minCodeValue, maxCodeValue);
        String title = "Код для подтверждения сброса пароля";
        String message = String
                .format(
                        "Ваш код для подтверждения сброса пароля: %s",
                        resetConfirmationCode
                );
        EmailVerificationCode emailVerificationCode = emailVerificationRepository.
                findByEmailAndIsEnable(resetPasswordDTO.getEmail(), true);
        if (emailVerificationCode == null) {
            emailVerificationCode = EmailVerificationCode.builder()
                    .resetCode(resetConfirmationCode)
                    .email(resetPasswordDTO.getEmail())
                    .requestTime(Instant.now())
                    .isEnable(true)
                    .build();
        } else {
            emailVerificationCode.setResetCode(resetConfirmationCode);
            emailVerificationCode.setRequestTime(Instant.now());
        }
        emailVerificationRepository.save(emailVerificationCode);
        mailService.sendEmail(title, message, resetPasswordDTO.getEmail());
        user.setPassword(encoder.encode(String.valueOf(resetConfirmationCode)));
        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        User user = userRepository
                .findUserByIdAndStatus(changePasswordDTO.getUserId(), UserStatus.ENABLED)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with id: %s, does not exist", changePasswordDTO.getUserId())
                ));
        if (!encoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Credentials is wrong");
        }
        user.setPassword(encoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public TokensDTO confirmResetCode(EmailConfirmCodeDto confirmResetCodeDTO) {
        EmailVerificationCode emailVerificationCode = emailVerificationRepository
                .findByEmailAndIsEnable(confirmResetCodeDTO.getEmail(), true);
        if (emailVerificationCode == null) {
            log.error("Reset code with email: " + confirmResetCodeDTO.getEmail() + " does not exist");
            throw new UserNotFoundException(
                    String.format("Reset code with email: %s, does not exist", confirmResetCodeDTO.getEmail())
            );
        }
        if (emailVerificationCode.getResetCode() != confirmResetCodeDTO.getVerificationCode()) {
            emailVerificationCode.setEnable(false);
            String message = String.format("Reset code for email: %s, does not match", confirmResetCodeDTO.getEmail());
            log.error(message);
            throw new WrongCodeException(message);
        }
        if (emailVerificationCode.getRequestTime().plus(CODE_EXPIRATION_TIME_IN_MIN, ChronoUnit.MINUTES).isBefore(Instant.now())) {
            emailVerificationCode.setEnable(false);
            String message = String.format("Reset code for email: %s, expired", confirmResetCodeDTO.getEmail());
            log.error(message);
            throw new ExpiredCodeException(message);
        }
        User user = userRepository
                .findUserByUserEmail(confirmResetCodeDTO.getEmail())
                .orElseThrow(() -> {
                    log.error("User with email: " + confirmResetCodeDTO.getEmail() + " does not exist");
                    return new UserNotFoundException(
                            String.format("User with email: %s, does not exist", confirmResetCodeDTO.getEmail())
                    );
                });
        try {
            TokensDTO tokens = getTokenDTO(user.getId(), user.getUserRole());
            emailVerificationCode.setEnable(false);
            emailVerificationRepository.save(emailVerificationCode);
            return tokens;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new MakeTokenException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void getCodeForLoginWithWhatsApp(PhoneNumberDto phoneNumberDto) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        User user = userRepository.findUserByPhoneNumber(phoneNumberDto.getPhoneNumber());
        int verificationCode = verificationCodeService.getVerificationCode(
                minCodeValue, maxCodeValue
        );

        WhatsAppVerificationMsgDto message = getWhatsAppVerificationMsg(
                verificationCode, phoneNumberDto.getPhoneNumber()
        );
        sendMessage(phoneNumberDto, message);
        if (user != null) {
            PhoneVerificationCode phoneVerificationCode = phoneVerificationRepository
                    .findByPhoneNumberAndIsEnableIsTrue(phoneNumberDto.getPhoneNumber());
            if (phoneVerificationCode != null) {
                phoneVerificationCode.setVerificationCode(verificationCode);
                phoneVerificationCode.setRequestTime(OffsetDateTime.now());

            } else {
                phoneVerificationCode = PhoneVerificationCode
                        .builder()
                        .verificationCode(verificationCode)
                        .phoneNumber(phoneNumberDto.getPhoneNumber())
                        .build();
            }
            phoneVerificationRepository.save(phoneVerificationCode);

            if (!user.hasWhatsappAuthentication()) {
                user.hasWhatsappAuthentication(true);
                userRepository.save(user);
            }

            log.info("[TRANSACTION] Close transaction in class: " + this.getClass().getName());
            return;
        }
        Registrant registrant = registrantRepository.findByPhoneNumberAndStatus(
                phoneNumberDto.getPhoneNumber(), RegistrantStatus.AWAITED
        );
        if (registrant != null) {
            registrant.setVerificationCode(verificationCode);
            registrant.setCreatedAt(OffsetDateTime.now());
        } else {
            registrant = Registrant
                    .builder()
                    .phoneNumber(phoneNumberDto.getPhoneNumber())
                    .verificationCode(verificationCode)
                    .username(UUID.randomUUID().toString())
                    .status(RegistrantStatus.AWAITED)
                    .build();
        }
        registrantRepository.save(registrant);

        log.info("[TRANSACTION] Close transaction in class: " + this.getClass().getName());
    }

    private void sendMessage(PhoneNumberDto phoneNumberDto, WhatsAppVerificationMsgDto message) {
        ResponseEntity<?> whatsappVerificationResponse = whatsAppClient.sendVerificationCode(
                whatsAppAccessToken, whatsAppApiVersion, whatsAppPhoneNumberId, message
        );
        if (whatsappVerificationResponse.getStatusCode() != HttpStatus.OK) {

        }
    }

    private WhatsAppVerificationMsgDto getWhatsAppVerificationMsg(int verificationCode, String phoneNumber) {
        WhatsAppMessageLanguageDto language = WhatsAppMessageLanguageDto.builder()
                .code("ru")
                .build();
        WhatsAppMessageParameterDto parameter = WhatsAppMessageParameterDto.builder()
                .type("text")
                .text(String.valueOf(verificationCode))
                .build();
        WhatsAppComponentDto bodyComponent = WhatsAppComponentDto.builder()
                .type("body")
                .parameters(List.of(parameter))
                .build();
        WhatsAppComponentDto buttonComponent = WhatsAppComponentDto.builder()
                .type("button")
                .subType("url")
                .index("0")
                .parameters(List.of(parameter))
                .build();
        WhatsAppMessageTemplateDto template = WhatsAppMessageTemplateDto.builder()
                .name("tribe_auth")
                .language(language)
                .components(List.of(bodyComponent, buttonComponent))
                .build();
        WhatsAppVerificationMsgDto message = WhatsAppVerificationMsgDto.builder()
                .messagingProduct("whatsapp")
                .recipientType("individual")
                .to(phoneNumber)
                .type("template")
                .template(template)
                .build();
        return message;
    }

    @Override
    public TokensDTO confirmCodeForLoginWithWhatsApp(PhoneConfirmCodeDto phoneConfirmCodeDto) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        User user = userRepository.findUserByPhoneNumber(phoneConfirmCodeDto.getPhoneNumber());
        if (user == null) {
            Registrant registrant = registrantRepository.findByPhoneNumberAndStatus(
                    phoneConfirmCodeDto.getPhoneNumber(), RegistrantStatus.AWAITED
            );
            if (registrant == null) {
                String message = String.format(
                        "There is no registrant with phone number %s", phoneConfirmCodeDto.getPhoneNumber()
                );
                log.error(message);
                throw new RegistrantNotFoundException(message);
            }
            OffsetDateTime now = OffsetDateTime.now();
            if (!registrant.getVerificationCode().equals(phoneConfirmCodeDto.getVerificationCode())) {
                String message = String.format(
                        "Verification code is wrong for registrant with phone number %s", phoneConfirmCodeDto.getPhoneNumber()
                );
                log.error(message);
                throw new WrongCodeException(message);
            }
            if (now.isAfter(registrant.getCreatedAt().plus(CODE_EXPIRATION_TIME_IN_MIN, ChronoUnit.MINUTES))) {
                String message = String.format(
                        "Verification code is expired for registrant with phone number %s", phoneConfirmCodeDto.getPhoneNumber()
                );
                if (!TransactionSynchronizationManager.isActualTransactionActive()) {
                    registrant.setStatus(RegistrantStatus.EXPIRED);
                    registrantRepository.save(registrant);
                }
                log.error(message);
                throw new ExpiredCodeException(message);
            }
            User newUser = User.builder()
                    .username(registrant.getUsername())
                    .firebaseId(phoneConfirmCodeDto.getFirebaseId())
                    .phoneNumber(registrant.getPhoneNumber())
                    .hasWhatsappAuthentication(true)
                    .build();
            try {
                userRepository.save(newUser);
                registrant.setStatus(RegistrantStatus.CONFIRMED);
                registrantRepository.save(registrant);
                return getTokenDTO(newUser.getId(), newUser.getUserRole());
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                String message = String.format(
                        "Failed to generate token for registrant with phone number %s, error: %s",
                        phoneConfirmCodeDto.getPhoneNumber(),
                        e.getCause()
                );
                log.error(message);
                throw new MakeTokenException(message);
            }
        } else {
            PhoneVerificationCode phoneVerificationCode = phoneVerificationRepository
                    .findByPhoneNumberAndIsEnableIsTrue(phoneConfirmCodeDto.getPhoneNumber());
            if (phoneVerificationCode == null) {
                String message = String.format(
                        "There is no verification code for user with phone number %s",
                        phoneConfirmCodeDto.getPhoneNumber()
                );
                log.error(message);
                throw new WrongCodeException(message);
            }
            if (phoneVerificationCode.getVerificationCode() != phoneConfirmCodeDto.getVerificationCode()) {
                String message = String.format(
                        "Verification code is wrong for user with phone number %s",
                        phoneConfirmCodeDto.getPhoneNumber()
                );
                log.error(message);
                throw new WrongCodeException(message);
            }
            OffsetDateTime now = OffsetDateTime.now();
            if (now.isAfter(phoneVerificationCode.getRequestTime().plus(CODE_EXPIRATION_TIME_IN_MIN, ChronoUnit.MINUTES))) {
                phoneVerificationCode.setEnable(false);
                phoneVerificationRepository.save(phoneVerificationCode);
                String message = String.format(
                        "Verification code is expired for user with phone number %s",
                        phoneConfirmCodeDto.getPhoneNumber()
                );
                log.error(message);
                throw new ExpiredCodeException(message);
            }
            try {
                phoneVerificationCode.setEnable(false);
                phoneVerificationRepository.save(phoneVerificationCode);
                return getTokenDTO(user.getId(), user.getUserRole());
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                String message = String.format(
                        "Failed to generate token for user with phone number %s, error: %s",
                        phoneConfirmCodeDto.getPhoneNumber(),
                        e.getCause()
                );
                log.error(message);
                throw new MakeTokenException(message);
            }
        }
    }

    private TokensDTO getTokensForGoogleUser(String token, UserForSignInUpDTO userForSignInUpDTO) {
        try {
            GoogleIdToken idToken = googleIdTokenVerifier.verify(token);
            if (idToken != null) {
                Payload tokenPayload = idToken.getPayload();
                String googleUserId = tokenPayload.getSubject();
                String email = tokenPayload.getEmail() == null ? null : tokenPayload.getEmail();
                userForSignInUpDTO.setEmail(email);
                return getTokensDTO(userForSignInUpDTO, googleUserId, SocialIdType.GOOGLE);
            } else {
                throw new GoogleIntrospectionException("Invalid idToken");
            }
        } catch (GeneralSecurityException e) {
            throw new GoogleIntrospectionException(e.getMessage());
        } catch (IOException e) {
            throw new JwtDecoderException(e.getMessage());
        }
    }

private TokensDTO getTokenForVkUser(String token, UserForSignInUpDTO userForSign    private TokensDTO getTokenForVkUser(String token, UserForSignInUpDTO userForSignInUpDTO) throws JsonProcessingException {

        VkValidationParams vkValidationParams = new VkValidationParams(token, clientSecret, apiVersion);
        ResponseEntity<String> vkResponse = vkClient.isTokenValid(vkValidationParams);
        VkValidationResponseDTO response;
        if (vkResponse.getStatusCode() == HttpStatus.OK
                && Objects.requireNonNull(vkResponse.getBody()).contains("response")
        ) {
            response = mapStringToVkValidationResponseDTO(vkResponse.getBody());
            String vkUserId = response.getResponse().getUserId().toString();
            return getTokensDTO(userForSignInUpDTO, vkUserId, SocialIdType.VK);
        } else {
            VkValidationErrorDTO vkValidationResponseDTO = mapStringToVkValidationErrorDTO(vkResponse.getBody());
            throw new VkIntrospectionException(vkValidationResponseDTO.getError().getErrorMessage());
        }
    }

    @NotNull
    private TokensDTO getTokensDTO(UserForSignInUpDTO userForSignInUpDTO, String socialId, SocialIdType socialIdType) {
        try {
            User user = null;
            if (socialIdType == SocialIdType.GOOGLE) {
                user = userRepository.findByGoogleId(socialId);
            }
            if (socialIdType == SocialIdType.VK) {
                user = userRepository.findByVkId(socialId);
            }
            if (user != null) {
                user.setFirebaseId(userForSignInUpDTO.getFirebaseId());
                TokensDTO tokensDTO = getTokenDTO(user.getId(), user.getUserRole());
                tokensDTO.setUserId(user.getId());
                return tokensDTO;
            } else {
                User newUser = registerNewUser(userForSignInUpDTO, socialId, socialIdType);
                TokensDTO tokensDTO = getTokenDTO(newUser.getId(), newUser.getUserRole());
                tokensDTO.setUserId(newUser.getId());
                return tokensDTO;
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JwtDecoderException(e.getMessage());
        }
    }

    private VkValidationResponseDTO mapStringToVkValidationResponseDTO(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, VkValidationResponseDTO.class);
    }

    private VkValidationErrorDTO mapStringToVkValidationErrorDTO(String error) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(error, VkValidationErrorDTO.class);
    }

    public User registerNewUser(UserForSignInUpDTO userForSignInUpDTO, String socialId, SocialIdType socialIdType) {
        if (userRepository.existsUserByUsername(userForSignInUpDTO.getUsername())) {
            String message = String.format("Username: %s, already exists", userForSignInUpDTO.getUsername());
            log.error(message);
            throw new UserAlreadyExistException(message);
        }
        User userToSave = null;
        if (socialIdType == SocialIdType.GOOGLE) {
            userToSave = userMapper.mapToUserFromUserGoogleRegistration(userForSignInUpDTO, socialId);
        } else if (socialIdType == SocialIdType.VK) {
            userToSave = userMapper.mapToUserFromUserVkRegistration(userForSignInUpDTO, socialId);
        } else {
            String message = String.format("Unknown socialIdType: %s", socialIdType);
            log.error(message);
            throw new IllegalArgumentException(message);
        }

        UnknownUser unknownUserWithUserToSaveFirebaseId = unknownUserRepository
                .findUnknownUserByFirebaseId(userForSignInUpDTO.getFirebaseId());
        if (unknownUserWithUserToSaveFirebaseId != null) {
            userToSave.addInterestingEventTypes(new HashSet<>(unknownUserWithUserToSaveFirebaseId
                    .getUserInterests()));
        } else {
            List<EventType> allEventTypes = eventTypeRepository.findAll();
            userToSave.addInterestingEventTypes(new HashSet<>(allEventTypes));
        }
        userToSave = saveUser(userToSave);
        return userToSave;
    }

    private TokensDTO getTokenDTO(Long userId, UserRole userRole) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        TokensDTO tokensDTO = new TokensDTO();
        tokensDTO.setUserId(userId);
        tokensDTO.setAccessToken(jwtProvider.generateAccessToken(userId, userRole));
        tokensDTO.setRefreshToken(jwtProvider.generateRefreshToken(userId));
        return tokensDTO;
    }

    private User saveUser(User user) {
        if (userRepository.existsUserByUsername(user.getUsername())) {
            log.error("[EXCEPTION] User with passed username already exists. Username: {}", user.getUsername());
            throw new UserAlreadyExistException(
                    String.format("Passed username already exists: %s", user.getUsername()));
        }
        if (user.getUserEmail() != null && userRepository.existsUserByUserEmail(user.getUserEmail())) {
            log.error("[EXCEPTION] User with passed email already exists. Email: {}", user.getUserEmail());
            throw new UserAlreadyExistException(
                    String.format("Passed email already exists: %s", user.getUserEmail()));
        }
        if (user.getPhoneNumber() != null && userRepository.existsUserByPhoneNumber(user.getPhoneNumber())) {
            log.error("[EXCEPTION] User with passed phoneNumber already exists. PhoneNumber: {}", user.getPhoneNumber());
            throw new UserAlreadyExistException(
                    String.format("Passed phoneNumber already exists: %s", user.getPhoneNumber()));
        }
        user = userRepository.save(user);
        return user;
    }
}
