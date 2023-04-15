package com.covenant.tribe.repository;

import org.springframework.stereotype.Component;

@Component
public interface FileStorageRepository {
    public String saveFileToTmpDir(String contentType, byte[] photo);
}
