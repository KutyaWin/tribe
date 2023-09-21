package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.user.Profession;
import com.covenant.tribe.dto.user.*;

public interface ProfessionMapper {
    ProfessionDto mapToProfessionDto(Profession profession);
}
