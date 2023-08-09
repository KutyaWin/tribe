package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.user.Registrant;
import com.covenant.tribe.domain.user.RegistrantStatus;
import com.covenant.tribe.dto.auth.RegistrantRequestDTO;
import com.covenant.tribe.util.mapper.RegistrantMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Component
public class RegistrantMapperImpl implements RegistrantMapper {

    PasswordEncoder bCryptPasswordEncoder;
    @Override
    public Registrant mapToRegistrant(RegistrantRequestDTO registrantRequestDTO, int verificationCode) {
        return Registrant.builder()
                .email(registrantRequestDTO.getEmail())
                .password(bCryptPasswordEncoder.encode(registrantRequestDTO.getPassword()))
                .verificationCode(verificationCode)
                .username(registrantRequestDTO.getUsername())
                .status(RegistrantStatus.AWAITED)
                .build();
    }
}
