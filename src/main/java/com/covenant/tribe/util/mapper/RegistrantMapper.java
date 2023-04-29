package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.user.Registrant;
import com.covenant.tribe.dto.user.RegistrantRequestDTO;

public interface RegistrantMapper {
    Registrant mapToRegistrant(RegistrantRequestDTO registrantRequestDTO, int verificationCode);
}
