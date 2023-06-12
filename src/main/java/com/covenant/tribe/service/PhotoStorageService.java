package com.covenant.tribe.service;

import com.covenant.tribe.dto.ImageDto;

import java.io.FileNotFoundException;
import java.util.List;

public interface PhotoStorageService {

    String saveFileToTmpDir(String contentType, byte[] photo);

    ImageDto getEventAvatar(String avatarFileName) throws FileNotFoundException;

    ImageDto getUserAvatar(String fileName) throws FileNotFoundException;

    void deletePhotosInTmpDir(List<String> fileNames);

    List<String> addEventAvatars(List<String> fileNames);
}
