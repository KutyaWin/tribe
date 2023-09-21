package com.covenant.tribe.repository.impl;

import com.covenant.tribe.client.kudago.KudaGoImageClient;
import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.exeption.NotFoundException;
import com.covenant.tribe.repository.ImageDownloadRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
@Slf4j
public class KudaGoImageDownloadRepository implements ImageDownloadRepository {

    KudaGoImageClient kudaGoImageClient;
    @Override
    public ImageDto downloadImage(String url) {
        ResponseEntity<byte[]> responseEntity = kudaGoImageClient.getImage(url);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            if (responseEntity.getBody() != null) {
                return ImageDto.builder()
                        .image(responseEntity.getBody())
                        .contentType(
                                String.valueOf(responseEntity.getHeaders().getContentType())
                        )
                        .build();
            } else {
                String errorMsg = """
                Image with url %s not found. Download from kudaGo.
                """.formatted(url);
                log.error(errorMsg);
                throw new NotFoundException(errorMsg);
            }
        }
        String errorMsg = """
                Image with url %s didnt download. Error code: %s.
                """.formatted(url, responseEntity.getStatusCode());
        log.error(errorMsg);
        throw new NotFoundException(errorMsg);
    }
}
