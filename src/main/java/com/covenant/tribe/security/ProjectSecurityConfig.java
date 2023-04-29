package com.covenant.tribe.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

@Configuration
@EnableWebSecurity
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProjectSecurityConfig {

    private KeysReader keysReader;
    @Value("${keys.access-public}")
    private String accessPublicKeyPath;
    @Value("${keys.refresh-public}")
    private String refreshPublicKeyPath;

    @Autowired
    public ProjectSecurityConfig(KeysReader keysReader) {
        this.keysReader = keysReader;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(
                j -> j.authenticationManagerResolver(
                        authenticationManagerResolver(accessJwtDecoder(), refreshJwtDecoder())
                )
        );
        http.csrf().disable();


        http.authorizeHttpRequests()
                .requestMatchers("api/v1/auth/social-login").permitAll()
                .requestMatchers("api/v1/auth/registration/email/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/events/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/event/type").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/tags/**").permitAll()
                .requestMatchers(HttpMethod.POST, "api/v1/unknown-user/interests").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/user/email/check/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/user/username/check/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/user/email/check/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated();
        return http.build();
    }

    @Bean
    public JwtDecoder accessJwtDecoder() {
        RSAPublicKey publicKey = null;
        try {
            publicKey = keysReader.getPublicKey(accessPublicKeyPath);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new NullPointerException(e.getMessage());
        }
        return NimbusJwtDecoder
                .withPublicKey(publicKey)
                .build();
    }

    @Bean
    public JwtDecoder refreshJwtDecoder() {
        RSAPublicKey publicKey = null;
        try {
            publicKey = keysReader.getPublicKey(refreshPublicKeyPath);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new NullPointerException(e.getMessage());
        }
        return NimbusJwtDecoder
                .withPublicKey(publicKey)
                .build();
    }

    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver(
            JwtDecoder accessJwtDecoder, JwtDecoder refreshJwtDecoder
    ) {
        AuthenticationManager accessJwtAuth = new ProviderManager(
                new JwtAuthenticationProvider(accessJwtDecoder)
        );

        AuthenticationManager refreshJwtAuth = new ProviderManager(
                new JwtAuthenticationProvider(refreshJwtDecoder)
        );

        return (request) -> {
            if (String.valueOf(request.getRequestURL()).contains("refresh")) {
                return refreshJwtAuth;
            } else {
                return accessJwtAuth;
            }
        };
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
