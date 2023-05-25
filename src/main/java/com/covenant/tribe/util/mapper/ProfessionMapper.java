package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.Profession;
import com.covenant.tribe.domain.user.Registrant;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.auth.ConfirmRegistrationDTO;
import com.covenant.tribe.dto.user.*;

import java.util.Set;

public interface ProfessionMapper {
    ProfessionDto mapToProfessionDto(Profession profession);
}
