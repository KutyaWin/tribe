package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.vk.VkClient;
import com.covenant.tribe.client.whatsapp.WhatsAppClient;
import com.covenant.tribe.domain.auth.SocialIdType;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.domain.user.UserRole;
import com.covenant.tribe.dto.auth.TokensDTO;
import com.covenant.tribe.dto.user.UserForSignInUpDTO;
import com.covenant.tribe.exeption.auth.UnexpectedTokenTypeException;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

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
        when(authService.registerNewUser(new UserForSignInUpDTO(), anyString(), SocialIdType.GOOGLE)).thenReturn(0L);
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

    @Test
    void refreshTokens() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
                "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        User user = new User();
        user.setId(1L);
        user.setUserRole(UserRole.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(jwtProvider.getRefreshTokenClaims(anyString())).thenReturn(
                Jwts.parserBuilder()
                .setSigningKey(secretKeyFor(SignatureAlgorithm.HS256))
                .build()
                .parseClaimsJws(token)
                .getBody()
        );
        when(jwtProvider.getRefreshTokenClaims(anyString()).getSubject()).thenReturn("1");

        // When
        TokensDTO dto = authService.refreshTokens(token);

        // Then
        assertNotNull(dto);
    }

    @Test
    void addRegistrantWithEmail() {
    }

    @Test
    void confirmEmailRegistration() {
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