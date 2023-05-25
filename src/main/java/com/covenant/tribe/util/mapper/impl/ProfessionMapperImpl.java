package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.user.Profession;
import com.covenant.tribe.dto.user.ProfessionDto;
import com.covenant.tribe.util.mapper.ProfessionMapper;
import org.springframework.stereotype.Component;

@Component
public class ProfessionMapperImpl implements ProfessionMapper {
    @Override
    public ProfessionDto mapToProfessionDto(Profession profession) {
        return ProfessionDto.builder()
                .id(profession.getId())
                .professionName(profession.getName())
                .build();
    }
}
