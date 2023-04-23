package com.covenant.tribe.security;

import com.covenant.tribe.security.introspector.VkTokenIntrospector;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProjectSecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.introspection-uri}")
    String introspectionUri;

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
    String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
    String clientSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(
                j -> j.authenticationManagerResolver(
                        authenticationManagerResolver(jwtDecoder(), opaqueTokenIntrospector())
                )
        );

        http.authorizeHttpRequests()
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
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver(
            JwtDecoder jwtDecoder, OpaqueTokenIntrospector opaqueTokenIntrospector
    ) {
        AuthenticationManager jwtAuth = new ProviderManager(
                new JwtAuthenticationProvider(jwtDecoder)
        );

        AuthenticationManager opaqueAuth = new ProviderManager(
                new OpaqueTokenAuthenticationProvider(opaqueTokenIntrospector)
        );

        return (request) -> {
            if ("jwt".equals(request.getHeader("type"))) {
                return jwtAuth;
            } else {
                return opaqueAuth;
            }
        };
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withJwkSetUri("https://accounts.google.com")
                .build();
    }

    @Bean
    public OpaqueTokenIntrospector opaqueTokenIntrospector() {
        return new VkTokenIntrospector(introspectionUri, clientId, clientSecret);
    }
}
