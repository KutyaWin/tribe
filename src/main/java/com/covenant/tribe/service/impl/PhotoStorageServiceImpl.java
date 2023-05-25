package com.covenant.tribe.service.impl;

import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.repository.FileStorageRepository;
import com.covenant.tribe.service.PhotoStorageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

@Service
@Slf4j
@AllArgsConstructor
public class PhotoStorageServiceImpl implements PhotoStorageService {

    FileStorageRepository fileStorageRepository;
    @Override
    public String saveFileToTmpDir(String contentType, byte[] image) {
        return fileStorageRepository.saveFileToTmpDir(contentType, image);
    }

    @Override
    public ImageDto getEventAvatar(String avatarFileName) throws FileNotFoundException {
        return fileStorageRepository.getEventAvatarByFileName(avatarFileName);
    }

    @Override
    public ImageDto getUserAvatar(String fileName) throws FileNotFoundException {
        return fileStorageRepository.getUserAvatarByFileName(fileName);
    }
}
