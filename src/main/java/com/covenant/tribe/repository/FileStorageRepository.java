package com.covenant.tribe.repository;

import com.covenant.tribe.dto.ImageDto;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Component
public interface FileStorageRepository {
    public String saveFileToTmpDir(String contentType, byte[] photo);

    public ImageDto getEventAvatarByFileName(String avatarFileName) throws FileNotFoundException;

    public List<String> addEventAvatars(List<String> fileNames) throws IOException;

    public void deleteFileInTmpDir(List<String> fileNames) throws IOException;
    public void deleteEventAvatars(List<String> fileNames) throws IOException;

    ImageDto getUserAvatarByFileName(String fileName) throws FileNotFoundException;

    String addUserAvatar(String fileNameForAdding) throws IOException;

    String getRectangleAnimationJson(String fileName) throws IOException;

    String getCircleAnimationJson(String fileName) throws IOException;
}
