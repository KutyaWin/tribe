package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.vk.VkClient;
import com.covenant.tribe.client.vk.VkValidationParams;
import com.covenant.tribe.client.whatsapp.WhatsAppClient;
import com.covenant.tribe.domain.auth.EmailVerificationCode;
import com.covenant.tribe.domain.auth.PhoneVerificationCode;
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
import com.covenant.tribe.service.MailService;
import com.covenant.tribe.service.VerificationCodeService;
import com.covenant.tribe.util.mapper.RegistrantMapper;
import com.covenant.tribe.util.mapper.UserMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.webtoken.JsonWebSignature;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private final Integer CODE_EXPIRATION_TIME_IN_MIN = 5;

    @Mock
    private RegistrantRepository registrantRepository;
    @Mock
    private PhoneVerificationRepository phoneVerificationRepository;
    @Mock
    private VerificationCodeService verificationCodeService;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private MailService mailService;
    @Mock
    private VkClient vkClient;
    @Mock
    private WhatsAppClient whatsAppClient;
    @Mock
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UnknownUserRepository unknownUserRepository;
    @Mock
    private EmailVerificationRepository emailVerificationRepository;
    @Mock
    private EventTypeRepository eventTypeRepository;
    @Mock
    private RegistrantMapper registrantMapper;

    @InjectMocks
    private AuthServiceImpl authService;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getTokensToUserFromSocialNetworks_shouldThrowAnExceptionWhenUnexpectedTokenType() {
        // Given
        String tokenType = "123";
        String token = "123";
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();

        // Then
        assertThrows(UnexpectedTokenTypeException.class, () -> authService.getTokensToUserFromSocialNetworks(token, tokenType, userForSignInUpDTO));
    }



    @Test
    void getTokensToUserFromSocialNetworks_shouldReturnTokenWhenTokenTypeIsGoogleType()
            throws IOException, GeneralSecurityException {
        // Given
        String tokenType = "google";
        String token = "123";
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();
        User user = new User();
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setSubject("123");

        when(userRepository.findByGoogleId(anyString())).thenReturn(user);
        when(userMapper.mapToUserFromUserGoogleRegistration(any(), anyString())).thenReturn(user);

        lenient().when(authService.registerNewUser(userForSignInUpDTO, anyString(), SocialIdType.GOOGLE))
                .thenReturn(user);
        when(googleIdTokenVerifier.verify(anyString())).thenReturn(new GoogleIdToken(
                new JsonWebSignature.Header(),
                payload,
                new byte[] {0},
                new byte[] {0}
        ));

        // Wnen
        TokensDTO dto = authService.getTokensToUserFromSocialNetworks(token, tokenType, userForSignInUpDTO);

        // Then
        assertNotNull(dto);
    }

    @Test
    void getTokensToUserFromSocialNetworks_shouldReturnTokenWhenTokenTypeIsVKType()
            throws IOException {
        // Given
        String tokenType = "vk";
        String token = "123";
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();
        User user = new User();
        String response =  "{ \"response\" : {" +
                "\"date\" : \"1200\"," +
                "\"expire\" : \"1200\"," +
                "\"success\" : \"1\"," +
                "\"user_id\" : \"1\"" +
                "} }";

        when(vkClient.isTokenValid(any(VkValidationParams.class)))
                .thenReturn(ResponseEntity.ok(response));
        when(userMapper.mapToUserFromUserVkRegistration(any(), anyString())).thenReturn(user);

        when(authService.registerNewUser(userForSignInUpDTO, anyString(), SocialIdType.VK))
                .thenReturn(user);

        // Wnen
        TokensDTO dto = authService.getTokensToUserFromSocialNetworks(token, tokenType, userForSignInUpDTO);

        // Then
        assertNotNull(dto);
    }




    @Test
    void refreshTokens_shouldReturnDto() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
                "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        User user = new User();
        user.setId(1L);
        user.setUserRole(UserRole.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(jwtProvider.getRefreshTokenClaims(anyString())).thenReturn(
                generateClaims()
        );

        // When
        TokensDTO dto = authService.refreshTokens(token);

        // Then
        assertNotNull(dto);
    }

    @Test
    void refreshTokens_shouldThrowExceptionIfUnknownUser() throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
                "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        User user = new User();
        user.setId(1L);
        user.setUserRole(UserRole.USER);
        when(jwtProvider.getRefreshTokenClaims(anyString())).thenReturn(
                generateClaims()
        );

        // Then
        assertThrows(UserNotFoundException.class, () -> authService.refreshTokens(token));
    }

    @Test
    void refreshTokens_shouldThrowExceptionIfExceptionCaught() throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
                "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        User user = new User();
        user.setId(1L);
        user.setUserRole(UserRole.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtProvider.getRefreshTokenClaims(anyString())).thenReturn(
                generateClaims()
        );
        when(jwtProvider.generateAccessToken(any(), any())).thenThrow(NoSuchAlgorithmException.class);

        // Then
        assertThrows(JwtDecoderException.class, () -> authService.refreshTokens(token));

    }

    @Test
    void addRegistrantWithEmail_shouldReturnDtoIfRegistrantIsNew() {
        // Given
        RegistrantRequestDTO dto = new RegistrantRequestDTO("email@mail.com", "password",
                "user");
        when(registrantRepository.save(any())).thenReturn(new Registrant());

        // When
        RegistrantResponseDTO responseDTO = authService.addRegistrantWithEmail(dto);

        //Then
        assertNotNull(responseDTO);
    }

    @Test
    void addRegistrantWithEmail_shouldThrowExceptionIfUserAlreadyExistsAndConfirmed() {
        // Given
        RegistrantRequestDTO dto = new RegistrantRequestDTO("email@mail.com", "password",
                "user");
        Registrant registrant = new Registrant();
        registrant.setStatus(RegistrantStatus.CONFIRMED);
        when(registrantRepository.findByEmail("email@mail.com")).thenReturn(registrant);

        // Then
        assertThrows(UserAlreadyExistException.class, () -> authService.addRegistrantWithEmail(dto));

    }

    @Test
    void addRegistrantWithEmail_shouldReturnDtoIfUserAlreadyExistsAndNotConfirmed() {
        // Given
        RegistrantRequestDTO dto = new RegistrantRequestDTO("email@mail.com", "password",
                "user");
        Registrant registrant = new Registrant();
        registrant.setStatus(RegistrantStatus.EXPIRED);
        when(registrantRepository.findByEmail("email@mail.com")).thenReturn(registrant);

        // When
        RegistrantResponseDTO responseDTO = authService.addRegistrantWithEmail(dto);

        //Then
        assertNotNull(responseDTO);

    }

    @Test
    void confirmEmailRegistration_shouldThrowExceptionIfUserNotFound() {
        // Given
        ConfirmRegistrationDTO dto = new ConfirmRegistrationDTO();

        // Then
        assertThrows(UserNotFoundException.class, () -> authService.confirmEmailRegistration(dto));
    }

    @Test
    void confirmEmailRegistration_shouldThrowExceptionIfCodeExpired() {
        // Given
        ConfirmRegistrationDTO dto = new ConfirmRegistrationDTO();
        dto.setRegistrantId(1L);
        Registrant registrant = new Registrant();
        registrant.setCreatedAt(OffsetDateTime.now().minusMinutes(CODE_EXPIRATION_TIME_IN_MIN * 3L));
        when(registrantRepository.findById(1L)).thenReturn(Optional.of(registrant));

        // Then
        assertThrows(ExpiredCodeException.class, () -> authService.confirmEmailRegistration(dto));
    }

    @Test
    void confirmEmailRegistration_shouldThrowExceptionIfWrongCode() {
        // Given
        ConfirmRegistrationDTO dto = new ConfirmRegistrationDTO();
        dto.setRegistrantId(1L);
        dto.setVerificationCode(10);
        Registrant registrant = new Registrant();
        registrant.setCreatedAt(OffsetDateTime.now());
        registrant.setVerificationCode(20);
        when(registrantRepository.findById(1L)).thenReturn(Optional.of(registrant));

        // Then
        assertThrows(WrongCodeException.class, () -> authService.confirmEmailRegistration(dto));
    }

    @Test
    void confirmEmailRegistration_shouldGetInterestsForUnknownUserIfItsNotFound() {
        // Given
        ConfirmRegistrationDTO dto = new ConfirmRegistrationDTO();
        dto.setRegistrantId(1L);
        dto.setVerificationCode(10);
        dto.setFirebaseId("5");
        Registrant registrant = new Registrant();
        registrant.setCreatedAt(OffsetDateTime.now());
        registrant.setVerificationCode(10);

        EventType et1 = new EventType();
        EventType et2 = new EventType();
        List<EventType> userInterests = List.of(et1, et2);
        when(registrantRepository.findById(1L)).thenReturn(Optional.of(registrant));
        when(eventTypeRepository.findAll()).thenReturn(userInterests);
        when(userMapper.buildUserFromConfirmRegistrationDTORegistrantAndUserInterests(any(), any(), any()))
                .thenReturn(new User());
        when(userRepository.save(any())).thenReturn(new User());
        when(unknownUserRepository.findUnknownUserByFirebaseId("5")).thenReturn(null);

        // When
        authService.confirmEmailRegistration(dto);

        // Then
        verify(eventTypeRepository, atLeastOnce()).findAll();
    }

    @Test
    void confirmEmailRegistration_shouldGetInterestsForUnknownUserIfItsFound() {
        // Given
        ConfirmRegistrationDTO dto = new ConfirmRegistrationDTO();
        dto.setRegistrantId(1L);
        dto.setVerificationCode(10);
        dto.setFirebaseId("5");
        Registrant registrant = new Registrant();
        registrant.setCreatedAt(OffsetDateTime.now());
        registrant.setVerificationCode(10);

        EventType et1 = new EventType();
        EventType et2 = new EventType();
        List<EventType> userInterests = List.of(et1, et2);
        UnknownUser unknownUser = new UnknownUser(1L, "5", userInterests);
        when(registrantRepository.findById(1L)).thenReturn(Optional.of(registrant));
        when(userMapper.buildUserFromConfirmRegistrationDTORegistrantAndUserInterests(any(), any(), any()))
                .thenReturn(new User());
        when(userRepository.save(any())).thenReturn(new User());
        when(unknownUserRepository.findUnknownUserByFirebaseId("5")).thenReturn(unknownUser);

        // When
        authService.confirmEmailRegistration(dto);

        // Then
        verify(eventTypeRepository, never()).findAll();
        verify(userMapper, never())
                .buildUserFromConfirmRegistrationDTORegistrantAndUserInterests(any(), eq(null), any());
    }

    @Test
    void confirmEmailRegistration_shouldReturnDto() {
        // Given
        ConfirmRegistrationDTO dto = new ConfirmRegistrationDTO();
        dto.setRegistrantId(1L);
        dto.setVerificationCode(10);
        dto.setFirebaseId("5");
        Registrant registrant = new Registrant();
        registrant.setCreatedAt(OffsetDateTime.now());
        registrant.setVerificationCode(10);

        EventType et1 = new EventType();
        EventType et2 = new EventType();
        List<EventType> userInterests = List.of(et1, et2);
        UnknownUser unknownUser = new UnknownUser(1L, "5", userInterests);
        when(registrantRepository.findById(1L)).thenReturn(Optional.of(registrant));
        when(userMapper.buildUserFromConfirmRegistrationDTORegistrantAndUserInterests(any(), any(), any()))
                .thenReturn(new User());
        when(userRepository.save(any())).thenReturn(new User());
        when(unknownUserRepository.findUnknownUserByFirebaseId("5")).thenReturn(unknownUser);

        // When
        TokensDTO tokensDTO = authService.confirmEmailRegistration(dto);

        // Then
        assertNotNull(tokensDTO);
    }

    @Test
    void confirmEmailRegistration_shouldThrowMakeTokenException() {
        // Given
        ConfirmRegistrationDTO dto = new ConfirmRegistrationDTO();
        dto.setRegistrantId(1L);
        dto.setVerificationCode(10);
        dto.setFirebaseId("5");
        Registrant registrant = new Registrant();
        registrant.setCreatedAt(OffsetDateTime.now());
        registrant.setVerificationCode(10);

        EventType et1 = new EventType();
        EventType et2 = new EventType();
        List<EventType> userInterests = List.of(et1, et2);
        UnknownUser unknownUser = new UnknownUser(1L, "5", userInterests);
        when(registrantRepository.findById(1L)).thenReturn(Optional.of(registrant));
        when(userMapper.buildUserFromConfirmRegistrationDTORegistrantAndUserInterests(any(), any(), any()))
                .thenReturn(new User());
        when(userRepository.save(any())).thenReturn(new User());
        when(unknownUserRepository.findUnknownUserByFirebaseId("5")).thenReturn(unknownUser);
        when(authService.confirmEmailRegistration(dto)).thenThrow(NoSuchAlgorithmException.class);

        // Then
        assertThrows(MakeTokenException.class, () -> authService.confirmEmailRegistration(dto));
    }



    @Test
    void loginUserWithEmail_shouldThrowExceptionIfUserNotFound() {
        // Given
        EmailLoginDTO dto = new EmailLoginDTO();

        // Then
        assertThrows(UserNotFoundException.class, () -> authService.loginUserWithEmail(dto));
    }

    @Test
    void loginUserWithEmail_shouldThrowExceptionIfWrongCredentials() {
        // Given
        EmailLoginDTO dto = new EmailLoginDTO("email", "password");
        when(userRepository.findUserByUserEmail(anyString())).thenReturn(Optional.of(new User()));

        // Then
        assertThrows(BadCredentialsException.class, () -> authService.loginUserWithEmail(dto));
    }

    @Test
    void loginUserWithEmail_shouldThrowMakeTokenException() {
        // Given
        User user = new User();
        user.setPassword("password");
        EmailLoginDTO dto = new EmailLoginDTO("email", "password");
        when(userRepository.findUserByUserEmail(anyString())).thenReturn(Optional.of(user));
        when(encoder.matches(any(), any())).thenReturn(true);
        when(authService.loginUserWithEmail(dto)).thenThrow(InvalidKeySpecException.class);

        // Then
        assertThrows(MakeTokenException.class, () -> authService.loginUserWithEmail(dto));
    }

    @Test
    void loginUserWithEmail_shouldReturnDto() {
        // Given
        User user = new User();
        user.setPassword("password");
        EmailLoginDTO dto = new EmailLoginDTO("email", "password");
        when(userRepository.findUserByUserEmail(anyString())).thenReturn(Optional.of(user));
        when(encoder.matches(any(), any())).thenReturn(true);

        // When
        TokensDTO tokensDTO = authService.loginUserWithEmail(dto);

        // Then
        assertNotNull(tokensDTO);
    }

    @Test
    void sendResetCodeToEmail_shouldThrowExceptionIfUserNotFound() {
        // Given
        ResetPasswordDTO dto = new ResetPasswordDTO("email");

        // Then
        assertThrows(UserNotFoundException.class, () -> authService.sendResetCodeToEmail(dto));
    }

    @Test
    void sendResetCodeToEmail_shouldThrowExceptionIfNoEmailAuthentication() {
        // Given
        ResetPasswordDTO dto = new ResetPasswordDTO("email");
        User user = User.builder()
                .hasEmailAuthentication(false)
                .userEmail("email")
                .build();
        when(userRepository.findUserByUserEmail("email")).thenReturn(Optional.of(user));

        // Then
        assertThrows(BadCredentialsException.class, () -> authService.sendResetCodeToEmail(dto));
    }

    @Test
    void sendResetCodeToEmail_shouldWorkIfVerificationCodeNotFound() {
        // Given
        ResetPasswordDTO dto = new ResetPasswordDTO("email");
        User user = User.builder()
                .hasEmailAuthentication(true)
                .userEmail("email")
                .build();
        when(userRepository.findUserByUserEmail("email")).thenReturn(Optional.of(user));
        when(verificationCodeService.getVerificationCode(anyInt(), anyInt())).thenReturn(1);

        //When
        when(emailVerificationRepository.findByEmailAndIsEnable("email", true))
                .thenReturn(null);
        authService.sendResetCodeToEmail(dto);

        // Then
        assertDoesNotThrow(() -> authService.sendResetCodeToEmail(dto));
        verify(mailService, atLeastOnce()).sendEmail(anyString(), anyString(), eq("email"));
        verify(emailVerificationRepository, atLeastOnce()).save(any(EmailVerificationCode.class));
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    void sendResetCodeToEmail_shouldWorkIfVerificationCodeFound() {
        // Given
        ResetPasswordDTO dto = new ResetPasswordDTO("email");
        User user = User.builder()
                .hasEmailAuthentication(true)
                .userEmail("email")
                .build();
        EmailVerificationCode code = new EmailVerificationCode();
        when(userRepository.findUserByUserEmail("email")).thenReturn(Optional.of(user));
        when(verificationCodeService.getVerificationCode(anyInt(), anyInt())).thenReturn(1);

        //When
        when(emailVerificationRepository.findByEmailAndIsEnable("email", true))
                .thenReturn(code);
        authService.sendResetCodeToEmail(dto);

        // Then
        assertDoesNotThrow(() -> authService.sendResetCodeToEmail(dto));
        verify(mailService, atLeastOnce()).sendEmail(anyString(), anyString(), eq("email"));
        verify(emailVerificationRepository, atLeastOnce()).save(any(EmailVerificationCode.class));
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    void changePassword_shouldThrowExceptionWhenUserNotFound() {
        // Given
        ChangePasswordDTO dto = new ChangePasswordDTO();

        // Then
        assertThrows(UserNotFoundException.class, () -> authService.changePassword(dto));
    }

    @Test
    void changePassword_shouldThrowExceptionWhenCredentialsAreWrong() {
        // Given
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setUserId(1L);
        when(userRepository.findUserByIdAndStatus(1L, UserStatus.ENABLED))
                .thenReturn(Optional.of(new User()));

        // Then
        assertThrows(BadCredentialsException.class, () -> authService.changePassword(dto));
    }

    @Test
    void changePassword_shouldWorkWhenUserIsFoundAndCredentialsAreRight() {
        // Given
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setUserId(1L);
        dto.setOldPassword("oldPassword");
        User user = new User();
        user.setPassword("password");
        when(userRepository.findUserByIdAndStatus(1L, UserStatus.ENABLED))
                .thenReturn(Optional.of(user));
        when(encoder.matches("oldPassword", "password")).thenReturn(true);

        // Then
        assertDoesNotThrow(() -> authService.changePassword(dto));
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    void confirmResetCode_shouldThrowExceptionIfResetCodeNotFound() {
        // Given
        EmailConfirmCodeDto dto = new EmailConfirmCodeDto();

        // Then
        assertThrows(UserNotFoundException.class, () -> authService.confirmResetCode(dto));
    }

    @Test
    void confirmResetCode_shouldThrowExceptionIfResetCodeDoesntMatch() {
        // Given
        EmailConfirmCodeDto dto = new EmailConfirmCodeDto();
        dto.setVerificationCode(1);
        dto.setEmail("email");
        when(emailVerificationRepository.findByEmailAndIsEnable("email", true))
                .thenReturn(new EmailVerificationCode());

        // Then
        assertThrows(WrongCodeException.class, () -> authService.confirmResetCode(dto));
    }

    @Test
    void confirmResetCode_shouldThrowExceptionIfResetCodeExpired() {
        // Given
        EmailConfirmCodeDto dto = new EmailConfirmCodeDto();
        dto.setVerificationCode(1);
        dto.setEmail("email");
        EmailVerificationCode code = new EmailVerificationCode();
        code.setResetCode(1);
        code.setRequestTime(Instant.now()
                .minus(CODE_EXPIRATION_TIME_IN_MIN * 2, ChronoUnit.MINUTES));
        when(emailVerificationRepository.findByEmailAndIsEnable("email", true))
                .thenReturn(code);

        // Then
        assertThrows(ExpiredCodeException.class, () -> authService.confirmResetCode(dto));
    }

    @Test
    void confirmResetCode_shouldThrowExceptionIfUserNotFound() {
        // Given
        EmailConfirmCodeDto dto = new EmailConfirmCodeDto();
        dto.setVerificationCode(1);
        dto.setEmail("email");
        EmailVerificationCode code = new EmailVerificationCode();
        code.setResetCode(1);
        code.setRequestTime(Instant.now()
                .plus(CODE_EXPIRATION_TIME_IN_MIN * 2, ChronoUnit.MINUTES));
        when(emailVerificationRepository.findByEmailAndIsEnable("email", true))
                .thenReturn(code);

        // Then
        assertThrows(UserNotFoundException.class, () -> authService.confirmResetCode(dto));
    }

    @Test
    void confirmResetCode_shouldWork() {
        // Given
        EmailConfirmCodeDto dto = new EmailConfirmCodeDto();
        dto.setVerificationCode(1);
        dto.setEmail("email");
        EmailVerificationCode code = new EmailVerificationCode();
        code.setResetCode(1);
        code.setRequestTime(Instant.now()
                .plus(CODE_EXPIRATION_TIME_IN_MIN * 2, ChronoUnit.MINUTES));
        User user = new User();
        when(emailVerificationRepository.findByEmailAndIsEnable("email", true))
                .thenReturn(code);
        when(userRepository.findUserByUserEmail("email")).thenReturn(Optional.of(user));

        // When
        TokensDTO tokensDTO = authService.confirmResetCode(dto);

        // Then
        verify(emailVerificationRepository, atLeastOnce()).save(any());
        assertNotNull(tokensDTO);
    }

    @Test
    void confirmResetCode_shouldThrowMakeTokenException() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Given
        EmailConfirmCodeDto dto = new EmailConfirmCodeDto();
        dto.setVerificationCode(1);
        dto.setEmail("email");
        EmailVerificationCode code = new EmailVerificationCode();
        code.setResetCode(1);
        code.setRequestTime(Instant.now()
                .plus(CODE_EXPIRATION_TIME_IN_MIN * 2, ChronoUnit.MINUTES));
        User user = new User();
        when(emailVerificationRepository.findByEmailAndIsEnable("email", true))
                .thenReturn(code);
        when(userRepository.findUserByUserEmail("email")).thenReturn(Optional.of(user));
        when(jwtProvider.generateRefreshToken(any())).thenThrow(IOException.class);

        // Then
        assertThrows(MakeTokenException.class, () -> authService.confirmResetCode(dto));
    }

    @Test
    void getCodeForLoginWithWhatsApp_shouldNotSaveUserIfUserNotFound() {
        // Given
        PhoneNumberDto dto = new PhoneNumberDto("+88005553535");
        when(userRepository.findUserByPhoneNumber("+88005553535")).thenReturn(null);
        when(whatsAppClient.sendVerificationCode(any(), any(), any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // When
        authService.getCodeForLoginWithWhatsApp(dto);

        // Then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getCodeForLoginWithWhatsApp_shouldSaveUserIfUserIsFound() {
        // Given
        PhoneNumberDto dto = new PhoneNumberDto("+88005553535");
        User user = new User();
        when(userRepository.findUserByPhoneNumber("+88005553535")).thenReturn(user);
        when(whatsAppClient.sendVerificationCode(any(), any(), any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // When
        authService.getCodeForLoginWithWhatsApp(dto);

        // Then
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    void getCodeForLoginWithWhatsApp_shouldWorkIfPhoneVerificationCodeIsFound() {
        // Given
        PhoneNumberDto dto = new PhoneNumberDto("+88005553535");
        User user = new User();
        when(userRepository.findUserByPhoneNumber("+88005553535")).thenReturn(user);
        when(whatsAppClient.sendVerificationCode(any(), any(), any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(phoneVerificationRepository.findByPhoneNumberAndIsEnableIsTrue("+88005553535"))
                .thenReturn(new PhoneVerificationCode());

        // Then
        assertDoesNotThrow(() -> authService.getCodeForLoginWithWhatsApp(dto));
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    void getCodeForLoginWithWhatsApp_shouldWorkIfPhoneVerificationCodeIsNotFound() {
        // Given
        PhoneNumberDto dto = new PhoneNumberDto("+88005553535");
        User user = new User();
        when(userRepository.findUserByPhoneNumber("+88005553535")).thenReturn(user);
        when(whatsAppClient.sendVerificationCode(any(), any(), any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(phoneVerificationRepository.findByPhoneNumberAndIsEnableIsTrue("+88005553535"))
                .thenReturn(null);

        // Then
        assertDoesNotThrow(() -> authService.getCodeForLoginWithWhatsApp(dto));
        verify(userRepository, atLeastOnce()).save(any(User.class));
    }

    @Test
    void getCodeForLoginWithWhatsApp_shouldWorkIfRegistrantIsNotFound() {
        // Given
        PhoneNumberDto dto = new PhoneNumberDto("+88005553535");
        when(userRepository.findUserByPhoneNumber("+88005553535")).thenReturn(null);
        when(whatsAppClient.sendVerificationCode(any(), any(), any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // Then
        assertDoesNotThrow(() -> authService.getCodeForLoginWithWhatsApp(dto));
        verify(registrantRepository, atLeastOnce()).save(any(Registrant.class));
    }

    @Test
    void getCodeForLoginWithWhatsApp_shouldWorkIfRegistrantFound() {
        // Given
        PhoneNumberDto dto = new PhoneNumberDto("+88005553535");
        Registrant registrant = new Registrant();
        when(userRepository.findUserByPhoneNumber("+88005553535")).thenReturn(null);
        when(whatsAppClient.sendVerificationCode(any(), any(), any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(registrantRepository.findByPhoneNumberAndStatus("+88005553535", RegistrantStatus.AWAITED))
                .thenReturn(registrant);

        // Then
        assertDoesNotThrow(() -> authService.getCodeForLoginWithWhatsApp(dto));
        verify(registrantRepository, atLeastOnce()).save(registrant);
    }

    @Test
    void confirmCodeForLoginWithWhatsApp_shouldThrowExceptionIfUserNotFoundAndRegistrantNotFound() {
        // Given
        PhoneConfirmCodeDto dto = new PhoneConfirmCodeDto();
        dto.setPhoneNumber("2-12-85-06");
        when(userRepository.findUserByPhoneNumber("2-12-85-06")).thenReturn(null);

        // Then
        assertThrows(RegistrantNotFoundException.class, () -> authService
                .confirmCodeForLoginWithWhatsApp(dto));
    }

    @Test
    void confirmCodeForLoginWithWhatsApp_shouldThrowExceptionIfUserNotFoundAndWrongCode() {
        // Given
        PhoneConfirmCodeDto dto = new PhoneConfirmCodeDto();
        dto.setPhoneNumber("2-12-85-06");
        dto.setVerificationCode(1);
        Registrant registrant = new Registrant();
        registrant.setVerificationCode(2);
        when(userRepository.findUserByPhoneNumber("2-12-85-06")).thenReturn(null);
        when(registrantRepository.findByPhoneNumberAndStatus("2-12-85-06",
                RegistrantStatus.AWAITED)).thenReturn(registrant);

        // Then
        assertThrows(WrongCodeException.class, () -> authService
                .confirmCodeForLoginWithWhatsApp(dto));
    }

    @Test
    void confirmCodeForLoginWithWhatsApp_shouldThrowExceptionIfUserNotFoundAndExpiredCode() {
        // Given
        PhoneConfirmCodeDto dto = new PhoneConfirmCodeDto();
        dto.setPhoneNumber("2-12-85-06");
        dto.setVerificationCode(1);
        Registrant registrant = new Registrant();
        registrant.setVerificationCode(1);
        registrant.setCreatedAt(OffsetDateTime.now().minusMinutes(CODE_EXPIRATION_TIME_IN_MIN * 2));
        when(userRepository.findUserByPhoneNumber("2-12-85-06")).thenReturn(null);
        when(registrantRepository.findByPhoneNumberAndStatus("2-12-85-06",
                RegistrantStatus.AWAITED)).thenReturn(registrant);

        // Then
        assertThrows(ExpiredCodeException.class, () -> authService
                .confirmCodeForLoginWithWhatsApp(dto));
    }

    @Test
    void confirmCodeForLoginWithWhatsApp_shouldSaveRegistrantIfUserNotFound() {
        // Given
        PhoneConfirmCodeDto dto = new PhoneConfirmCodeDto();
        dto.setPhoneNumber("2-12-85-06");
        dto.setVerificationCode(1);
        Registrant registrant = new Registrant();
        registrant.setVerificationCode(1);
        registrant.setCreatedAt(OffsetDateTime.now().minusMinutes(CODE_EXPIRATION_TIME_IN_MIN * 2));
        when(userRepository.findUserByPhoneNumber("2-12-85-06")).thenReturn(null);
        when(registrantRepository.findByPhoneNumberAndStatus("2-12-85-06",
                RegistrantStatus.AWAITED)).thenReturn(registrant);

        // Then
        assertThrows(ExpiredCodeException.class, () -> authService
                .confirmCodeForLoginWithWhatsApp(dto));
        verify(registrantRepository, times(1)).save(registrant);
    }

    @Test
    void confirmCodeForLoginWithWhatsApp_shouldSaveUserAndRegistrantIfUserNotFound() {
        // Given
        PhoneConfirmCodeDto dto = new PhoneConfirmCodeDto();
        dto.setPhoneNumber("2-12-85-06");
        dto.setVerificationCode(1);
        Registrant registrant = new Registrant();
        registrant.setVerificationCode(1);
        registrant.setCreatedAt(OffsetDateTime.now());
        when(userRepository.findUserByPhoneNumber("2-12-85-06")).thenReturn(null);
        when(registrantRepository.findByPhoneNumberAndStatus("2-12-85-06",
                RegistrantStatus.AWAITED)).thenReturn(registrant);

        // When
        authService.confirmCodeForLoginWithWhatsApp(dto);

        // Then
        verify(userRepository, atLeastOnce()).save(any(User.class));
        verify(registrantRepository, atLeastOnce()).save(registrant);
    }

    @Test
    void confirmCodeForLoginWithWhatsApp_shouldThrowExceptionIfUserIsFoundAndCodeNotFound() {
        // Given
        PhoneConfirmCodeDto dto = new PhoneConfirmCodeDto();
        dto.setPhoneNumber("2-12-85-06");
        User user = new User();
        user.setPhoneNumber("2-12-85-06");
        when(userRepository.findUserByPhoneNumber("2-12-85-06")).thenReturn(user);

        // Then
        assertThrows(WrongCodeException.class, () -> authService.confirmCodeForLoginWithWhatsApp(dto));
    }

    @Test
    void confirmCodeForLoginWithWhatsApp_shouldThrowExceptionIfUserIsFoundAndCodeIsWrong() {
        // Given
        PhoneConfirmCodeDto dto = new PhoneConfirmCodeDto();
        dto.setPhoneNumber("2-12-85-06");
        dto.setVerificationCode(1);
        User user = new User();
        user.setPhoneNumber("2-12-85-06");
        PhoneVerificationCode code = new PhoneVerificationCode();
        code.setVerificationCode(2);
        when(userRepository.findUserByPhoneNumber("2-12-85-06")).thenReturn(user);
        when(phoneVerificationRepository.findByPhoneNumberAndIsEnableIsTrue("2-12-85-06"))
                .thenReturn(code);

        // Then
        assertThrows(WrongCodeException.class, () -> authService.confirmCodeForLoginWithWhatsApp(dto));
    }

    @Test
    void confirmCodeForLoginWithWhatsApp_shouldThrowExceptionIfUserIsFoundAndCodeIsExpired() {
        // Given
        PhoneConfirmCodeDto dto = new PhoneConfirmCodeDto();
        dto.setPhoneNumber("2-12-85-06");
        dto.setVerificationCode(1);
        User user = new User();
        user.setPhoneNumber("2-12-85-06");
        PhoneVerificationCode code = new PhoneVerificationCode();
        code.setVerificationCode(1);
        code.setRequestTime(OffsetDateTime.now().minusMinutes(CODE_EXPIRATION_TIME_IN_MIN * 2));
        when(userRepository.findUserByPhoneNumber("2-12-85-06")).thenReturn(user);
        when(phoneVerificationRepository.findByPhoneNumberAndIsEnableIsTrue("2-12-85-06"))
                .thenReturn(code);

        // Then
        assertThrows(ExpiredCodeException.class, () -> authService.confirmCodeForLoginWithWhatsApp(dto));
    }

    @Test
    void confirmCodeForLoginWithWhatsApp_shouldSaveCodeIfUserIsFound() {
        // Given
        PhoneConfirmCodeDto dto = new PhoneConfirmCodeDto();
        dto.setPhoneNumber("2-12-85-06");
        dto.setVerificationCode(1);
        User user = new User();
        user.setPhoneNumber("2-12-85-06");
        PhoneVerificationCode code = new PhoneVerificationCode();
        code.setVerificationCode(1);
        code.setRequestTime(OffsetDateTime.now());
        when(userRepository.findUserByPhoneNumber("2-12-85-06")).thenReturn(user);
        when(phoneVerificationRepository.findByPhoneNumberAndIsEnableIsTrue("2-12-85-06"))
                .thenReturn(code);

        // Then
        assertDoesNotThrow(() -> authService.confirmCodeForLoginWithWhatsApp(dto));
        verify(phoneVerificationRepository, atLeastOnce()).save(code);
    }

    @Test
    void confirmCodeForLoginWithWhatsApp_shouldThrowMakeTokenExceptionUserNotFound() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Given
        PhoneConfirmCodeDto dto = new PhoneConfirmCodeDto();
        dto.setPhoneNumber("2-12-85-06");
        dto.setVerificationCode(1);
        User user = new User();
        user.setPhoneNumber("2-12-85-06");
        PhoneVerificationCode code = new PhoneVerificationCode();
        code.setVerificationCode(1);
        code.setRequestTime(OffsetDateTime.now());
        Registrant registrant = new Registrant();
        registrant.setVerificationCode(1);
        registrant.setCreatedAt(OffsetDateTime.now());
        when(userRepository.findUserByPhoneNumber("2-12-85-06")).thenReturn(null);
        when(registrantRepository.findByPhoneNumberAndStatus("2-12-85-06",
                RegistrantStatus.AWAITED)).thenReturn(registrant);
        when(jwtProvider.generateRefreshToken(any())).thenThrow(IOException.class);

        // Then
        assertThrows(MakeTokenException.class, () -> authService.confirmCodeForLoginWithWhatsApp(dto));
    }

    @Test
    void confirmCodeForLoginWithWhatsApp_shouldThrowMakeTokenExceptionUserFound() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Given
        PhoneConfirmCodeDto dto = new PhoneConfirmCodeDto();
        dto.setPhoneNumber("2-12-85-06");
        dto.setVerificationCode(1);
        User user = new User();
        user.setPhoneNumber("2-12-85-06");
        PhoneVerificationCode code = new PhoneVerificationCode();
        code.setVerificationCode(1);
        code.setRequestTime(OffsetDateTime.now());
        Registrant registrant = new Registrant();
        registrant.setVerificationCode(1);
        registrant.setCreatedAt(OffsetDateTime.now());
        when(userRepository.findUserByPhoneNumber("2-12-85-06")).thenReturn(user);
        when(phoneVerificationRepository.findByPhoneNumberAndIsEnableIsTrue("2-12-85-06")).thenReturn(code);
        when(jwtProvider.generateRefreshToken(any())).thenThrow(IOException.class);

        // Then
        assertThrows(MakeTokenException.class, () -> authService.confirmCodeForLoginWithWhatsApp(dto));
    }


    @Test
    void registerNewUser_shouldThrowExceptionIfUserAlreadyExists() {
        // Given
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();
        userForSignInUpDTO.setUsername("user");
        String socialId = "socialId";
        SocialIdType socialIdTypeGoogle = SocialIdType.GOOGLE;
        when(userRepository.existsUserByUsername("user")).thenReturn(true);

        // Then
        assertThrows(UserAlreadyExistException.class, () -> authService
                .registerNewUser(userForSignInUpDTO, socialId, socialIdTypeGoogle));
    }

    @Test
    void registerNewUser_shouldMapUserForGoogle() {
        // Given
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();
        userForSignInUpDTO.setUsername("user");
        String socialId = "socialId";
        SocialIdType socialIdTypeGoogle = SocialIdType.GOOGLE;
        User user = new User();
        when(userRepository.existsUserByUsername("user")).thenReturn(false);
        when(userMapper.mapToUserFromUserGoogleRegistration(userForSignInUpDTO, socialId)).thenReturn(user);

        // When
        authService.registerNewUser(userForSignInUpDTO, socialId, socialIdTypeGoogle);

        // Then
        verify(userMapper, atLeastOnce()).mapToUserFromUserGoogleRegistration(userForSignInUpDTO, socialId);
    }

    @Test
    void registerNewUser_shouldMapUserForVK() {
        // Given
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();
        userForSignInUpDTO.setUsername("user");
        String socialId = "socialId";
        SocialIdType socialIdTypeVK = SocialIdType.VK;
        User user = new User();
        when(userRepository.existsUserByUsername("user")).thenReturn(false);
        when(userMapper.mapToUserFromUserVkRegistration(userForSignInUpDTO, socialId)).thenReturn(user);

        // When
        authService.registerNewUser(userForSignInUpDTO, socialId, socialIdTypeVK);

        // Then
        verify(userMapper, atLeastOnce()).mapToUserFromUserVkRegistration(userForSignInUpDTO, socialId);
    }

    @Test
    void registerNewUser_shouldThrowExceptionWhenUnknownSocialIdType() {
        // Given
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();
        userForSignInUpDTO.setUsername("user");
        String socialId = "socialId";
        SocialIdType socialIdTypeNull = null;
        when(userRepository.existsUserByUsername("user")).thenReturn(false);

        // Then
        assertThrows(IllegalArgumentException.class, () -> authService
                .registerNewUser(userForSignInUpDTO, socialId, socialIdTypeNull));
    }

    @Test
    void registerNewUser_shouldWorkIfUnknownUserIsFound() {
        // Given
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();
        userForSignInUpDTO.setUsername("user");
        userForSignInUpDTO.setFirebaseId("firebaseId");
        String socialId = "socialId";
        SocialIdType socialIdTypeGoogle = SocialIdType.GOOGLE;
        User user = new User();
        UnknownUser unknownUser = new UnknownUser();
        when(userRepository.existsUserByUsername("user")).thenReturn(false);
        when(userMapper.mapToUserFromUserGoogleRegistration(userForSignInUpDTO, socialId)).thenReturn(user);
        when(unknownUserRepository.findUnknownUserByFirebaseId("firebaseId")).thenReturn(unknownUser);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User userToSave = authService.registerNewUser(userForSignInUpDTO, socialId, socialIdTypeGoogle);

        // Then
        assertNotNull(userToSave);
    }

    @Test
    void registerNewUser_shouldWorkIfUnknownUserIsNotFound() {
        // Given
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();
        userForSignInUpDTO.setUsername("user");
        userForSignInUpDTO.setFirebaseId("firebaseId");
        String socialId = "socialId";
        SocialIdType socialIdTypeGoogle = SocialIdType.GOOGLE;
        User user = new User();
        when(userRepository.existsUserByUsername("user")).thenReturn(false);
        when(userMapper.mapToUserFromUserGoogleRegistration(userForSignInUpDTO, socialId)).thenReturn(user);
        when(unknownUserRepository.findUnknownUserByFirebaseId("firebaseId")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User userToSave = authService.registerNewUser(userForSignInUpDTO, socialId, socialIdTypeGoogle);

        // Then
        assertNotNull(userToSave);
    }

    @Test
    void getTokensForGoogleUser_shouldThrowGoogleIntrospectionException()
            throws GeneralSecurityException, IOException {
        // Given
        String token = "123";
        String tokenType = "google";
        UserForSignInUpDTO dto = new UserForSignInUpDTO();
        when(googleIdTokenVerifier.verify("123")).thenReturn(null);

        // Then
        assertThrows(GoogleIntrospectionException.class, () -> authService
                .getTokensToUserFromSocialNetworks(token, tokenType, dto));
    }

    @Test
    void getTokensForGoogleUser_shouldThrowGoogleIntrospectionExceptionIfSecurityException()
            throws GeneralSecurityException, IOException {
        // Given
        String token = "123";
        String tokenType = "google";
        UserForSignInUpDTO dto = new UserForSignInUpDTO();
        when(googleIdTokenVerifier.verify("123"))
                .thenThrow(GeneralSecurityException.class);

        // Then
        assertThrows(GoogleIntrospectionException.class, () -> authService
                .getTokensToUserFromSocialNetworks(token, tokenType, dto));
    }

    @Test
    void getTokensForGoogleUser_shouldThrowJwtDecoderException()
            throws GeneralSecurityException, IOException {
        // Given
        String token = "123";
        String tokenType = "google";
        UserForSignInUpDTO dto = new UserForSignInUpDTO();
        when(googleIdTokenVerifier.verify("123"))
                .thenThrow(IOException.class);

        // Then
        assertThrows(JwtDecoderException.class, () -> authService
                .getTokensToUserFromSocialNetworks(token, tokenType, dto));
    }

    @Test
    void getTokensForVkUser_shouldThrowVkIntrospectionException() {
        // Given
        String tokenType = "vk";
        String token = "123";
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();
        User user = new User();
        String response =  "{ \"error\" : {" +
                "\"error_code\" : \"1200\"," +
                "\"error_msg\" : \"1200\"," +
                "\"request_params\" : [{ \"1\" : \"1\" }]" +
                "} }";

        when(vkClient.isTokenValid(any(VkValidationParams.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.FORBIDDEN).body(response));
        when(userMapper.mapToUserFromUserVkRegistration(any(), anyString())).thenReturn(user);

        lenient().when(authService.registerNewUser(userForSignInUpDTO, anyString(), SocialIdType.VK))
                .thenReturn(user);

        // Then
        assertThrows(VkIntrospectionException.class,
                () -> authService.getTokensToUserFromSocialNetworks(token, tokenType, userForSignInUpDTO));

    }

    @Test
    void getTokensDTO_shouldThrowJwtDecoderException() throws GeneralSecurityException, IOException {
        // Given
        String token = "123";
        String tokenType = "google";
        UserForSignInUpDTO dto = new UserForSignInUpDTO();

        User user = new User();
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setSubject("123");

        when(userRepository.findByGoogleId(anyString())).thenReturn(user);
        when(jwtProvider.generateAccessToken(any(), any())).thenThrow(NoSuchAlgorithmException.class);
        when(userMapper.mapToUserFromUserGoogleRegistration(any(), anyString())).thenReturn(user);

        lenient().when(authService.registerNewUser(dto, anyString(), SocialIdType.GOOGLE))
                .thenReturn(user);
        when(googleIdTokenVerifier.verify(anyString())).thenReturn(new GoogleIdToken(
                new JsonWebSignature.Header(),
                payload,
                new byte[] {0},
                new byte[] {0}
        ));

        assertThrows(JwtDecoderException.class, () -> authService
                .getTokensToUserFromSocialNetworks(token, tokenType, dto));
    }

    @Test
    void saveUser_shouldThrowExceptionIfUsernameExists() {
        // Given
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();
        userForSignInUpDTO.setUsername("user");
        userForSignInUpDTO.setFirebaseId("firebaseId");
        String socialId = "socialId";
        SocialIdType socialIdTypeGoogle = SocialIdType.GOOGLE;
        User user = new User();
        when(userRepository.existsUserByUsername("user")).thenReturn(false);
        user.setUsername("userToSave");
        when(userRepository.existsUserByUsername("userToSave")).thenReturn(true);
        when(userMapper.mapToUserFromUserGoogleRegistration(userForSignInUpDTO, socialId)).thenReturn(user);
        when(unknownUserRepository.findUnknownUserByFirebaseId("firebaseId")).thenReturn(null);

        // Then
        assertThrows(UserAlreadyExistException.class,
                () -> authService.registerNewUser(userForSignInUpDTO, socialId, socialIdTypeGoogle));

    }

    @Test
    void saveUser_shouldThrowExceptionIfEmailExists() {
        // Given
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();
        userForSignInUpDTO.setUsername("user");
        userForSignInUpDTO.setFirebaseId("firebaseId");
        String socialId = "socialId";
        SocialIdType socialIdTypeGoogle = SocialIdType.GOOGLE;
        User user = new User();
        user.setUserEmail("email");
        when(userRepository.existsUserByUsername("user")).thenReturn(false);
        when(userRepository.existsUserByUserEmail("email")).thenReturn(true);
        when(userMapper.mapToUserFromUserGoogleRegistration(userForSignInUpDTO, socialId)).thenReturn(user);
        when(unknownUserRepository.findUnknownUserByFirebaseId("firebaseId")).thenReturn(null);

        // Then
        assertThrows(UserAlreadyExistException.class,
                () -> authService.registerNewUser(userForSignInUpDTO, socialId, socialIdTypeGoogle));

    }

    @Test
    void saveUser_shouldThrowExceptionIfPhoneNumberExists() {
        // Given
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();
        userForSignInUpDTO.setUsername("user");
        userForSignInUpDTO.setFirebaseId("firebaseId");
        String socialId = "socialId";
        SocialIdType socialIdTypeGoogle = SocialIdType.GOOGLE;
        User user = new User();
        user.setPhoneNumber("8-800");
        when(userRepository.existsUserByUsername("user")).thenReturn(false);
        when(userRepository.existsUserByPhoneNumber("8-800")).thenReturn(true);
        when(userMapper.mapToUserFromUserGoogleRegistration(userForSignInUpDTO, socialId)).thenReturn(user);
        when(unknownUserRepository.findUnknownUserByFirebaseId("firebaseId")).thenReturn(null);

        // Then
        assertThrows(UserAlreadyExistException.class,
                () -> authService.registerNewUser(userForSignInUpDTO, socialId, socialIdTypeGoogle));

    }




    private Claims generateClaims() {
        return new Claims() {
            @Override
            public String getIssuer() {
                return null;
            }

            @Override
            public Claims setIssuer(String s) {
                return null;
            }

            @Override
            public String getSubject() {
                return "1";
            }

            @Override
            public Claims setSubject(String s) {
                return null;
            }

            @Override
            public String getAudience() {
                return null;
            }

            @Override
            public Claims setAudience(String s) {
                return null;
            }

            @Override
            public Date getExpiration() {
                return null;
            }

            @Override
            public Claims setExpiration(Date date) {
                return null;
            }

            @Override
            public Date getNotBefore() {
                return null;
            }

            @Override
            public Claims setNotBefore(Date date) {
                return null;
            }

            @Override
            public Date getIssuedAt() {
                return null;
            }

            @Override
            public Claims setIssuedAt(Date date) {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public Claims setId(String s) {
                return null;
            }

            @Override
            public <T> T get(String s, Class<T> aClass) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public Object get(Object key) {
                return null;
            }

            @Nullable
            @Override
            public Object put(String key, Object value) {
                return null;
            }

            @Override
            public Object remove(Object key) {
                return null;
            }

            @Override
            public void putAll(@NotNull Map<? extends String, ?> m) {

            }

            @Override
            public void clear() {

            }

            @NotNull
            @Override
            public Set<String> keySet() {
                return null;
            }

            @NotNull
            @Override
            public Collection<Object> values() {
                return null;
            }

            @NotNull
            @Override
            public Set<Entry<String, Object>> entrySet() {
                return null;
            }
        };
    }
}