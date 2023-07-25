package com.covenant.tribe.service;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.ImageDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ExternalImageStorageService {

    Map<Long, List<String>> saveExternalImages(List<KudagoEventDto> images);

}
