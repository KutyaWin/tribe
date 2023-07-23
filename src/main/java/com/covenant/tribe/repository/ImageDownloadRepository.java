package com.covenant.tribe.repository;

import com.covenant.tribe.dto.ImageDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageDownloadRepository {

    ImageDto downloadImage(String url);

}
