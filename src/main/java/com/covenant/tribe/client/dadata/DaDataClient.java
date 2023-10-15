package com.covenant.tribe.client.dadata;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingRequest;
import com.covenant.tribe.client.dadata.dto.ReverseGeocodingResponse;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "dadata-client")
public interface DaDataClient {

    @PostMapping("/suggestions/api/4_1/rs/geolocate/address")
    @RateLimiter(name = "dadata")
    ResponseEntity<ReverseGeocodingResponse> getGeolocationData(
            @RequestBody ReverseGeocodingRequest request
    );

}
