package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.client.kudago.dto.KudagoImageDto;
import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.exeption.storage.FilesNotHandleException;
import com.covenant.tribe.repository.FileStorageRepository;
import com.covenant.tribe.service.ExternalImageStorageService;
import com.covenant.tribe.service.ImageConversionService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Service
@AllArgsConstructor
@Slf4j
public class KudaGoImageStorageService implements ExternalImageStorageService {

    FileStorageRepository fileStorageRepository;
    ImageDownloadService imageDownloadService;
    ImageConversionService imageConversionService;


    @Override
    public Map<Long, List<String>> saveExternalImages(List<KudagoEventDto> events) {
        Map<Long, List<String>> eventImages = new HashMap<>();
        try {
            for (KudagoEventDto event : events) {
                List<String> imageUrls = event.getImages().stream()
                        .map(KudagoImageDto::getImage)
                        .toList();
                List<ImageDto> images = imageDownloadService.downloadImages(imageUrls)
                        .stream()
                        .map(this::processImageDto)
                        .toList();

                List<String> imagePaths = fileStorageRepository.saveExternalEventImages(images);
                eventImages.put(event.getId(), imagePaths);
                Thread.sleep(20);
            }
        } catch (InterruptedException | FeignException e) {
            log.error(e.getMessage());
        }

        return eventImages;
    }


    private ImageDto processImageDto(ImageDto dto) {
        ImageDto processedDto = new ImageDto();
        processedDto.setContentType("image/webp");
        byte[] processedImage;
        try {
            processedImage = imageConversionService.process(dto.getImage(), dto.getContentType());
        } catch (IOException e) {
            String message = String.format("[EXCEPTION] IOException with message: %s", e.getMessage());
            log.error(message, e);
            throw new FilesNotHandleException(message);
        }
        processedDto.setImage(processedImage);
        return processedDto;
    }

}
