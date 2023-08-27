package com.covenant.tribe.service.impl;

import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.repository.ImageDownloadRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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

        for (int i = 0; i < urls.size(); i++) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
            if (i == 1) {
                break;
            }
            String url = urls.get(i);
            String urlWithoutHost = url.substring(url.indexOf("/images"));
            ImageDto image = imageDownloadRepository.downloadImage(urlWithoutHost);
            images.add(image);
        }
        return images;
    }
}
