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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
                           JwtProvider jwtProvider,
                           UserRepository userRepository,
                           UserMapper userMapper,
                           UnknownUserRepository unknownUserRepository,
                           EventTypeRepository eventTypeRepository
    ) {
        this.vkClient = vkClient;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.unknownUserRepository = unknownUserRepository;
        this.eventTypeRepository = eventTypeRepository;
    }

    private final String GOOGLE_TOKEN_TYPE = "google";
    private final String VK_TOKEN_TYPE = "vk";

    @Override
    public TokensDTO getTokensToUserFromSocialNetworks(
            String token, String tokenType, UserForSignInUpDTO userForSignInUpDTO
    ) throws JsonProcessingException {
        if (tokenType.equals(GOOGLE_TOKEN_TYPE)) return getTokensForGoogleUser(tokenType);
        if (tokenType.equals(VK_TOKEN_TYPE)) return getTokenForVkUser(token, userForSignInUpDTO);
        throw new UnexpectedTokenTypeException(tokenType);
    }

    private TokensDTO getTokensForGoogleUser(String token) {
        return new TokensDTO();
    }

    @Transactional
    public TokensDTO getTokenForVkUser(String token, UserForSignInUpDTO userForSignInUpDTO) throws JsonProcessingException {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        VkValidationParams vkValidationParams = new VkValidationParams(token, clientSecret, apiVersion);
        ResponseEntity<String> vkResponse = vkClient.isTokenValid(vkValidationParams);
        VkValidationResponseDTO response;
        if (vkResponse.getStatusCode() == HttpStatus.OK
                && Objects.requireNonNull(vkResponse.getBody()).contains("response")
        ) {
            response = mapStringToVkValidationResponseDTO(vkResponse.getBody());
            String vkUserId = VK_TOKEN_TYPE + response.getResponse().getUserId();
            try {
                String accessToken = jwtProvider.generateAccessToken(123456L);
                String refreshToken = jwtProvider.generateRefreshToken(123456L);
                User user = userRepository.findBySocialId(vkUserId);
                if (user != null) {
                    log.info("[TRANSACTION] Close transaction in class: " + this.getClass().getName());
                    return new TokensDTO(user.getId(), accessToken, refreshToken);
                } else {
                    Long userId = registerNewUser(userForSignInUpDTO, vkUserId);
                    log.info("[TRANSACTION] Close transaction in class: " + this.getClass().getName());
                    return new TokensDTO(userId, accessToken, refreshToken);
                }
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        } else {
            VkValidationErrorDTO vkValidationResponseDTO = mapStringToVkValidationErrorDTO(vkResponse.getBody());
            throw new VkIntrospectionException(vkValidationResponseDTO.getError().getErrorMessage());
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
