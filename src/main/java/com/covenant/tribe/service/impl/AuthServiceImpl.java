package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.dto.VkValidationErrorDTO;
import com.covenant.tribe.client.dto.VkValidationResponseDTO;
import com.covenant.tribe.client.vk.VkClient;
import com.covenant.tribe.client.vk.VkValidationParams;
import com.covenant.tribe.domain.auth.ResetCodes;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.Registrant;
import com.covenant.tribe.domain.user.RegistrantStatus;
import com.covenant.tribe.domain.user.UnknownUser;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.auth.*;
import com.covenant.tribe.dto.user.UserForSignInUpDTO;
import com.covenant.tribe.exeption.auth.*;
import com.covenant.tribe.exeption.user.UserAlreadyExistException;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.repository.*;
import com.covenant.tribe.security.JwtProvider;
import com.covenant.tribe.service.AuthService;
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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final String FROM_EMAIL = "tribe@tribual.ru";
    private final String EMAIL_SUBJECT = "Регистрация в приложении Tribe";
    private final Integer CODE_EXPIRATION_TIME_IN_MIN = 5;

    private final Integer MIN_VALUE_FOR_PASSWORD = 1000;
    private final Integer MAX_VALUE_FOR_PASSWORD = 9999;

    RegistrantRepository registrantRepository;
    PasswordEncoder encoder;
    JavaMailSender mailSender;
    VkClient vkClient;
    GoogleIdTokenVerifier googleIdTokenVerifier;
    JwtProvider jwtProvider;
    UserRepository userRepository;
    UserMapper userMapper;
    UnknownUserRepository unknownUserRepository;
    ResetCodeRepository resetCodeRepository;
    EventTypeRepository eventTypeRepository;
    RegistrantMapper registrantMapper;


    @Value(value = "${spring.cloud.openfeign.client.config.vk-client.client-id}")
    String clientId;

    @Value(value = "${spring.cloud.openfeign.client.config.vk-client.client-secret}")
    String clientSecret;

    @Value(value = "${spring.cloud.openfeign.client.config.vk-client.api-version}")
    String apiVersion;

    @Autowired
    public AuthServiceImpl(RegistrantRepository registrantRepository, PasswordEncoder encoder, ResetCodeRepository resetCodeRepository, JavaMailSender mailSender, VkClient vkClient, GoogleIdTokenVerifier googleIdTokenVerifier, JwtProvider jwtProvider, UserRepository userRepository, UserMapper userMapper, UnknownUserRepository unknownUserRepository, EventTypeRepository eventTypeRepository, RegistrantMapper registrantMapper) {
        this.registrantRepository = registrantRepository;
        this.encoder = encoder;
        this.mailSender = mailSender;
        this.vkClient = vkClient;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.unknownUserRepository = unknownUserRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.registrantMapper = registrantMapper;
        this.resetCodeRepository = resetCodeRepository;
    }

    private final String GOOGLE_TOKEN_TYPE = "google";
    private final String VK_TOKEN_TYPE = "vk";

    @Transactional
    @Override
    public TokensDTO getTokensToUserFromSocialNetworks(
            String token, String tokenType, UserForSignInUpDTO userForSignInUpDTO
    ) throws JsonProcessingException {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());
        String bluetoothId = userForSignInUpDTO.getBluetoothId();
        String firebaseId = userForSignInUpDTO.getFirebaseId();

        if (bluetoothId.isEmpty() || firebaseId.isBlank()) {
            throw new BadCredentialsException(
                    String.format("BluetoothId: %s and FirebaseId: %s  can't be empty or null'",
                            bluetoothId, firebaseId)
            );
        }

        if (tokenType.equals(GOOGLE_TOKEN_TYPE)) return getTokensForGoogleUser(token, userForSignInUpDTO);
        if (tokenType.equals(VK_TOKEN_TYPE)) return getTokenForVkUser(token, userForSignInUpDTO);

        log.info("[TRANSACTION] Close transaction in class: " + this.getClass().getName());

        throw new UnexpectedTokenTypeException(tokenType);
    }

    @Override
    public TokensDTO refreshTokens(String token) {
        try {
            Long userId = Long.parseLong(jwtProvider.getRefreshTokenClaims(token).getSubject());
            TokensDTO tokensDTO = new TokensDTO();
            tokensDTO.setUserId(userId);
            tokensDTO.setAccessToken(jwtProvider.generateAccessToken(userId));
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

        int verificationCode = getRandomVerificationNumber(1000, 9999);
        Registrant registrant = registrantRepository.findByEmail(registrantRequestDTO.getEmail());
        String emailMessage = String.format("Код подтверждения: %s", verificationCode);
        if (registrant == null) {
            registrant = registrantMapper.mapToRegistrant(registrantRequestDTO, verificationCode);
            Registrant newRegistrant = registrantRepository.save(registrant);
            sendEmail(EMAIL_SUBJECT, emailMessage, registrantRequestDTO.getEmail());
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
            sendEmail(EMAIL_SUBJECT, emailMessage, registrantRequestDTO.getEmail());
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
        String bluetoothId = confirmRegistrationDTO.getBluetoothId();
        String firebaseId = confirmRegistrationDTO.getFirebaseId();

        if (bluetoothId.isEmpty() || firebaseId.isBlank()) {
            throw new BadCredentialsException(
                    String.format("BluetoothId: %s and FirebaseId: %s  can't be empty or null'",
                            bluetoothId, firebaseId)
            );
        }

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
                .findUnknownUserByBluetoothId(confirmRegistrationDTO.getBluetoothId());
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
            TokensDTO tokensDTO = getTokenDTO(savedUser.getId());
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
            return getTokenDTO(user.getId());
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
        int resetConfirmationCode = generateResetCode();
        String title = "Код для подтверждения сброса пароля";
        String message = String
                .format(
                        "Ваш код для подтверждения сброса пароля: %s",
                        resetConfirmationCode
                );
        ResetCodes resetCodes = ResetCodes.builder()
                .resetCode(resetConfirmationCode)
                .email(resetPasswordDTO.getEmail())
                .requestTime(Instant.now())
                .isEnable(true)
                .build();
        resetCodeRepository.save(resetCodes);
        sendEmail(title, message, resetPasswordDTO.getEmail());
        user.setPassword(encoder.encode(String.valueOf(resetConfirmationCode)));
        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        User user = userRepository
                .findById(changePasswordDTO.getUserId())
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
    public TokensDTO confirmResetCode(ConfirmCodeDTO confirmResetCodeDTO) {
        ResetCodes resetCodes = resetCodeRepository
                .findByEmailAndIsEnable(confirmResetCodeDTO.getEmail(), true);
        if (resetCodes == null) {
            log.error("Reset code with email: " + confirmResetCodeDTO.getEmail() + " does not exist");
            throw new UserNotFoundException(
                    String.format("Reset code with email: %s, does not exist", confirmResetCodeDTO.getEmail())
            );
        }
        if (resetCodes.getResetCode() != confirmResetCodeDTO.getVerificationCode()) {
            resetCodes.setEnable(false);
            String message = String.format("Reset code for email: %s, does not match", confirmResetCodeDTO.getEmail());
            log.error(message);
            throw new WrongCodeException(message);
        }
        if (resetCodes.getRequestTime().plus(CODE_EXPIRATION_TIME_IN_MIN, ChronoUnit.MINUTES).isBefore(Instant.now())) {
            resetCodes.setEnable(false);
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
            TokensDTO tokens = getTokenDTO(user.getId());
            resetCodes.setEnable(false);
            resetCodeRepository.save(resetCodes);
            return tokens;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new MakeTokenException(e.getMessage());
        }
    }

    private int generateResetCode() {
        return new Random()
                .nextInt(MAX_VALUE_FOR_PASSWORD - MIN_VALUE_FOR_PASSWORD) + MIN_VALUE_FOR_PASSWORD;
    }

    private void sendEmail(String subject, String message, String email) {
        SimpleMailMessage mailMsg = new SimpleMailMessage();
        mailMsg.setFrom(FROM_EMAIL);
        mailMsg.setTo(email);
        mailMsg.setSubject(subject);
        mailMsg.setText(message);
        mailSender.send(mailMsg);
    }

    private int getRandomVerificationNumber(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

    private TokensDTO getTokensForGoogleUser(String token, UserForSignInUpDTO userForSignInUpDTO) {
        try {
            GoogleIdToken idToken = googleIdTokenVerifier.verify(token);
            if (idToken != null) {
                Payload tokenPayload = idToken.getPayload();
                String socialId = GOOGLE_TOKEN_TYPE + tokenPayload.getSubject();
                String email = tokenPayload.getEmail() == null ? "" : tokenPayload.getEmail();
                userForSignInUpDTO.setEmail(email);
                return getTokensDTO(userForSignInUpDTO, socialId);
            } else {
                throw new GoogleIntrospectionException("Invalid idToken");
            }
        } catch (GeneralSecurityException e) {
            throw new GoogleIntrospectionException(e.getMessage());
        } catch (IOException e) {
            throw new JwtDecoderException(e.getMessage());
        }
    }

    public TokensDTO getTokenForVkUser(String token, UserForSignInUpDTO userForSignInUpDTO) throws JsonProcessingException {

        VkValidationParams vkValidationParams = new VkValidationParams(token, clientSecret, apiVersion);
        ResponseEntity<String> vkResponse = vkClient.isTokenValid(vkValidationParams);
        VkValidationResponseDTO response;
        if (vkResponse.getStatusCode() == HttpStatus.OK
                && Objects.requireNonNull(vkResponse.getBody()).contains("response")
        ) {
            response = mapStringToVkValidationResponseDTO(vkResponse.getBody());
            String socialId = VK_TOKEN_TYPE + response.getResponse().getUserId();
            return getTokensDTO(userForSignInUpDTO, socialId);
        } else {
            VkValidationErrorDTO vkValidationResponseDTO = mapStringToVkValidationErrorDTO(vkResponse.getBody());
            throw new VkIntrospectionException(vkValidationResponseDTO.getError().getErrorMessage());
        }
    }

    @NotNull
    private TokensDTO getTokensDTO(UserForSignInUpDTO userForSignInUpDTO, String socialId) {
        try {
            User user = userRepository.findBySocialId(socialId);
            if (user != null) {
                TokensDTO tokensDTO = getTokenDTO(user.getId());
                tokensDTO.setUserId(user.getId());
                return tokensDTO;
            } else {
                Long userId = registerNewUser(userForSignInUpDTO, socialId);
                TokensDTO tokensDTO = getTokenDTO(userId);
                tokensDTO.setUserId(userId);
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

    public Long registerNewUser(UserForSignInUpDTO userForSignInUpDTO, String socialId) {
        if (userRepository.existsUserByUsername(userForSignInUpDTO.getUsername())) {
            String message = String.format("Username: %s, already exists", userForSignInUpDTO.getUsername());
            log.error(message);
            throw new UserAlreadyExistException(message);
        }

        User userToSave = userMapper.mapToUserFromUserForSignInUpDTO(userForSignInUpDTO, socialId);
        UnknownUser unknownUserWithUserToSaveBluetoothId = unknownUserRepository
                .findUnknownUserByBluetoothId(userForSignInUpDTO.getBluetoothId());
        if (unknownUserWithUserToSaveBluetoothId != null) {
            userToSave.addInterestingEventTypes(new HashSet<>(unknownUserWithUserToSaveBluetoothId
                    .getUserInterests()));
        } else {
            List<EventType> allEventTypes = eventTypeRepository.findAll();
            userToSave.addInterestingEventTypes(new HashSet<>(allEventTypes));
        }
        userToSave = saveUser(userToSave);
        return userToSave.getId();
    }

    private TokensDTO getTokenDTO(Long userId) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        TokensDTO tokensDTO = new TokensDTO();
        tokensDTO.setUserId(userId);
        tokensDTO.setAccessToken(jwtProvider.generateAccessToken(userId));
        tokensDTO.setRefreshToken(jwtProvider.generateRefreshToken(userId));
        return tokensDTO;
    }

    private User saveUser(User user) {
        if (userRepository.existsUserByUsername(user.getUsername())) {
            log.error("[EXCEPTION] User with passed username already exists. Username: {}", user.getUsername());
            throw new UserAlreadyExistException(
                    String.format("Passed username already exists: %s", user.getUsername()));
        }
        if (userRepository.existsUserByUserEmail(user.getUserEmail())) {
            log.error("[EXCEPTION] User with passed email already exists. Email: {}", user.getUserEmail());
            throw new UserAlreadyExistException(
                    String.format("Passed email already exists: %s", user.getUserEmail()));
        }
        if (userRepository.existsUserByPhoneNumber(user.getPhoneNumber())) {
            log.error("[EXCEPTION] User with passed phoneNumber already exists. PhoneNumber: {}", user.getPhoneNumber());
            throw new UserAlreadyExistException(
                    String.format("Passed phoneNumber already exists: %s", user.getPhoneNumber()));
        }
        user = userRepository.save(user);
        return user;
    }
}
