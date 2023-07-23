package com.covenant.tribe.service;

import com.covenant.tribe.dto.ImageDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ExternalImageStorageService {

    void saveImage(List<ImageDto> images);

}
