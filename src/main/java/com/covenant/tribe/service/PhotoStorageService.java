package com.covenant.tribe.service;

import com.covenant.tribe.dto.ImageDTO;

import java.io.FileNotFoundException;

public interface PhotoStorageService {
    public String saveFileToTmpDir(String contentType, byte[] photo);
    public ImageDTO getEventAvatar(String avatarFileName) throws FileNotFoundException;
}
