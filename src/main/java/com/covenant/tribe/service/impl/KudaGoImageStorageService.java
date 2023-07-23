package com.covenant.tribe.service.impl;

import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.repository.FileStorageRepository;
import com.covenant.tribe.service.ExternalImageStorageService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class KudaGoImageStorageService implements ExternalImageStorageService {

    FileStorageRepository fileStorageRepository;
    @Override
    public void saveImage(List<ImageDto> images) {

    }
}
