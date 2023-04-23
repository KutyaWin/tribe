package com.covenant.tribe.security.introspector;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class VkTokenIntrospector extends SpringOpaqueTokenIntrospector {

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.api-version}")
    String apiVersion;

    private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP =
            new ParameterizedTypeReference<Map<String, Object>>() {};
    private String introspectionUri;
    private String clientSecret;


    public VkTokenIntrospector(String introspectionUri, String clientId, String clientSecret) {
        super(introspectionUri, clientId, clientSecret);
        this.introspectionUri = introspectionUri;
        this.clientSecret = clientSecret;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(introspectionUri)
                .queryParam("token", token)
                .queryParam("access_token", clientSecret)
                .queryParam("v", apiVersion    );
        String uriString = builder.build().toUriString();
        RequestEntity<?> requestEntity = new RequestEntity<>(HttpMethod.GET, URI.create(uriString));
        ResponseEntity<Map<String, Object>> responseEntity = makeRequest(requestEntity);

        if (responseEntity.getStatusCode() != HttpStatus.OK)  {
            log.error("Error ");
            throw new OAuth2IntrospectionException(
                    "Introspection endpoint responded with " + responseEntity.getStatusCode());
        }

        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseBody == null) {
            throw new OAuth2IntrospectionException("Introspection body was null");
        }

        Map<String, Object> claims;

        if (responseBody.containsKey("response")) {
            claims = (Map<String, Object>) responseBody.get("response");
        } else if (responseBody.containsKey("error")) {
            Map<String, Object> error = (Map<String, Object>) responseBody.get("error");
            Integer errorCode = (Integer) error.get("error_code");
            String errorMessage = String.valueOf(error.get("error_msg"));
            throw new OAuth2IntrospectionException(
                    "Introspection endpoint responded with error " + errorMessage + "with code " + errorCode);
        } else {
            throw new OAuth2IntrospectionException(
                    "The body of vk response don't contains neither a response nor an error");
        }

        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = convertClaimsSet(claims);

        if (oAuth2AuthenticatedPrincipal.getAttributes().get("success").equals(0)) {
            throw new OAuth2IntrospectionException(
                    "Introspection endpoint responded with " + responseEntity.getStatusCode());
        }
        return oAuth2AuthenticatedPrincipal;
    }

    private OAuth2AuthenticatedPrincipal convertClaimsSet(Map<String, Object> claims) {
        Map<String, Object> claimsForPrincipal = new HashMap<>();
        Number success = (Number) claims.get("success");
        String userId = String.valueOf(claims.get("user_id"));
        Instant tokenCreated = Instant.ofEpochSecond(((Number) claims.get("date")).longValue());
        Instant expire = Instant.ofEpochSecond(((Number) claims.get("expire")).longValue());
        if (expire.compareTo(Instant.now()) <= 0) throw new OAuth2IntrospectionException("Provided token is expired"); ;
        claimsForPrincipal.put("exp", expire);
        claimsForPrincipal.put("iat", tokenCreated);
        claimsForPrincipal.put("sub", userId);
        claimsForPrincipal.put("success", success);
        return new OAuth2IntrospectionAuthenticatedPrincipal(claimsForPrincipal, List.of());
    }

    private ResponseEntity<Map<String, Object>> makeRequest(RequestEntity<?> requestEntity) {
        try {
            return new RestTemplate().exchange(requestEntity, STRING_OBJECT_MAP);
        }
        catch (Exception ex) {
            throw new OAuth2IntrospectionException(ex.getMessage(), ex);
        }
    }
}
