package com.covenant.tribe.repository;

import com.covenant.tribe.dto.ImageDto;

public interface ImageDownloadRepository {

    ImageDto downloadImage(String url);

}
