package com.covenant.tribe.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface ImageConversionService {
    byte[] convertToWebpWithLosslessCompression(byte[] data) throws IOException;
    byte[] resize(byte[] data, String format, int scaledWidth, int scaledHeight,
                  boolean maintainAspectRatio) throws IOException;
    byte[] process(byte[] inputData, String mimeType) throws IOException;

}
