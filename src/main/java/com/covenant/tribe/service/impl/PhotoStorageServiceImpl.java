package com.covenant.tribe.service.impl;

import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.exeption.storage.FilesNotHandleException;
import com.covenant.tribe.repository.FileStorageRepository;
import com.covenant.tribe.service.PhotoStorageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

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

    @Override
    public void deletePhotosInTmpDir(List<String> fileNames) {
        try {
            fileStorageRepository.deleteFileInDir(fileNames);
        } catch (IOException e) {
            String message = String.format("[EXCEPTION] IOException with message: %s", e.getMessage());
            log.error(message, e);
            throw new FilesNotHandleException(message);
        }
    }

    @Override
    public List<String> addEventAvatars(List<String> fileNames) {
        try {
            return fileStorageRepository.addEventAvatars(fileNames);
        } catch (IOException e) {
            String message = String.format("[EXCEPTION] IOException with message: %s", e.getMessage());
            log.error(message, e);
            throw new FilesNotHandleException(message);
        }
    }
}
