package com.covenant.tribe.repository;

import com.covenant.tribe.dto.ImageDTO;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Component
public interface FileStorageRepository {
    public String saveFileToTmpDir(String contentType, byte[] photo);
    public ImageDTO getEventAvatarByFileName(String avatarFileName) throws FileNotFoundException;

    public List<String> addEventImages(List<String> fileNames) throws IOException;

    public void deleteUnnecessaryAvatars(List<String> fileNames) throws IOException;
}
