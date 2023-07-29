package com.covenant.tribe.service.impl;

import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.repository.ImageDownloadRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KudaGoImageDownloadService implements ImageDownloadService {

    final ImageDownloadRepository imageDownloadRepository;
    final String KUDAGO_IMAGE_HOST;

    public KudaGoImageDownloadService(
            ImageDownloadRepository imageDownloadRepository,
            @Value("spring.cloud.openfeign.client.config.kudago-image-client.url")
            String kudagoImageHost) {
        this.imageDownloadRepository = imageDownloadRepository;
        this.KUDAGO_IMAGE_HOST = kudagoImageHost;
    }

    @Override
    public List<ImageDto> downloadImages(List<String> urls) {

        List<ImageDto> images = new ArrayList<>();

        urls.forEach(url -> {
            String urlWithoutHost = url.substring(url.indexOf("/images"));
            ImageDto image = imageDownloadRepository.downloadImage(urlWithoutHost);
            images.add(image);
        });

        return images;
    }
}
