package com.covenant.tribe.service;

import com.covenant.tribe.dto.user.ProfessionDto;
import org.springframework.stereotype.Service;

import java.util.List;


@Service

public interface ProfessionService {
    List<ProfessionDto> getAllProfessions();
}
