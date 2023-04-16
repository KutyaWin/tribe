package com.covenant.tribe.repository;

import com.covenant.tribe.dto.ImageDTO;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

@Component
public interface FileStorageRepository {
    public String saveFileToTmpDir(String contentType, byte[] photo);
    public ImageDTO getEventAvatarByFileName(String avatarFileName) throws FileNotFoundException;
}
