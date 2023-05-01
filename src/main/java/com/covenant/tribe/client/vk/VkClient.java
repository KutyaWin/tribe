package com.covenant.tribe.client.vk;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "vk-client")
public interface VkClient {

    @GetMapping
    ResponseEntity<String> isTokenValid(@SpringQueryMap VkValidationParams params);

}
