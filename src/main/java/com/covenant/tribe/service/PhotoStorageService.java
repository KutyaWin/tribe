package com.covenant.tribe.service;

public interface PhotoStorageService {
    public String saveFileToTmpDir(String contentType, byte[] photo);
}
