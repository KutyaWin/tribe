package com.covenant.tribe.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProjectSecurityConfig {

    @Autowired
    JwtProvider jwtProvider;

    @Bean
    @Order(0)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(
                jwt -> jwt
                        .authenticationManagerResolver(
                                authenticationManagerResolver(accessJwtDecoder(), refreshJwtDecoder())
                        )

        );
        http.cors();
        http.csrf(csrfConf -> csrfConf.ignoringRequestMatchers("/api/**"));
//        http.securityMatcher(new AntPathRequestMatcher(""))
//                .authorizeHttpRequests()
        http.securityMatcher("/api/**", "/actuator/**")
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET, "api/v1/tags/**").permitAll()
                .requestMatchers("api/v1/auth/login/**").permitAll()
                .requestMatchers("api/v1/auth/email/password/reset/**").permitAll()
                .requestMatchers("api/v1/auth/registration/email/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/events/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/event/type/circle").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/event/type/info").permitAll()
                .requestMatchers(HttpMethod.POST, "api/v1/unknown-user/interests").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/user/email/check/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/user/username/check/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/user/email/check/**").permitAll()
                .requestMatchers(HttpMethod.GET, "api/v1/user/avatar/**").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().authenticated();

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain swSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                .hasAuthority("swagger_read")
                .anyRequest().authenticated()
                .and()
                .formLogin(form -> {
                    form
                            .loginPage("/sw-login")
                            .loginProcessingUrl("/sw-login")
                            .permitAll();
                });
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://tribual.ru", "https://admin.tribual.ru/", "http://localhost", "http://localhost:3000"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtDecoder accessJwtDecoder() {
        RSAPublicKey publicKey = null;
        publicKey = jwtProvider.getAccessPublicKey();
        return NimbusJwtDecoder
                .withPublicKey(publicKey)
                .build();
    }

    @Bean
    public JwtDecoder refreshJwtDecoder() {
        RSAPublicKey publicKey = jwtProvider.getRefreshPublicKey();
        return NimbusJwtDecoder
                .withPublicKey(publicKey)
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("role");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver(
            JwtDecoder accessJwtDecoder, JwtDecoder refreshJwtDecoder
    ) {
        JwtAuthenticationProvider accessJwtAuthenticationProvider = new JwtAuthenticationProvider(accessJwtDecoder);
        accessJwtAuthenticationProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter());
        AuthenticationManager accessJwtAuth = new ProviderManager(
                accessJwtAuthenticationProvider
        );

        AuthenticationManager refreshJwtAuth = new ProviderManager(
                new JwtAuthenticationProvider(refreshJwtDecoder)
        );

        AuthenticationManager userPasswordAuth = new ProviderManager(
                new DaoAuthenticationProvider()
        );


        return (request) -> {
            if (String.valueOf(request.getRequestURL()).contains("refresh")) {
                return refreshJwtAuth;
            } else if (String.valueOf(request.getRequestURL()).contains("swagger")) {
                return userPasswordAuth;
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
