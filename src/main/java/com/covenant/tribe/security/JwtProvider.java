package com.covenant.tribe.security;

import com.covenant.tribe.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Component
public class JwtProvider {

    private AuthenticationManager authenticationManager;

    private KeysReader keysReader;

    @Value("${keys.access-private}")
    private String accessPrivateKeyPath;

    @Value("${keys.access-public}")
    private String accessPublicKeyPath;

    @Value("${keys.refresh-public}")
    private String refreshPublicKeyPath;

    @Value("${keys.refresh-private}")
    private String refreshPrivateKeyPath;

    private final Integer ACCESS_TOKEN_EXPIRATION_MINS = 300;
    private final Integer REFRESH_TOKEN_EXPIRATION_DAYS = 365;

    @Autowired
    public JwtProvider(KeysReader keysReader) {
        this.keysReader = keysReader;
    }

    public String generateAccessToken(@NonNull Long userId) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PrivateKey privateKey = keysReader.getPrivateKey(accessPrivateKeyPath);
        SignatureAlgorithm algorithm = SignatureAlgorithm.RS256;
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now
                .plusMinutes(ACCESS_TOKEN_EXPIRATION_MINS)
                .atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(accessExpiration)
                .signWith(privateKey, algorithm)
                .compact();
    }

    public String generateRefreshToken(@NonNull Long userId) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        SignatureAlgorithm algorithm = SignatureAlgorithm.RS256;
        PrivateKey privateKey = keysReader.getPrivateKey(refreshPrivateKeyPath);
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now
                .plusDays(REFRESH_TOKEN_EXPIRATION_DAYS)
                .atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(refreshExpiration)
                .signWith(privateKey, algorithm)
                .compact();
    }

    public Claims getAccessTokenClaims(@NonNull String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPublicKey publicKey = keysReader.getPublicKey(accessPublicKeyPath);
        String tokenWithoutBearerStr = token.substring(7);
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(tokenWithoutBearerStr)
                .getBody();
    }

    public Claims getRefreshTokenClaims(@NonNull String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String tokenWithoutBearerStr = token.substring(7);
        RSAPublicKey publicKey = keysReader.getPublicKey(refreshPublicKeyPath);
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(tokenWithoutBearerStr)
                .getBody();
    }

    public Long getUserIdFromToken(@NonNull String token) {
        Claims claims = null;
        try {
            claims = getAccessTokenClaims(token);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        return Long.parseLong(claims.getSubject());
    }

}
