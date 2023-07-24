package com.covenant.tribe.service;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.ImageDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ExternalImageStorageService {

    List<String> saveExternalImages(List<KudagoEventDto> images);

}
