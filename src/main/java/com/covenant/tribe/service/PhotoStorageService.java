package com.covenant.tribe.service;

import com.covenant.tribe.dto.ImageDto;

import java.io.FileNotFoundException;

public interface PhotoStorageService {
    public String saveFileToTmpDir(String contentType, byte[] photo);
    public ImageDto getEventAvatar(String avatarFileName) throws FileNotFoundException;
}
