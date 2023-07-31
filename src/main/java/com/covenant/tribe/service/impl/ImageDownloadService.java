package com.covenant.tribe.service.impl;

import com.covenant.tribe.dto.ImageDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ImageDownloadService {

    List<ImageDto> downloadImages(List<String> urls);

}
