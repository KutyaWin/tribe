package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.vk.VkClient;
import com.covenant.tribe.client.whatsapp.WhatsAppClient;
import com.covenant.tribe.domain.auth.SocialIdType;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.*;
import com.covenant.tribe.dto.auth.ConfirmRegistrationDTO;
import com.covenant.tribe.dto.auth.RegistrantRequestDTO;
import com.covenant.tribe.dto.auth.RegistrantResponseDTO;
import com.covenant.tribe.dto.auth.TokensDTO;
import com.covenant.tribe.dto.user.UserForSignInUpDTO;
import com.covenant.tribe.exeption.auth.ExpiredCodeException;
import com.covenant.tribe.exeption.auth.JwtDecoderException;
import com.covenant.tribe.exeption.auth.UnexpectedTokenTypeException;
import com.covenant.tribe.exeption.auth.WrongCodeException;
import com.covenant.tribe.exeption.user.UserAlreadyExistException;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.repository.*;
import com.covenant.tribe.security.JwtProvider;
import com.covenant.tribe.service.MailService;
import com.covenant.tribe.service.VerificationCodeService;
import com.covenant.tribe.util.mapper.RegistrantMapper;
import com.covenant.tribe.util.mapper.UserMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.webtoken.JsonWebSignature;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.OffsetDateTime;
import java.util.*;

import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

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
        assertThrows(UnexpectedTokenTypeException.class, () -> {
            TokensDTO dto = authService.getTokensToUserFromSocialNetworks(token, tokenType, userForSignInUpDTO);
        });
    }



    @Test
    void getTokensToUserFromSocialNetworks_shouldReturnTokenWhenTokenTypeIsGoogleType()
            throws IOException, GeneralSecurityException {
        // Given
        String tokenType = "google";
        String token = "123";
        UserForSignInUpDTO userForSignInUpDTO = new UserForSignInUpDTO();

//        doReturn(new GoogleIdToken(
//                new JsonWebSignature.Header(),
//                new GoogleIdToken.Payload(),
//                new byte[] {0},
//                new byte[]{0}
//        )).when(googleIdTokenVerifier).verify(anyString());

        when(userMapper.mapToUserFromUserGoogleRegistration(any(), anyString())).thenReturn(new User());
        when(userRepository.save(any())).thenReturn(new User());
        when(authService.registerNewUser(new UserForSignInUpDTO(), anyString(), SocialIdType.GOOGLE))
                .thenReturn(new User());
        when(googleIdTokenVerifier.verify(anyString())).thenReturn(new GoogleIdToken(
                new JsonWebSignature.Header(),
                new GoogleIdToken.Payload(),
                new byte[] {0},
                new byte[] {0}
        ));


        // Wnen
        TokensDTO dto = authService.getTokensToUserFromSocialNetworks(token, tokenType, userForSignInUpDTO);


        // Then
        assertNotNull(dto);


    }

    Claims generateClaims() {
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
        assertThrows(UserNotFoundException.class, () -> {
            authService.refreshTokens(token);
        });
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
        assertThrows(JwtDecoderException.class, () -> {
            authService.refreshTokens(token);
        });

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
        assertNotNull(dto);
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
        assertNotNull(dto);

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
    void loginUserWithEmail() {
    }

    @Test
    void sendResetCodeToEmail() {
    }

    @Test
    void changePassword() {
    }

    @Test
    void confirmResetCode() {
    }

    @Test
    void getCodeForLoginWithWhatsApp() {
    }

    @Test
    void confirmCodeForLoginWithWhatsApp() {
    }

    @Test
    void getTokenForVkUser() {
    }

    @Test
    void registerNewUser() {
    }
}