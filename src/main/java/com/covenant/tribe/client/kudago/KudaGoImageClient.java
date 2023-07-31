package com.covenant.tribe.client.kudago;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "kudago-image-client")
public interface KudaGoImageClient {

    @GetMapping("/{url_part}")
    ResponseEntity<byte[]> getImage(@PathVariable String url_part);

}
