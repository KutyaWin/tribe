package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.client.kudago.dto.KudagoImageDto;
import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.repository.FileStorageRepository;
import com.covenant.tribe.service.ExternalImageStorageService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class KudaGoImageStorageService implements ExternalImageStorageService {

    FileStorageRepository fileStorageRepository;
    @Override
    public List<String> saveExternalImages(List<KudagoEventDto> events) {
        Map<Long, List<String>> eventImages = new HashMap<>();
        for(KudagoEventDto event : events) {
            List<ImageDto> imageUrls = event.getImages().stream()
                    .map(image -> {
                        return ImageDto.builder()
                                .
                    })
                    .toList();
            List<String> imagePaths = fileStorageRepository.saveExternalEventImages(imageUrls);
        }


        return fileStorageRepository.saveExternalEventImages(events);
    }
}
