package com.covenant.tribe.client.nominatim;

import com.covenant.tribe.client.nominatim.dto.NominatimPlaceDto;
import com.covenant.tribe.client.nominatim.dto.NominatimSearchParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "nominatim-client")
public interface NominatimClient {

    @GetMapping("/search")
    ResponseEntity<NominatimPlaceDto> getAddressFromGeocoding(
            @SpringQueryMap NominatimSearchParams params
    );

}
