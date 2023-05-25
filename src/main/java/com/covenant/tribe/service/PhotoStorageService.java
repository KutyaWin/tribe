package com.covenant.tribe.service;

import com.covenant.tribe.dto.ImageDto;

import java.io.FileNotFoundException;

public interface PhotoStorageService {
    String saveFileToTmpDir(String contentType, byte[] photo);
    ImageDto getEventAvatar(String avatarFileName) throws FileNotFoundException;

    ImageDto getUserAvatar(String fileName) throws FileNotFoundException;
}
