package com.covenant.tribe.service;

import com.covenant.tribe.dto.auth.TokensDTO;
import com.covenant.tribe.dto.user.UserForSignInUpDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    TokensDTO getTokensToUserFromSocialNetworks(String token, String tokenType, UserForSignInUpDTO userForSignInUpDTO) throws JsonProcessingException;
}
