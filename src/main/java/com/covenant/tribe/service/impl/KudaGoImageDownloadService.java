package com.covenant.tribe.service.impl;

import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.repository.ImageDownloadRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class KudaGoImageDownloadService implements ImageDownloadService {

    ImageDownloadRepository imageDownloadRepository;
    @Override
    public List<ImageDto> downloadImages(List<String> urls) {

        List<ImageDto> images = new ArrayList<>();

        urls.forEach(url -> {
            ImageDto image = imageDownloadRepository.downloadImage(url);
            images.add(image);
        });

        return images;
    }
}
