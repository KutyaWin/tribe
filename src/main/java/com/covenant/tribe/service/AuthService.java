package com.covenant.tribe.service;

import com.covenant.tribe.dto.auth.*;
import com.covenant.tribe.dto.user.UserForSignInUpDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    TokensDTO getTokensToUserFromSocialNetworks(String token, String tokenType, UserForSignInUpDTO userForSignInUpDTO) throws JsonProcessingException;
    TokensDTO refreshTokens(String token);
    RegistrantResponseDTO addRegistrantWithEmail(RegistrantRequestDTO registrantRequestDTO);
    TokensDTO confirmEmailRegistration(ConfirmRegistrationDTO confirmRegistrationDTO);

    TokensDTO loginUserWithEmail(EmailLoginDTO emailLoginDTO);

    void sendResetCodeToEmail(ResetPasswordDTO resetPasswordDTO);

    void changePassword(ChangePasswordDTO changePasswordDTO);

    TokensDTO confirmResetCode(ConfirmCodeDTO confirmResetCodeDTO);
}
