package com.covenant.tribe.service.impl;

import com.covenant.tribe.dto.user.ProfessionDto;
import com.covenant.tribe.repository.ProfessionRepository;
import com.covenant.tribe.service.ProfessionService;
import com.covenant.tribe.util.mapper.ProfessionMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfessionServiceImpl implements ProfessionService {

    ProfessionRepository professionRepository;
    ProfessionMapper professionMapper;
    @Transactional(readOnly = true)
    @Override
    public List<ProfessionDto> getAllProfessions() {
        return professionRepository.findAll().stream()
                .map(professionMapper::mapToProfessionDto)
                .collect(Collectors.toList());
    }
}
