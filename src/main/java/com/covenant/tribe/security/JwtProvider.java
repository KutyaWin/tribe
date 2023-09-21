package com.covenant.tribe.security;

import com.covenant.tribe.domain.user.UserRole;
import com.covenant.tribe.exeption.auth.JwtDecoderException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class JwtProvider {

    AuthenticationManager authenticationManager;

    KeysReader keysReader;

    @Value("${keys.access-private}")
    String accessPrivateKeyPath;

    @Value("${keys.access-public}")
    String accessPublicKeyPath;

    @Value("${keys.refresh-public}")
    String refreshPublicKeyPath;

    @Value("${keys.refresh-private}")
    String refreshPrivateKeyPath;

    PrivateKey accessPrivateKey;
    PrivateKey refreshPrivateKey;
    RSAPublicKey accessPublicKey;
    RSAPublicKey refreshPublicKey;

    private final Integer ACCESS_TOKEN_EXPIRATION_MINS = 300;
    private final Integer REFRESH_TOKEN_EXPIRATION_DAYS = 365;

    @Autowired
    public JwtProvider(KeysReader keysReader) {
        this.keysReader = keysReader;
    }

    @PostConstruct
    public void readKeys() {
        try {
            this.accessPrivateKey = keysReader.getPrivateKey(accessPrivateKeyPath);
            this.refreshPrivateKey = keysReader.getPrivateKey(refreshPrivateKeyPath);
            this.accessPublicKey = keysReader.getPublicKey(accessPublicKeyPath);
            this.refreshPublicKey = keysReader.getPublicKey(refreshPublicKeyPath);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JwtDecoderException(e.getMessage());
        }
    }

    public String generateAccessToken(@NonNull Long userId, UserRole userRole) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        SignatureAlgorithm algorithm = SignatureAlgorithm.RS256;
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now
                .plusMinutes(ACCESS_TOKEN_EXPIRATION_MINS)
                .atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        String role = userRole.toString();
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(accessExpiration)
                .signWith(this.accessPrivateKey, algorithm)
                .addClaims(claims)
                .compact();
    }

    public String generateRefreshToken(@NonNull Long userId) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        SignatureAlgorithm algorithm = SignatureAlgorithm.RS256;
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now
                .plusDays(REFRESH_TOKEN_EXPIRATION_DAYS)
                .atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(refreshExpiration)
                .signWith(this.refreshPrivateKey, algorithm)
                .compact();
    }

    public Claims getAccessTokenClaims(@NonNull String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String tokenWithoutBearerStr = token.substring(7);
        return Jwts.parserBuilder()
                .setSigningKey(this.accessPublicKey)
                .build()
                .parseClaimsJws(tokenWithoutBearerStr)
                .getBody();
    }

    public Claims getRefreshTokenClaims(@NonNull String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String tokenWithoutBearerStr = token.substring(7);
        return Jwts.parserBuilder()
                .setSigningKey(this.refreshPublicKey)
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
