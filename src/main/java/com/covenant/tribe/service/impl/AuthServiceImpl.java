package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.dto.VkValidationErrorDTO;
import com.covenant.tribe.client.dto.VkValidationResponseDTO;
import com.covenant.tribe.client.vk.VkClient;
import com.covenant.tribe.client.vk.VkValidationParams;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.UnknownUser;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.auth.TokensDTO;
import com.covenant.tribe.dto.user.UserForSignInUpDTO;
import com.covenant.tribe.exeption.auth.GoogleIntrospectionException;
import com.covenant.tribe.exeption.auth.JwtDecoderException;
import com.covenant.tribe.exeption.auth.UnexpectedTokenTypeException;
import com.covenant.tribe.exeption.auth.VkIntrospectionException;
import com.covenant.tribe.exeption.user.UsernameDataAlreadyExistException;
import com.covenant.tribe.repository.EventTypeRepository;
import com.covenant.tribe.repository.UnknownUserRepository;
import com.covenant.tribe.repository.UserRepository;
import com.covenant.tribe.security.JwtProvider;
import com.covenant.tribe.service.AuthService;
import com.covenant.tribe.util.mapper.UserMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    VkClient vkClient;
    GoogleIdTokenVerifier googleIdTokenVerifier;
    JwtProvider jwtProvider;
    UserRepository userRepository;
    UserMapper userMapper;
    UnknownUserRepository unknownUserRepository;
    EventTypeRepository eventTypeRepository;


    @Value(value = "${spring.cloud.openfeign.client.config.vk-client.client-id}")
    String clientId;

    @Value(value = "${spring.cloud.openfeign.client.config.vk-client.client-secret}")
    String clientSecret;

    @Value(value = "${spring.cloud.openfeign.client.config.vk-client.api-version}")
    String apiVersion;

    @Autowired
    public AuthServiceImpl(VkClient vkClient,
                           GoogleIdTokenVerifier googleIdTokenVerifier,
                           JwtProvider jwtProvider,
                           UserRepository userRepository,
                           UserMapper userMapper,
                           UnknownUserRepository unknownUserRepository,
                           EventTypeRepository eventTypeRepository
    ) {
        this.vkClient = vkClient;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.unknownUserRepository = unknownUserRepository;
        this.eventTypeRepository = eventTypeRepository;
    }

    private final String GOOGLE_TOKEN_TYPE = "google";
    private final String VK_TOKEN_TYPE = "vk";

    @Transactional
    @Override
    public TokensDTO getTokensToUserFromSocialNetworks(
            String token, String tokenType, UserForSignInUpDTO userForSignInUpDTO
    ) throws JsonProcessingException {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

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

    private TokensDTO getTokensForGoogleUser(String token, UserForSignInUpDTO userForSignInUpDTO)  {
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

        User userToSave = userMapper.mapToUser(userForSignInUpDTO, socialId);
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
        tokensDTO.setAccessToken(jwtProvider.generateAccessToken(userId));
        tokensDTO.setRefreshToken(jwtProvider.generateRefreshToken(userId));
        return tokensDTO;
    }

    private User saveUser(User user) {
        if (userRepository.existsUserByUsername(user.getUsername())) {
            log.error("[EXCEPTION] User with passed username already exists. Username: {}", user.getUsername());
            throw new UsernameDataAlreadyExistException(
                    String.format("Passed username already exists: %s", user.getUsername()));
        }
        if (userRepository.existsUserByUserEmail(user.getUserEmail())) {
            log.error("[EXCEPTION] User with passed email already exists. Email: {}", user.getUserEmail());
            throw new UsernameDataAlreadyExistException(
                    String.format("Passed email already exists: %s", user.getUserEmail()));
        }
        if (userRepository.existsUserByPhoneNumber(user.getPhoneNumber())) {
            log.error("[EXCEPTION] User with passed phoneNumber already exists. PhoneNumber: {}", user.getPhoneNumber());
            throw new UsernameDataAlreadyExistException(
                    String.format("Passed phoneNumber already exists: %s", user.getPhoneNumber()));
        }
        user = userRepository.save(user);
        return user;
    }
}
